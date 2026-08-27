#!/usr/bin/env bash
# The five-scenario loop used between optimisations. Chosen to measure the change and to catch
# what it might break, rather than for coverage:
#   s00_empty            control, nothing on screen. Catches regressions that cost even when idle.
#   s01_one_shut         one exterior, BOTI off. Isolates the exterior model render from BOTI.
#   x1_booth             one exterior, BOTI on. Smallest per-portal signal.
#   x20_booth            twenty exteriors. The headline number, 64 fps and BOTI at 55% of frame.
#   s06_interior_console interior and door BOTI. DoorRenderer shares the fix, so this is the canary.
set -u
HOST=127.0.0.1; PORT=25632; PASS=aitperf
OUT=${1:-scripts/perf/manifest_five.tsv}
: > "$OUT"
newest() { ls -t run/debug/profiling/*.zip 2>/dev/null | head -1; }

printf 'ait perf-clear\ngamemode spectator @a\ntp @a 0 100 0 90 0\nSLEEP 4\nait profile-client @a\nSLEEP 15\n' > /tmp/warm.txt
python scripts/perf/rcon.py $HOST $PORT $PASS /tmp/warm.txt >/dev/null 2>&1
echo "warmup discarded"

for name in s00_empty s01_one_shut x1_booth x20_booth s06_interior_console; do
  f="scripts/perf/${name}.txt"
  before=$(newest)
  python scripts/perf/rcon.py $HOST $PORT $PASS "$f" > "/tmp/${name}.log" 2>&1
  after=$(newest)
  verify=$(grep -o 'PERF-VERIFY.*' "/tmp/${name}.log" | tail -1)
  [ "$after" = "$before" ] && after=NO_DUMP
  printf '%s\t%s\t%s\n' "$name" "$after" "${verify:-none}" >> "$OUT"
  echo "ok $name  ${verify:-}"
  sleep 2
done
