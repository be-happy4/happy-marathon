#!/usr/bin/env python3
"""
World Athletics 数据爬取脚本（原型验证用）。
通过 nimarion REST API 获取全球赛事数据。

用法:
  python3 wa_fetch.py [--proxy http://127.0.0.1:7897]
"""
import urllib.request, ssl, json, sys, os
from collections import OrderedDict

def fetch_all(proxy_url=None):
    ctx = ssl.create_default_context()
    ctx.check_hostname = False
    ctx.verify_mode = ssl.CERT_NONE
    
    opener = urllib.request.build_opener()
    if proxy_url:
        proxy_handler = urllib.request.ProxyHandler({'https': proxy_url})
        opener = urllib.request.build_opener(proxy_handler)
    
    search_terms = [
        "Marathon", "Half Marathon", "10km", "10K", "Trail",
        "China", "Chinese", "Beijing Marathon", "Shanghai Marathon"
    ]
    
    all_comps = {}
    for term in search_terms:
        url = f"https://worldathletics.nimarion.de/competitions?name={urllib.parse.quote(term)}"
        try:
            req = urllib.request.Request(url)
            with opener.open(req, timeout=15) as resp:
                comps = json.loads(resp.read())
                for c in comps:
                    if c['id'] not in all_comps:
                        all_comps[c['id']] = c
        except Exception as e:
            print(f"Search '{term}' failed: {e}", file=sys.stderr)
    
    print(f"Total unique: {len(all_comps)}")
    return list(all_comps.values())

if __name__ == '__main__':
    import argparse, urllib.parse
    p = argparse.ArgumentParser()
    p.add_argument('--proxy', help='HTTPS proxy, e.g. http://127.0.0.1:7897')
    p.add_argument('-o', '--output', default='wa_competitions.json', help='Output file')
    args = p.parse_args()
    
    comps = fetch_all(args.proxy)
    with open(args.output, 'w') as f:
        json.dump(comps, f, ensure_ascii=False, indent=2)
    print(f"Saved {len(comps)} competitions to {args.output}")
