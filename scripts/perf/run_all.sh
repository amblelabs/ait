#!/usr/bin/env bash
# Runs every scenario in order and records which dump belongs to which, since dumps are
# only timestamped. Order matters: some scenarios build on the state the previous one left.
set -u
HOST=127.0.0.1; PORT=25632; PASS=aitperf
OUT=scripts/perf/manifest.tsv
: > "$OUT"

newest() { ls -t run/debug/profiling/*.zip 2>/dev/null | head -1; }

# Warm-up, discarded. The first profile after a launch is consistently the slowest.
printf 'gamemode spectator @a\ntp @a 0 100 0 90 0\nSLEEP 4\nait profile-client @a\nSLEEP 15\n' > /tmp/warm.txt
python scripts/perf/rcon.py $HOST $PORT $PASS /tmp/warm.txt >/dev/null 2>&1
echo "warmup discarded: $(newest)"

for f in scripts/perf/s[0-9][0-9]_*.txt; do
  name=$(basename "$f" .txt)
  before=$(newest)
  python scripts/perf/rcon.py $HOST $PORT $PASS "$f" > "/tmp/${name}.log" 2>&1
  after=$(newest)
  if [ "$after" = "$before" ]; then
    echo -e "${name}\tNO_DUMP" >> "$OUT"
    echo "!! $name produced no dump"
  else
    echo -e "${name}\t${after}" >> "$OUT"
    echo "ok $name -> $(basename "$after")"
  fi
  sleep 2
done
echo "--- manifest:"; cat "$OUT"
