#!/usr/bin/env python3
"""
将从 WA API 爬取的赛事数据导入 PostgreSQL crawler_game 表。

用法:
  python3 import_to_db.py wa_competitions.json
"""
import json, sys, subprocess
from datetime import date

PSQL = "/Applications/Postgres.app/Contents/Versions/16/bin/psql"
DB = "marathon"
USER = "postgres"
HOST = "127.0.0.1"
PORT = "5432"

def import_competitions(comps, start_id=1000, source_id=1):
    today = date.today().isoformat()
    game_id = start_id
    sql_lines = []
    
    for comp in comps:
        disciplines = comp.get('disciplines', [])
        is_road = any('Road Running' in d or 'Marathon' in d or 'Race Walking' in d for d in disciplines)
        if not is_road:
            continue
        
        name = comp['name'].replace("'", "''")
        loc = comp.get('location', {}) or {}
        city = (loc.get('city') or '').replace("'", "''")
        country = loc.get('country', '')
        region = f"{city}, {country}" if city else country
        region = region.replace("'", "''") if region else ''
        
        n = name.lower()
        if 'marathon' in n and 'half' not in n and '10k' not in n:
            game_type, dist = 'marathon', '42.195'
        elif 'half marathon' in n:
            game_type, dist = 'half_marathon', '21.0975'
        elif '10k' in n or '10km' in n:
            game_type, dist = 'road_run', '10.000'
        else:
            game_type, dist = 'road_run', 'NULL'
        
        cg = comp.get('competitionGroup', '') or ''
        cg_l = cg.lower()
        if 'platinum' in cg_l: label = 'platinum'
        elif 'gold' in cg_l: label = 'gold'
        elif 'elite' in cg_l: label = 'elite'
        elif 'label' in cg_l: label = 'label'
        else: label = 'NULL'
        
        start = comp['start'][:10] if comp.get('start') else None
        if start and start > today: status = 'announced'
        elif start and start < today: status = 'finished'
        elif start: status = 'prepared'
        else: status = 'uncertain'; start = 'NULL'
        
        if start != 'NULL': start = f"'{start}'"
        raw_data = json.dumps(comp, ensure_ascii=False).replace("'", "''")
        label_val = f"'{label}'" if label != 'NULL' else 'NULL'
        
        sql_lines.append(f"INSERT INTO crawler_game (id, source_id, source_game_id, name, game_date, region_name, world_athletics_level, status, raw_data, import_status) VALUES ({game_id}, {source_id}, '{comp['id']}', '{name}', {start}, '{region}', {label_val}, '{status}', '{raw_data}', 'PENDING') ON CONFLICT DO NOTHING;")
        sql_lines.append(f"INSERT INTO crawler_game_category (id, crawler_game_id, game_type, distance_km) VALUES ({game_id*10}, {game_id}, '{game_type}', {dist}) ON CONFLICT DO NOTHING;")
        game_id += 1
    
    return sql_lines

if __name__ == '__main__':
    if len(sys.argv) < 2:
        print(f"Usage: {sys.argv[0]} <competitions.json>")
        sys.exit(1)
    
    with open(sys.argv[1]) as f:
        comps = json.load(f)
    
    sql = import_competitions(comps)
    sql_file = '/tmp/crawler_import.sql'
    with open(sql_file, 'w') as f:
        f.write('\n'.join(sql))
    
    subprocess.run([PSQL, '-h', HOST, '-p', PORT, '-U', USER, '-d', DB, '-f', sql_file])
    print(f"Imported {len(sql)//2} competitions")
