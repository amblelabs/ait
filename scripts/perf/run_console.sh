#!/usr/bin/env bash
set -u
HOST=127.0.0.1; PORT=25632; PASS=aitperf
OUT=${1:-scripts/perf/manifest_console.tsv}
: > "$OUT"
newest() { ls -t run/debug/profiling/*.zip 2>/dev/null | head -1; }
printf 'gamemode spectator @a\nSLEEP 4\nait profile-client @a\nSLEEP 15\n' > /tmp/warm.txt
python scripts/perf/rcon.py $HOST $PORT $PASS /tmp/warm.txt >/dev/null 2>&1
echo "warmup discarded"
for name in c01_hartnell c02_copper c03_hartnell_flight c04_copper_flight; do
  before=$(newest)
  python scripts/perf/rcon.py $HOST $PORT $PASS "scripts/perf/${name}.txt" > "/tmp/${name}.log" 2>&1
  after=$(newest); v=$(grep -o 'PERF-VERIFY.*' "/tmp/${name}.log" | tail -1)
  c=$(grep -o 'PERF-CONSOLE.*' "/tmp/${name}.log" | tail -1)
  [ "$after" = "$before" ] && after=NO_DUMP
  printf '%s\t%s\t%s | %s\n' "$name" "$after" "${v:-none}" "${c:-none}" >> "$OUT"
  echo "ok $name  ${c:-}"
  sleep 2
done
