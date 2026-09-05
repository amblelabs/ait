#!/usr/bin/env bash
# Runs every scenario in order and records which dump belongs to which, plus the verified
# server-side state at profile time. Order matters: some scenarios build on the previous one.
set -u
HOST=127.0.0.1; PORT=25632; PASS=aitperf
OUT=${MANIFEST_DIR:-run/debug/perf}/manifest.tsv
mkdir -p "$(dirname "$OUT")"
: > "$OUT"

newest() { ls -t run/debug/profiling/*.zip 2>/dev/null | head -1; }

# Warm-up, discarded. The first profile after a launch is reliably the slowest.
printf 'ait perf-clear\ngamemode spectator @a\ntp @a 0 100 0 90 0\nSLEEP 5\nait profile-client @a\nSLEEP 15\n' > /tmp/warm.txt
python scripts/perf/rcon.py $HOST $PORT $PASS /tmp/warm.txt >/dev/null 2>&1
echo "warmup discarded: $(basename "$(newest)")"

for f in scripts/perf/s[0-9][0-9]_*.txt; do
  name=$(basename "$f" .txt)
  before=$(newest)
  python scripts/perf/rcon.py $HOST $PORT $PASS "$f" > "/tmp/${name}.log" 2>&1
  after=$(newest)
  verify=$(grep -o 'PERF-VERIFY.*' "/tmp/${name}.log" | tail -1)
  if [ "$after" = "$before" ]; then
    printf '%s\tNO_DUMP\t%s\n' "$name" "${verify:-none}" >> "$OUT"
    echo "!! $name produced no dump"
  else
    printf '%s\t%s\t%s\n' "$name" "$after" "${verify:-none}" >> "$OUT"
    echo "ok $name  ${verify:-}"
  fi
  sleep 2
done
