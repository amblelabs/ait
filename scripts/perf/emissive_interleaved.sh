#!/usr/bin/env bash
# Interleaved A/B for copper's base layer. Alternates within one client session, so machine drift
# lands on both arms equally instead of on whichever ran second.
set -u
cd "$(dirname "$0")/../.." || exit 1
HOST=127.0.0.1; PORT=25632; PASS=aitperf
OUT=scripts/perf/emissive_interleaved.tsv
: > "$OUT"
printf 'list\n' > /tmp/s_list.txt
newest() { ls -t run/debug/profiling/*.zip 2>/dev/null | head -1; }

run_one() {   # $1 = translucent true|false, $2 = rep
  printf 'ait perf-flag sortEmissive %s\nait perf-console "console/copper"\nSLEEP 5\nait perf-tp console\nSLEEP 5\nait perf-verify\nait profile-client @a\nSLEEP 15\n' "$1" > /tmp/one.txt
  before=$(newest)
  python scripts/perf/rcon.py $HOST $PORT $PASS /tmp/one.txt > /tmp/li_out.log 2>&1
  after=$(newest); [ "$after" = "$before" ] && after=NO_DUMP
  flag=$(grep -o 'PERF-FLAG.*' /tmp/li_out.log | tail -1)
  printf 'translucent=%s\trep%s\t%s\t%s\n' "$1" "$2" "$after" "${flag:-none}" >> "$OUT"
  echo "  rep$2 translucent=$1  ${flag:-NO FLAG}"
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
