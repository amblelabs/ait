#!/usr/bin/env bash
# Interleaved A/B for the emissive layer's CPU quad sort. Alternates within one client session, so
# machine drift lands on both arms equally instead of on whichever ran second.
#
# What it decides: the emission zones sit either side of a layer switch, and Immediate.getBuffer
# flushes the previous layer when the new one has no dedicated buffer, so the zone that opens a layer
# is billed for closing the one before it. If monitor's cost is really the emissive sort being billed
# to the next zone, sortEmissive=false moves it.
set -u
cd "$(dirname "$0")/../.." || exit 1
HOST=127.0.0.1; PORT=25632; PASS=aitperf
OUT=${MANIFEST_DIR:-run/debug/perf}/emissive_interleaved.tsv
mkdir -p "$(dirname "$OUT")"
: > "$OUT"
printf 'list\n' > /tmp/s_list.txt
newest() { ls -t run/debug/profiling/*.zip 2>/dev/null | head -1; }

run_one() {   # $1 = sortEmissive true|false, $2 = rep
  printf 'ait perf-flag @a sortEmissive %s\nait perf-console "console/copper"\nSLEEP 5\nait perf-tp console\nSLEEP 5\nait perf-verify\nait profile-client @a\nSLEEP 15\n' "$1" > /tmp/one.txt
  before=$(newest)
  python scripts/perf/rcon.py $HOST $PORT $PASS /tmp/one.txt > /tmp/li_out.log 2>&1
  after=$(newest); [ "$after" = "$before" ] && after=NO_DUMP
  flag=$(grep -o 'PERF-FLAG.*' /tmp/li_out.log | tail -1)
  printf 'sortEmissive=%s\trep%s\t%s\t%s\n' "$1" "$2" "$after" "${flag:-none}" >> "$OUT"
  echo "  rep$2 sortEmissive=$1  ${flag:-NO FLAG}"
  sleep 2
}

echo "seeding"
printf 'ait perf-clear\nSLEEP 2\nait perf-spawn 1 8 0 100 12\nSLEEP 30\nait perf-doors open\nSLEEP 4\ngamemode spectator @a\n' > /tmp/seed.txt
python scripts/perf/rcon.py $HOST $PORT $PASS /tmp/seed.txt >/dev/null 2>&1
printf 'ait profile-client @a\nSLEEP 15\n' > /tmp/warm.txt
python scripts/perf/rcon.py $HOST $PORT $PASS /tmp/warm.txt >/dev/null 2>&1
echo "warmup discarded"

for rep in 1 2 3; do
  run_one true  "$rep"
  run_one false "$rep"
done
echo DONE
