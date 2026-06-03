#!/usr/bin/env python3
"""
批量爬取 World Athletics 已结束赛事的成绩数据。
通过 nimarion REST API → 直接写入 crawler_game_result 临时表。

用法:
  python3 wa_result_crawl.py                  # 全量: 所有 hasResults=true 的 finished 赛事
  python3 wa_result_crawl.py --limit 10       # 限制数量(测试用)
  python3 wa_result_crawl.py --game-id 2086   # 只爬单个赛事
  python3 wa_result_crawl.py --dry-run        # 只列待爬赛事，不实际爬取
"""

import argparse
import json
import os
import time
import hashlib

# PostgreSQL 连接信息
DB_HOST = "127.0.0.1"
DB_PORT = "5432"
DB_NAME = "marathon"
DB_USER = "postgres"
DB_PASS = "root"

# WA API 配置
WA_API_BASE = "https://worldathletics.nimarion.de"
PROXY_HOST = "127.0.0.1"
PROXY_PORT = 7897
SOURCE_ID = 1  # crawler_source.id for world_athletics
RATE_LIMIT_MS = 500  # 每次请求间隔


def run_sql(sql: str) -> str:
    """通过 psql 执行 SQL"""
    env = os.environ.copy()
    env["PGPASSWORD"] = DB_PASS
    import subprocess
    result = subprocess.run(
        ["/Applications/Postgres.app/Contents/Versions/16/bin/psql",
         "-h", DB_HOST, "-p", DB_PORT, "-U", DB_USER, "-d", DB_NAME,
         "-t", "-A", "-c", sql],
        capture_output=True, text=True, env=env
    )
    if result.returncode != 0:
        raise Exception(f"SQL error: {result.stderr}")
    return result.stdout.strip()


def fetch_json(url: str, timeout: int = 30) -> dict | list | None:
    """通过代理获取 JSON (使用 curl，兼容 macOS SSL)"""
    import subprocess
    try:
        result = subprocess.run(
            ["curl", "--proxy", f"http://{PROXY_HOST}:{PROXY_PORT}",
             "--connect-timeout", str(timeout), "-s", url],
            capture_output=True, text=True, timeout=timeout + 5
        )
        if result.returncode != 0:
            print(f"  curl error: {result.stderr[:100]}")
            return None
        return json.loads(result.stdout)
    except Exception as e:
        print(f"  HTTP error: {e}")
        return None


def get_pending_games(limit: int = None, game_id: int = None) -> list[dict]:
    """获取待爬取成绩的 finished 赛事"""
    if game_id:
        where = f"AND cg.id = {game_id}"
    else:
        where = ""

    sql = f"""
    SELECT cg.id, cg.source_game_id, cg.name, cg.game_date, cg.region_name,
           (SELECT count(*) FROM crawler_game_result cgr
            WHERE cgr.crawler_game_id = cg.id AND cgr.deleted = 0) AS existing_count
    FROM crawler_game cg
    WHERE cg.source_id = {SOURCE_ID}
      AND cg.status = 'finished'
      AND cg.source_game_id IS NOT NULL
      AND cg.source_game_id != ''
      AND cg.deleted = 0
      AND cg.raw_data::text LIKE '%hasResults%true%'
      {where}
    ORDER BY cg.game_date DESC
    """

    if limit:
        sql += f" LIMIT {limit}"

    raw = run_sql(sql)
    if not raw:
        return []

    games = []
    for line in raw.split("\n"):
        parts = line.split("|")
        if len(parts) >= 6:
            games.append({
                "id": int(parts[0]),
                "source_game_id": parts[1],
                "name": parts[2],
                "game_date": parts[3],
                "region_name": parts[4],
                "existing_count": int(parts[5]) if parts[5] else 0,
            })
    return games


def make_id_card(crawler_game_id: int, place: int, name: str) -> str:
    """生成去重用的 id_card (WA 没有真实身份证号，用摘要代替)"""
    raw = f"WA:{crawler_game_id}:{place}:{name}"
    return hashlib.md5(raw.encode()).hexdigest()[:32]


def esc_sql(s: str | None) -> str:
    """SQL 转义"""
    if s is None:
        return "NULL"
    return "'" + s.replace("'", "''") + "'"


def insert_result(game: dict, result: dict, event: dict, race: dict) -> bool:
    """插入一条成绩到 crawler_game_result (带去重)"""
    athlete = result.get("athletes", [{}])[0] if result.get("athletes") else {}
    name = f"{athlete.get('firstname', '')} {athlete.get('lastname', '')}".strip()

    if not name:
        return False

    id_card = make_id_card(game["id"], result.get("place", 0), name)
    raw_json = esc_sql(json.dumps(result, ensure_ascii=False))

    # 检查是否已存在
    check = run_sql(f"""
    SELECT id FROM crawler_game_result
    WHERE crawler_game_id = {game['id']}
      AND id_card = {esc_sql(id_card)}
      AND deleted = 0 LIMIT 1
    """)

    if check:
        return False  # 已存在，跳过

    fields = {
        "source_id": SOURCE_ID,
        "crawler_game_id": game["id"],
        "crawler_category_id": "NULL",
        "game_name": esc_sql(game["name"]),
        "game_date": esc_sql(game["game_date"]) if game.get("game_date") else "NULL",
        "bib_number": "NULL",
        "name": esc_sql(name),
        "name_en": esc_sql(name),
        "nationality": esc_sql(result.get("country")),
        "gender": esc_sql(event.get("sex")),
        "age_group": esc_sql(event.get("category")),
        "id_card": esc_sql(id_card),
        "passport": "NULL",
        "gun_time_ms": result.get("performanceValue", "NULL"),
        "net_time_ms": result.get("performanceValue", "NULL"),
        "rank": result.get("place", "NULL"),
        "gender_rank": "NULL",
        "category_rank": "NULL",
        "raw_data": raw_json,
        "match_status": "'UNMATCHED'",
        "import_status": "'PENDING'",
        "creator": "'1'",
        "updater": "'1'",
        "deleted": "0",
    }

    cols = ", ".join(fields.keys())
    vals = ", ".join(str(v) for v in fields.values())

    sql = f"INSERT INTO crawler_game_result ({cols}) VALUES ({vals})"
    run_sql(sql)
    return True


def crawl_game(game: dict) -> tuple[int, int]:
    """爬取单个赛事的所有成绩"""
    comp_id = game["source_game_id"]
    print(f"\n[{game['id']}] {game['name']} ({game['game_date']})", end="", flush=True)

    resp = fetch_json(f"{WA_API_BASE}/competitions/{comp_id}/results")
    if not resp:
        print(" → 无数据或请求失败")
        return 0, 0

    events = resp.get("events") or []
    if not events:
        print(" → 0 events")
        return 0, 0

    total = 0
    inserted = 0
    for event in events:
        for race in event.get("races") or []:
            results = race.get("results") or []
            total += len(results)
            for r in results:
                if insert_result(game, r, event, race):
                    inserted += 1

    skipped = total - inserted
    print(f" → {total} 条 (新增 {inserted}, 已跳过 {skipped})")
    return inserted, skipped


def main():
    parser = argparse.ArgumentParser(description="World Athletics 成绩爬取")
    parser.add_argument("--limit", type=int, help="限制爬取数量")
    parser.add_argument("--game-id", type=int, help="只爬取指定赛事")
    parser.add_argument("--dry-run", action="store_true", help="只列待爬赛事")
    args = parser.parse_args()

    games = get_pending_games(limit=args.limit, game_id=args.game_id)

    if not games:
        print("没有待爬取的 finished 赛事")
        return

    print(f"找到 {len(games)} 个待爬取赛事:")
    for g in games:
        print(f"  [{g['id']}] {g['name']} ({g['game_date']}) "
              f"source_id={g['source_game_id']} region={g['region_name']}"
              f" existing={g['existing_count']}")

    if args.dry_run:
        return

    total_new = 0
    total_skipped = 0
    success = 0
    fail = 0

    for i, game in enumerate(games):
        try:
            new, skipped = crawl_game(game)
            total_new += new
            total_skipped += skipped
            if new + skipped > 0:
                success += 1
            time.sleep(RATE_LIMIT_MS / 1000)
        except Exception as e:
            print(f"  → 失败: {e}")
            fail += 1

    print(f"\n{'='*60}")
    print(f"完成: 成功 {success} 赛事, 失败 {fail} 赛事")
    print(f"新增 {total_new} 条成绩, 跳过 {total_skipped} 条(已存在)")
    print(f"{'='*60}")


if __name__ == "__main__":
    main()
