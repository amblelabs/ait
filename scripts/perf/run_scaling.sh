#!/usr/bin/env bash
set -u
HOST=127.0.0.1; PORT=25632; PASS=aitperf
OUT=scripts/perf/manifest_scaling.tsv
: > "$OUT"
newest() { ls -t run/debug/profiling/*.zip 2>/dev/null | head -1; }
printf 'ait perf-clear\ngamemode spectator @a\ntp @a 0 100 0 90 0\nSLEEP 4\nait profile-client @a\nSLEEP 15\n' > /tmp/warm.txt
python scripts/perf/rcon.py $HOST $PORT $PASS /tmp/warm.txt >/dev/null 2>&1
for f in scripts/perf/x*_booth.txt; do
  name=$(basename "$f" .txt); before=$(newest)
  python scripts/perf/rcon.py $HOST $PORT $PASS "$f" > "/tmp/${name}.log" 2>&1
  after=$(newest); verify=$(grep -o 'PERF-VERIFY.*' "/tmp/${name}.log" | tail -1)
  [ "$after" = "$before" ] && after=NO_DUMP
  printf '%s\t%s\t%s\n' "$name" "$after" "${verify:-none}" >> "$OUT"
  echo "ok $name  ${verify:-}"
  sleep 2
done
