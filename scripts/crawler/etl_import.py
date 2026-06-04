#!/usr/bin/env python3
"""
ETL 导入：crawler_game → system_game, crawler_game_result → system_game_registration

用法:
  python3 etl_import.py --dry-run        # 预览
  python3 etl_import.py --games-only     # 只导入赛事
  python3 etl_import.py --all            # 全部导入
  python3 etl_import.py --match          # 运行自动匹配
"""

import argparse, os, subprocess, sys

DB = {
    "host": "127.0.0.1", "port": "5432", "db": "marathon",
    "user": "postgres", "pass": "root"
}
PSQL = ["/Applications/Postgres.app/Contents/Versions/16/bin/psql",
        "-h", DB["host"], "-p", DB["port"], "-U", DB["user"], "-d", DB["db"]]

def sql(cmd: str, **kw):
    env = os.environ.copy()
    env["PGPASSWORD"] = DB["pass"]
    r = subprocess.run(PSQL + ["-t", "-A", "-c", cmd], capture_output=True, text=True, env=env, **kw)
    if r.returncode != 0 and kw.get("check", True):
        print(f"SQL ERROR: {r.stderr}")
    return r.stdout.strip()

def esc(v):
    if v is None: return "NULL"
    return "'" + str(v).replace("'", "''") + "'"

# ============================================================
# Game import
# ============================================================

def import_games(dry_run=False):
    """crawler_game PENDING → system_game (by name+date matching or insert)"""

    # Get pending crawler games
    games_raw = sql("""
        SELECT cg.id, cg.name, to_char(cg.game_date,'YYYY-MM-DD'), cg.world_athletics_level,
               cg.china_road_run_level, cg.status, cg.tags, cg.region_name, cg.region_id
        FROM crawler_game cg
        WHERE cg.deleted = 0 AND cg.import_status = 'PENDING'
        ORDER BY cg.game_date DESC
    """)

    if not games_raw:
        print("No pending crawler games to import")
        return

    lines = [l for l in games_raw.split("\n") if l.strip()]
    print(f"Found {len(lines)} PENDING crawler games")

    def infer_game_type(name, tags):
        """Infer game_type from name"""
        if not name: return 'road_run'
        n = name.lower()
        if 'marathon' in n and 'half' not in n and '10k' not in n: return 'marathon'
        if 'half marathon' in n: return 'half_marathon'
        if '10k' in n or '10km' in n: return 'road_run'
        if 'trail' in n: return 'trail_run'
        if 'race walking' in n: return 'road_run'
        return 'road_run'

    new_count, linked_count = 0, 0
    for line in lines:
        parts = line.split("|")
        if len(parts) < 8: continue
        cg_id = parts[0]
        name = parts[1] if parts[1] else None
        game_date = parts[2] if parts[2] else None
        wa_level = parts[4] if len(parts) > 4 and parts[4] else None
        cn_level = parts[5] if len(parts) > 5 and parts[5] else None
        status = parts[6] if len(parts) > 6 and parts[6] else 'announced'
        tags = parts[7] if len(parts) > 7 and parts[7] else None
        region_name = parts[8] if len(parts) > 8 and parts[8] else None
        region_id = parts[9] if len(parts) > 9 and parts[9] else None

        game_type = infer_game_type(name, tags)

        # Check if matching system_game exists
        existing = sql(f"""
            SELECT id FROM system_game
            WHERE name = {esc(name)} AND game_date = {esc(game_date)} AND deleted = 0 LIMIT 1
        """)

        if existing:
            sg_id = existing
            print(f"  [Link] {name} ({game_date}) → system_game.id={sg_id}")
            if not dry_run:
                sql(f"UPDATE crawler_game SET import_status='IMPORTED', import_game_id={sg_id}, import_time=NOW() WHERE id={cg_id}")
            linked_count += 1
        else:
            if not dry_run:
                new_id = sql(f"""
                    INSERT INTO system_game (id, name, game_type, world_athletics_level,
                        china_road_run_level, game_date, status, tags, remark, region_id,
                        creator, create_time, updater, update_time, deleted, tenant_id)
                    VALUES (nextval('system_game_seq'), {esc(name)}, {esc(game_type)},
                        {esc(wa_level)}, {esc(cn_level)}, {esc(game_date)}, {esc(status)},
                        {esc(tags)}, {esc(region_name or name)},
                        {esc(region_id) if region_id else 'NULL'},
                        '1', NOW(), '1', NOW(), 0, 1)
                    RETURNING id
                """)
                if new_id:
                    sql(f"UPDATE crawler_game SET import_status='IMPORTED', import_game_id={new_id}, import_time=NOW() WHERE id={cg_id}")
                    print(f"  [New] {name} ({game_date}) → system_game.id={new_id}")
                else:
                    print(f"  [New FAIL] {name} ({game_date})")
            else:
                print(f"  [New] {name} ({game_date}) → new system_game")
            new_count += 1

    print(f"\nGame import: {linked_count} linked, {new_count} new (dry_run={dry_run})")


# ============================================================
# Result import (requires system_game entries first!)
# ============================================================

def import_results(dry_run=False):
    """MATCHED results → system_game_registration"""

    results_raw = sql("""
        SELECT cgr.id, cgr.game_name, cgr.game_date, cgr.bib_number, cgr.name,
               cgr.nationality, cgr.gender, cgr.age_group, cgr.gun_time_ms,
               cgr.net_time_ms, cgr.rank, cgr.gender_rank, cgr.category_rank,
               cgr.match_user_id, cgr.crawler_game_id
        FROM crawler_game_result cgr
        WHERE cgr.deleted = 0 AND cgr.match_status = 'MATCHED' AND cgr.import_status = 'PENDING'
        LIMIT 1000
    """)

    if not results_raw:
        print("No MATCHED+PENDING results to import")
        return

    lines = [l for l in results_raw.split("\n") if l.strip()]
    print(f"Found {len(lines)} MATCHED+PENDING results")

    imported = 0
    for line in lines:
        parts = line.split("|")
        if len(parts) < 14: continue
        cgr_id = parts[0]
        game_name, game_date = parts[1], parts[2]
        bib_number = parts[3] if parts[3] else None
        name = parts[4]
        nationality = parts[5] if parts[5] else None
        gender = parts[6] if parts[6] else None
        age_group = parts[7] if parts[7] else None
        gun_time = parts[8] if parts[8] else "NULL"
        net_time = parts[9] if parts[9] else "NULL"
        rank_val = parts[10] if parts[10] else "NULL"
        match_user_id = parts[13] if parts[13] else "NULL"

        # Find system_game
        sg_id = sql(f"""
            SELECT id FROM system_game WHERE name = {esc(game_name)}
            AND game_date = {esc(game_date)} AND deleted = 0 LIMIT 1
        """)

        if not sg_id:
            # Try via crawler_game.import_game_id
            cg_id = parts[14] if len(parts) > 14 else None
            if cg_id:
                sg_id = sql(f"""
                    SELECT import_game_id FROM crawler_game
                    WHERE id = {cg_id} AND import_status = 'IMPORTED' AND deleted = 0
                """)

        if not sg_id:
            continue

        if not dry_run:
            # Insert registration
            sql(f"""
                INSERT INTO system_game_registration (game_id, user_id, registration_status,
                    bib_number, gun_time_ms, net_time_ms, gender_place, overall_place, remark,
                    creator, create_time, updater, update_time, deleted, tenant_id)
                VALUES ({sg_id}, {match_user_id}, 'completed',
                    {esc(bib_number)}, {gun_time}, {net_time}, {rank_val}, {rank_val},
                    {esc('WA import: ' + name)},
                    '1', NOW(), '1', NOW(), 0, 1)
            """)
            sql(f"UPDATE crawler_game_result SET import_status='IMPORTED', import_time=NOW() WHERE id={cgr_id}")

        imported += 1

    print(f"\nResult import: {imported} imported (dry_run={dry_run})")


# ============================================================
# Auto-match via Java MatchEngine (call the existing backend API)
# ============================================================

def run_match():
    """Call match engine via backend API"""
    import urllib.request, json

    # Login
    login_data = json.dumps({"username":"admin","password":"admin123","tenantName":"芋道源码"}).encode()
    req = urllib.request.Request("http://localhost:48080/admin-api/system/auth/login",
        data=login_data, headers={"Content-Type":"application/json","tenant-id":"1"})
    with urllib.request.urlopen(req) as r:
        token = json.loads(r.read())["data"]["accessToken"]

    # Hit the batch query endpoint which internally calls MatchEngine.autoMatchAll()
    # Or we can just call the match engine via a dedicated call
    # Actually, the match engine is called in PersonalScoreQueryServiceImpl.batchQuery()
    # Let's just directly update via SQL for now

    print("Running automatic match via DB...")
    # For results with real-looking id_cards, try matching
    # (WA results use MD5 hashes, so this won't match most)

    # Check if any users have id_card set
    users = sql("SELECT id, real_name, id_card FROM system_users WHERE id_card IS NOT NULL AND deleted = 0")
    if users:
        user_lines = [l for l in users.split("\n") if l.strip()]
        print(f"Found {len(user_lines)} users with id_card set")
        matched = 0
        for ul in user_lines:
            uid, name, id_card = ul.split("|")
            # Match WA results that have matching id_card
            cnt = sql(f"""
                SELECT count(*) FROM crawler_game_result
                WHERE id_card = {esc(id_card)} AND match_status = 'UNMATCHED' AND deleted = 0
            """)
            if cnt and int(cnt) > 0:
                sql(f"""
                    UPDATE crawler_game_result
                    SET match_status = 'MATCHED', match_user_id = {uid},
                        match_confidence = 'HIGH', match_strategy = 'ID_CARD'
                    WHERE id_card = {esc(id_card)} AND match_status = 'UNMATCHED' AND deleted = 0
                """)
                print(f"  User {name} (id={uid}): matched {cnt} results by ID card")
                matched += 1

        # Also match by name (lower confidence)
        for ul in user_lines:
            uid, name, _ = ul.split("|")
            cnt2 = sql(f"""
                SELECT count(*) FROM crawler_game_result
                WHERE name = {esc(name)} AND match_status = 'UNMATCHED' AND deleted = 0
            """)
            if cnt2 and int(cnt2) > 0:
                sql(f"""
                    UPDATE crawler_game_result
                    SET match_status = 'MATCHED', match_user_id = {uid},
                        match_confidence = 'MEDIUM', match_strategy = 'NAME'
                    WHERE name = {esc(name)} AND match_status = 'UNMATCHED' AND deleted = 0
                """)
                print(f"  User {name} (id={uid}): matched {cnt2} results by name")

        total_matched = sql("SELECT count(*) FROM crawler_game_result WHERE match_status = 'MATCHED'")
        print(f"\nTotal matched results: {total_matched}")
    else:
        print("No users have id_card set")

    # Also call the backend MatchEngine if Spring is running
    try:
        # Try hitting a refresh endpoint that triggers matching
        print("(Spring MatchEngine not directly callable from script)")
    except Exception as e:
        pass


if __name__ == "__main__":
    p = argparse.ArgumentParser()
    p.add_argument("--dry-run", action="store_true")
    p.add_argument("--games-only", action="store_true")
    p.add_argument("--match", action="store_true")
    p.add_argument("--all", action="store_true")
    args = p.parse_args()

    if args.match:
        run_match()
    elif args.games_only or args.all:
        import_games(args.dry_run)
        if args.all:
            run_match()
            import_results(args.dry_run)
    else:
        # Default: show what would happen
        import_games(dry_run=True)
        print("\n---\nUse --games-only to import games, --match to match, --all for full ETL")
