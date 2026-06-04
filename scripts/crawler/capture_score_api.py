#!/usr/bin/env python3
"""
打开可见浏览器，拦截 runchina 成绩查询 API 调用。
修复：以「用户点击查询之后」为分界线，只捕获点击后出现的成绩相关 API。

用法: python3 capture_score_api.py
"""
from playwright.sync_api import sync_playwright
import json, time

OUTPUT_FILE = "scripts/crawler/captured_api.json"
SKIP_PATTERNS = ["searchList", "searchContent", "recentMatch", "img.", ".jpg", ".png", ".gif"]

def main():
    print("=" * 60)
    print("🚀 浏览器已打开，表单预填: 宋一得 / 330283200004010017")
    print("   1) 完成滑块验证码")
    print("   2) 点击「查询」按钮")
    print("   3) 等待结果出现后按 Enter 关闭")
    print("=" * 60)

    # All captured APIs (timestamp → data)
    all_apis = []
    query_click_time = None

    with sync_playwright() as p:
        # Emulate iPhone 15 Pro Max (closest to 17PM: 430×932 @3x)
        # Use Playwright's own Chromium as mobile browser
        iphone = p.devices["iPhone 15 Pro Max"]
        browser = p.chromium.launch(
            headless=False,
            args=["--disable-blink-features=AutomationControlled"],
        )
        ctx = browser.new_context(
            viewport={"width": 430, "height": 932},
            device_scale_factor=3,
            is_mobile=True,
            has_touch=True,
            user_agent="Mozilla/5.0 (iPhone; CPU iPhone OS 18_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) EdgiOS/131.0.0.0 Mobile/15E148 Safari/605.1.15",
            locale="zh-CN",
        )
        page = ctx.new_page()
        page.add_init_script(
            "Object.defineProperty(navigator, 'webdriver', { get: () => false });"
        )

        def on_request(req):
            if "changzheng" in req.url and req.method == "POST":
                try:
                    body = json.loads(req.post_data) if req.post_data else None
                except:
                    body = req.post_data
                all_apis.append({
                    "time": time.time(),
                    "method": req.method,
                    "url": req.url,
                    "body": body,
                    "headers": dict(req.headers),
                })

        def on_response(resp):
            url = resp.url
            if "changzheng" in url:
                for api in reversed(all_apis):
                    if api["url"] == url:
                        try:
                            ct = resp.headers.get("content-type", "")
                            if "json" in ct:
                                api["response"] = resp.json()
                            else:
                                api["response"] = resp.text()[:500]
                        except:
                            api["response"] = f"<{resp.status}>"
                        break

        page.on("request", on_request)
        page.on("response", on_response)

        # ----- Navigate & pre-fill -----
        try:
            page.goto("https://www.runchina.org.cn/", wait_until="domcontentloaded", timeout=30000)
        except:
            pass
        page.wait_for_timeout(3000)
        try:
            page.goto("https://www.runchina.org.cn/#/data-score/public-score/list", wait_until="domcontentloaded", timeout=30000)
        except:
            pass
        page.wait_for_timeout(8000)

        print("📝 请手动填写表单（姓名: 宋一得，证件号: 330283200004010017）")

        # ----- Mark time: user starts interacting now -----
        query_click_time = time.time()
        print("⏳ 请完成验证码 → 点击「查询」→ 看到结果后按 Enter...")

        try:
            input("\n按 Enter 结束...")
        except EOFError:
            time.sleep(180)  # fallback timeout

        # ----- Analyze: only APIs AFTER query_click_time -----
        post_click_apis = [
            a for a in all_apis
            if a["time"] >= query_click_time
            and not any(skip in a["url"] for skip in SKIP_PATTERNS)
        ]

        print("\n" + "=" * 60)
        if post_click_apis:
            print(f"📡 点击后捕获到 {len(post_click_apis)} 个非 CMS API 调用:")

            # Find the most likely score query API
            score_api = None
            for api in post_click_apis:
                url_lower = api["url"].lower()
                if "searchList" in url_lower and isinstance(api.get("body"), dict):
                    body = api["body"]
                    # searchList with pageTitle=6 or pageType=3 might be the score query
                    if body.get("pageTitle") == 6 or body.get("pageType") == 3:
                        score_api = api
                        break
                elif any(k in url_lower for k in ["score", "cert", "athlete", "result", "query"]):
                    score_api = api
                    break

            if not score_api:
                score_api = post_click_apis[-1]  # last resort: newest API

            if score_api:
                print(f"\n🎯 疑似成绩查询 API:")
                print(f"   URL: {score_api['url']}")
                if score_api.get("body"):
                    print(f"   BODY: {json.dumps(score_api['body'], ensure_ascii=False)}")
                if score_api.get("response"):
                    resp = score_api["response"]
                    if isinstance(resp, dict):
                        print(f"   RESPONSE code: {resp.get('code')}")
                        data = resp.get("data")
                        if isinstance(data, list):
                            print(f"   RECORDS: {len(data)}")
                            for item in data[:2]:
                                print(f"     {json.dumps(item, ensure_ascii=False)[:200]}")
                        elif isinstance(data, dict):
                            for k, v in data.items():
                                if isinstance(v, list):
                                    print(f"   {k}: {len(v)} records")
                                    for item in v[:1]:
                                        print(f"     {json.dumps(item, ensure_ascii=False)[:200]}")

            # Save everything
            output = {
                "score_query_api": score_api,
                "all_post_click_apis": post_click_apis,
                "collected_at": time.strftime("%Y-%m-%d %H:%M:%S"),
            }
        else:
            print("⚠️  点击后没有捕获到新的 API 调用")
            print("   可能原因: 验证码未完成 / 未点击查询 / 网络问题")
            output = {"error": "no post-click APIs captured", "all_apis": all_apis}

        with open(OUTPUT_FILE, "w", encoding="utf-8") as f:
            json.dump(output, f, ensure_ascii=False, indent=2)
        print(f"\n💾 已保存: {OUTPUT_FILE}")

        browser.close()


if __name__ == "__main__":
    main()
