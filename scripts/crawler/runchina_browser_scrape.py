"""Scrape runchina.org.cn using Playwright - v6.

Interact with the SPA search page to trigger and intercept API calls.
"""
import json, time
from playwright.sync_api import sync_playwright


def run():
    with sync_playwright() as p:
        browser = p.chromium.launch(headless=True)
        context = browser.new_context(
            user_agent="Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36"
        )
        page = context.new_page()

        all_responses = []

        def on_response(response):
            url = response.url
            if "api-changzheng" in url:
                try:
                    body = response.text()
                    ct = response.headers.get("content-type", "")
                    if "json" in ct or "application" in ct or url.endswith("json"):
                        data = json.loads(body)
                        path = url.split("/api/")[1] if "/api/" in url else url
                        print(f"[API RESP] {path}")
                        preview = json.dumps(data, ensure_ascii=False)
                        if len(preview) > 600:
                            preview = preview[:600] + "..."
                        print(f"  {preview}")
                        all_responses.append({"path": path, "data": data})
                except:
                    pass

        page.on("response", on_response)

        # Also log XHR/fetch requests
        def on_request(request):
            url = request.url
            if "api-changzheng" in url:
                print(f"[API REQ] {request.method} {url}")

        page.on("request", on_request)

        # Navigate to the race search page (this is the SPA route for searching races)
        print("=== Loading race search page ===")
        try:
            page.goto("https://www.runchina.org.cn/#/match/search", wait_until="load", timeout=20000)
        except:
            pass

        # Wait for SPA to mount
        page.wait_for_timeout(8000)

        html = page.content()
        print(f"Page HTML: {len(html)} bytes")
        print(f"Responses so far: {len(all_responses)}")

        # Dump the page to find interactive elements
        print("\n=== Page elements ===")
        all_elements = page.locator("*")
        total_elements = all_elements.count()
        print(f"Total DOM elements: {total_elements}")

        # Find inputs, buttons, links
        for selector, label in [
            ("input", "input"),
            ("button", "button"),
            ("select", "select"),
            ("a", "link"),
            ("[class*=search]", "search-related"),
            ("[class*=match]", "match-related"),
            ("[class*=race]", "race-related"),
        ]:
            els = page.locator(selector)
            c = els.count()
            if c > 0 and c < 30:
                print(f"\n{label} ({c}):")
                for i in range(min(c, 10)):
                    try:
                        text = els.nth(i).inner_text().strip()[:50] if els.nth(i).is_visible() else "(hidden)"
                        cls = els.nth(i).get_attribute("class") or ""
                        placeholder = els.nth(i).get_attribute("placeholder") or ""
                        tag = els.nth(i).evaluate("el => el.tagName")
                        tp = els.nth(i).get_attribute("type") or ""
                        info = f"<{tag} class='{cls[:60]}' type='{tp}'> {text}"
                        if placeholder:
                            info += f" placeholder='{placeholder}'"
                        print(f"  [{i}] {info}")
                    except:
                        pass
            elif c >= 30:
                print(f"\n{label}: {c} elements (too many)")

        # Try clicking the search button and see what API calls happen
        print("\n=== Trying interactions ===")

        # Find and click search button
        search_buttons = page.locator("button:has-text('搜索'), button:has-text('Search'), button:has-text('查询')")
        sb_count = search_buttons.count()
        print(f"Search buttons: {sb_count}")

        if sb_count > 0:
            print("Clicking search button...")
            try:
                search_buttons.first.click()
            except:
                pass
            page.wait_for_timeout(5000)
            print(f"Responses after search click: {len(all_responses)}")

        # Also try pressing Enter in a search input
        search_inputs = page.locator("input[placeholder*='赛'], input[placeholder*='搜索'], input[name*='search'], input[name*='match']")
        si_count = search_inputs.count()
        print(f"Search inputs: {si_count}")

        if si_count > 0:
            try:
                search_inputs.first.fill("马拉松")
                search_inputs.first.press("Enter")
                page.wait_for_timeout(5000)
                print(f"Responses after entering search: {len(all_responses)}")
            except Exception as e:
                print(f"Error: {e}")

        # Try to trigger page scrolling to load more data
        print("\n=== Scrolling page ===")
        for i in range(3):
            page.evaluate(f"window.scrollTo(0, {i * 1000})")
            page.wait_for_timeout(1000)

        page.wait_for_timeout(3000)
        print(f"Responses after scrolling: {len(all_responses)}")

        # Navigate to different routes
        for route_path in [
            "/#/match",
            "/#/race",
            "/#/event/list",
            "/#/certification",
        ]:
            try:
                print(f"\nNavigating to {route_path}...")
                page.goto(f"https://www.runchina.org.cn{route_path}", wait_until="load", timeout=10000)
                page.wait_for_timeout(4000)
                print(f"Responses: {len(all_responses)}")
            except:
                pass

        # Print all unique API paths found
        seen = set()
        print(f"\n=== All API calls captured ({len(all_responses)} total) ===")
        for r in all_responses:
            path = r["path"]
            if path not in seen:
                seen.add(path)
                d = r["data"]
                success = d.get("success")
                data_type = type(d.get("data")).__name__ if "data" in d else "N/A"
                data_len = len(d.get("data", [])) if isinstance(d.get("data"), list) else (
                    d["data"].get("total", "?") if isinstance(d.get("data"), dict) else "?"
                )
                print(f"  {path}: success={success}, data_len={data_len}")

        if not all_responses:
            print("No API calls were captured!")
            print("\nThis confirms: the runchina SPA does NOT make API calls on page load.")
            print("The data is likely rendered server-side or loaded via a different mechanism.")
            print("The only working public endpoints are recentMatch and searchList.")

        browser.close()


if __name__ == "__main__":
    run()
