#!/usr/bin/env python3
"""Query personal marathon results from runchina.org.cn public score page.

Usage:
    python3 personal_score_query.py --name 张三 --id-card 110101199001011234
    python3 personal_score_query.py --name 张三 --id-card 110101199001011234 --headless

Output (stdout): JSON with one of these shapes:
    {"status": "OK", "results": [...]}
    {"status": "CAPTCHA_REQUIRED", "message": "..."}
    {"status": "NO_RESULT", "message": "..."}
    {"status": "ERROR", "message": "..."}
"""

import argparse
import json
import sys
import time
from playwright.sync_api import sync_playwright


API_BASE = "api-changzheng.chinaath.com"
HOMEPAGE = "https://www.runchina.org.cn/"
SCORE_PAGE = "https://www.runchina.org.cn/#/data-score/public-score/list"


def main():
    parser = argparse.ArgumentParser(description="Query personal marathon results")
    parser.add_argument("--name", required=True, help="Athlete name")
    parser.add_argument("--id-card", default=None, help="ID card number")
    parser.add_argument("--phone", default=None, help="Phone number")
    parser.add_argument("--headless", action="store_true", help="Run browser in headless mode")
    parser.add_argument("--timeout", type=int, default=60, help="Total timeout seconds")
    args = parser.parse_args()

    try:
        query(args.name, args.id_card, args.headless, args.timeout)
    except Exception as e:
        print(json.dumps({"status": "ERROR", "message": str(e)}, ensure_ascii=False))
        sys.exit(1)


def query(name: str, id_card: str | None, headless: bool, timeout_sec: int):
    captured_apis = []

    with sync_playwright() as p:
        browser = p.chromium.launch(
            headless=headless,
            args=[
                "--no-sandbox",
                "--disable-blink-features=AutomationControlled",
                "--disable-features=IsolateOrigins,site-per-process",
            ],
        )
        context = browser.new_context(
            user_agent="Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36",
            viewport={"width": 1440, "height": 900},
            locale="zh-CN",
        )
        page = context.new_page()

        # Anti-detection
        page.add_init_script("""
            delete Object.getPrototypeOf(navigator).webdriver;
            Object.defineProperty(navigator, 'plugins', { get: () => [1, 2, 3, 4, 5] });
            window.chrome = { runtime: {}, app: {} };
        """)

        def on_response(response):
            url = response.url
            ct = response.headers.get("content-type", "")
            if ct and "json" in ct and API_BASE in url:
                try:
                    body = response.json()
                    captured_apis.append({"url": url, "status": response.status, "body": body})
                    preview = json.dumps(body, ensure_ascii=False)[:300]
                    path = url.split("/api/")[-1] if "/api/" in url else url[-100:]
                    print(f"[API] {response.status} {path}: {preview}", file=sys.stderr)
                except Exception:
                    pass

        page.on("response", on_response)

        # Step 1: Load homepage (CDN check)
        print("=== Loading homepage ===", file=sys.stderr)
        try:
            page.goto(HOMEPAGE, wait_until="domcontentloaded", timeout=30000)
        except Exception as e:
            print(f"Homepage load warning: {e}", file=sys.stderr)
        page.wait_for_timeout(5000)

        # Step 2: Navigate to score query page
        print("=== Loading score page ===", file=sys.stderr)
        try:
            page.goto(SCORE_PAGE, wait_until="domcontentloaded", timeout=30000)
        except Exception as e:
            print(f"Score page load warning: {e}", file=sys.stderr)
        page.wait_for_timeout(8000)

        # Step 3: Fill form
        print(f"=== Filling form: name={name} ===", file=sys.stderr)
        _fill_placeholder(page, "姓名", name)
        if id_card:
            _fill_placeholder(page, "证件号码", id_card)
        page.wait_for_timeout(1000)

        # Step 4: Check for captcha
        if _has_captcha(page):
            print(json.dumps({
                "status": "CAPTCHA_REQUIRED",
                "message": "Slider captcha detected on the query page"
            }, ensure_ascii=False))
            browser.close()
            return

        # Step 5: Click query button
        print("=== Clicking query ===", file=sys.stderr)
        _click_button(page, "查询")
        page.wait_for_timeout(10000)

        browser.close()

    # Step 6: Parse API responses
    results = _parse_results(captured_apis)

    if not results:
        print(json.dumps({
            "status": "NO_RESULT",
            "message": "No results found in captured API responses"
        }, ensure_ascii=False))
        return

    print(json.dumps({"status": "OK", "results": results}, ensure_ascii=False, default=str))


def _fill_placeholder(page, placeholder: str, value: str):
    try:
        el = page.locator(f"input[placeholder*='{placeholder}']").first
        if el.count() > 0:
            el.fill(value)
            print(f"  Filled '{placeholder}'", file=sys.stderr)
    except Exception as e:
        print(f"  Fill '{placeholder}' error: {e}", file=sys.stderr)


def _has_captcha(page) -> bool:
    selectors = [
        "iframe[src*=captcha]", "iframe[src*=ncaptcha]", "iframe[src*=aliyun]",
        ".nc_wrapper", ".nc-container", "#nc_1_n1z",
        "[class*=captcha]", "[id*=captcha]", "#tcaptcha_iframe",
    ]
    for sel in selectors:
        try:
            if page.locator(sel).count() > 0:
                print(f"  Captcha found: {sel}", file=sys.stderr)
                return True
        except Exception:
            pass
    return False


def _click_button(page, text: str):
    for sel in [f"button:has-text('{text}')", f"span:has-text('{text}')", f"div:has-text('{text}')"]:
        try:
            el = page.locator(sel).first
            if el.count() > 0:
                el.click()
                print(f"  Clicked '{text}'", file=sys.stderr)
                return
        except Exception:
            pass
    page.keyboard.press("Enter")
    print("  Pressed Enter (no button found)", file=sys.stderr)


def _parse_results(captured_apis: list) -> list:
    """Extract score records from captured API responses."""
    results = []
    for api in captured_apis:
        body = api.get("body", {})
        if not isinstance(body, dict):
            continue

        code = body.get("code")
        if code is not None and int(code) not in (0, 200):
            continue

        data = body.get("data")
        if data is None:
            continue

        # Handle both array and object with rows/results/list
        rows = []
        if isinstance(data, list):
            rows = data
        elif isinstance(data, dict):
            for key in ("results", "rows", "records", "list", "data"):
                arr = data.get(key)
                if isinstance(arr, list):
                    rows = arr
                    break
            if not rows:
                rows = [data]  # single record

        for row in rows:
            if not isinstance(row, dict):
                continue
            result = _map_row(row)
            if result:
                results.append(result)

    return results


def _map_row(row: dict) -> dict | None:
    name = (
        row.get("name")
        or row.get("realName")
        or row.get("athleteName")
        or row.get("playerName")
    )
    if not name:
        return None

    return {
        "name": name,
        "id_card": (
            row.get("idCard")
            or row.get("idNumber")
            or row.get("certNo")
        ),
        "bib_number": (
            row.get("bibNumber")
            or row.get("bibNo")
            or row.get("raceNumber")
        ),
        "gender": row.get("gender") or row.get("sex"),
        "nationality": row.get("nationality") or row.get("nation"),
        "game_name": (
            row.get("raceName")
            or row.get("competitionName")
            or row.get("matchName")
            or row.get("eventName")
        ),
        "game_date": (
            row.get("raceTime")
            or row.get("raceDate")
            or row.get("competitionDate")
            or row.get("matchDate")
        ),
        "gun_time": (
            row.get("gunTime") or row.get("gunScore") or row.get("grossTime")
        ),
        "net_time": (
            row.get("netTime") or row.get("netScore") or row.get("chipTime")
        ),
        "rank": (
            row.get("rank") or row.get("overallRank") or row.get("totalRank")
        ),
        "gender_rank": row.get("genderRank") or row.get("sexRank"),
        "category_rank": row.get("categoryRank") or row.get("groupRank"),
        "age_group": row.get("ageGroup") or row.get("groupName"),
    }


if __name__ == "__main__":
    main()
