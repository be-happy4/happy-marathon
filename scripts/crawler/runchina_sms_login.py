"""Interactive runchina SMS login using Playwright.

The flow:
1. Open runchina in Playwright (solves anti-crawling)
2. Navigate to login/member page
3. Find phone input, fill it, send SMS
4. Ask user for code, login
5. Capture JWT token
6. Use token for API crawling
"""
import json, sys, time
from playwright.sync_api import sync_playwright

PHONE = "18058255813"


def run():
    with sync_playwright() as p:
        browser = p.chromium.launch(headless=True)
        context = browser.new_context(
            user_agent="Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36"
        )
        page = context.new_page()

        # Collect all API responses
        captured_tokens = []
        api_data = []

        def on_request(request):
            url = request.url
            if "api-changzheng" in url:
                headers = dict(request.headers)
                # Check for any auth token in headers
                for k, v in headers.items():
                    if k.lower() in ["token", "authorization", "x-token", "x-auth"]:
                        captured_tokens.append({k: v, "url": url})
                        print(f"[TOKEN FOUND] {k}: {v[:50]}... in request to {url.split('/api/')[1] if '/api/' in url else url}")

        def on_response(response):
            url = response.url
            if "api-changzheng" in url:
                try:
                    body = response.json()
                    path = url.split("/api/")[1] if "/api/" in url else url
                    print(f"[RESP] {path}: {json.dumps(body, ensure_ascii=False)[:300]}")

                    # Check if response contains token
                    if isinstance(body, dict):
                        for k in ["token", "accessToken", "access_token", "Token"]:
                            if k in str(body):
                                print(f">>> TOKEN IN RESPONSE: {k}")
                                captured_tokens.append(body)

                    api_data.append({"path": path, "data": body})
                except:
                    pass

        page.on("request", on_request)
        page.on("response", on_response)

        # Navigate to runchina
        print("=== Loading runchina ===")
        try:
            page.goto("https://www.runchina.org.cn/", wait_until="load", timeout=20000)
        except:
            pass
        page.wait_for_timeout(5000)

        # Try various routes that might have login
        print("\n=== Trying to find login page ===")
        login_routes = [
            "/#/login",
            "/#/user/login",
            "/#/member/login",
            "/#/personal",
            "/#/my",
            "/#/mine",
            "/#/user",
        ]

        found_login = False
        for route in login_routes:
            try:
                page.goto(f"https://www.runchina.org.cn{route}", wait_until="load", timeout=10000)
                page.wait_for_timeout(3000)

                # Check for phone input
                phone_inputs = page.locator("input[type='tel'], input[type='number'], input[placeholder*='手机'], input[placeholder*='电话'], input[placeholder*='phone'], input[name*='phone'], input[name*='mobile']")
                if phone_inputs.count() > 0:
                    print(f"Found phone input on {route}!")
                    found_login = True
                    break

                # Check for login buttons
                login_btns = page.locator("button:has-text('登录'), span:has-text('登录'), div:has-text('登录')")
                if login_btns.count() > 0:
                    print(f"Found login button on {route}")
                    found_login = True
                    break

            except Exception as e:
                print(f"  {route}: error - {e}")

        if not found_login:
            print("\nNo login page found via routes. Checking page content...")
            html = page.content()
            # Search for login-related text
            if "登录" in html:
                print("'登录' found in page HTML")
            if "手机号" in html:
                print("'手机号' found in page HTML")

            # Try clicking on elements that might open login
            for text in ["登录", "注册", "Login", "个人中心", "我的"]:
                el = page.locator(f"text={text}").first
                if el.count() > 0:
                    try:
                        print(f"Clicking '{text}'...")
                        el.click()
                        page.wait_for_timeout(3000)
                    except:
                        pass

        # After all attempts, get current page state
        page.wait_for_timeout(2000)
        html = page.content()

        # Save screenshot for debugging
        page.screenshot(path="/tmp/runchina_debug.png")
        print(f"\nSaved debug screenshot to /tmp/runchina_debug.png")

        # Check for any text inputs and buttons
        inputs = page.locator("input:visible")
        btn_count = inputs.count()
        print(f"\nVisible inputs: {btn_count}")
        for i in range(min(btn_count, 10)):
            try:
                tp = inputs.nth(i).get_attribute("type") or "text"
                ph = inputs.nth(i).get_attribute("placeholder") or ""
                nm = inputs.nth(i).get_attribute("name") or ""
                print(f"  input[{i}]: type={tp}, placeholder='{ph}', name='{nm}'")
            except:
                pass

        buttons = page.locator("button:visible")
        btn_count = buttons.count()
        print(f"\nVisible buttons: {btn_count}")
        for i in range(min(btn_count, 10)):
            try:
                text = buttons.nth(i).inner_text().strip()[:40]
                print(f"  button[{i}]: '{text}'")
            except:
                pass

        # Print all captured API data
        print(f"\n=== Captured {len(api_data)} API responses ===")
        for d in api_data:
            print(f"  {d['path']}: {json.dumps(d['data'], ensure_ascii=False)[:200]}")

        if captured_tokens:
            print(f"\n=== Captured tokens: {len(captured_tokens)} ===")
            for t in captured_tokens:
                print(f"  {json.dumps(t, ensure_ascii=False)[:200]}")

        # Save storage state for next step
        context.storage_state(path="/tmp/runchina_storage.json")
        browser.close()

        print("\n=== Done ===")
        print("Next: review the debug screenshot and tell me what you see.")
        print("Or if a login form is visible, I can interact with it.")


if __name__ == "__main__":
    run()
