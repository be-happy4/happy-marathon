#!/usr/bin/env python3
"""
runchina.org.cn API 探测脚本（原型验证用）。
探测 changzheng API 网关的可达性和端点。

结论:
  - api-changzheng.chinaath.com 可直连（无 CDN 反爬）
  - /homePage/official/* 公开访问（CMS 内容）
  - /race/*, /match/*, /calendar/* 需 Token
  - Token 来自数字心动 APP 阿里云号码认证，无法服务器模拟
"""
import urllib.request, ssl, json, sys

ctx = ssl.create_default_context()
ctx.check_hostname = False
ctx.verify_mode = ssl.CERT_NONE

API = "https://api-changzheng.chinaath.com/changzheng-content-center-api/api"

def probe(path, data=None):
    try:
        body = json.dumps(data or {}).encode()
        req = urllib.request.Request(f"{API}{path}", data=body,
            headers={'Content-Type': 'application/json', 'User-Agent': 'Mozilla/5.0'},
            method='POST')
        with urllib.request.urlopen(req, context=ctx, timeout=5) as resp:
            r = json.loads(resp.read())
            return r
    except Exception as e:
        return {"error": str(e)}

if __name__ == '__main__':
    # Public endpoints
    print("=== Public (homePage/official) ===")
    for ep in ["recentMatch", "searchList", "searchContent"]:
        r = probe(f"/homePage/official/{ep}", {"pageTitle": 0, "pageType": 0})
        code = r.get('code', '?')
        print(f"  {ep}: code={code}")

    # Auth-required endpoints  
    print("\n=== Auth Required ===")
    for ep in ["/race/calendar", "/match/list", "/user/sendSms", "/user/login"]:
        r = probe(ep)
        code = r.get('code', '?')
        print(f"  {ep}: code={code} {r.get('msg','')}")
