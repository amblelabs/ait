#!/usr/bin/env bash
# Runs the whole scenario suite for one arm and writes a manifest of scenario to dump.
#
#   suite.sh <armLabel> [scenarioFile ...]
#
# Resilient on purpose: a scenario that produces no dump, or a server that dies part way through,
# records the failure and carries on to the next rather than stalling the run. A suite that stops
# silently on scenario three is worse than one with three holes in it, because the holes are visible.
#
# Ordered so the cheap overworld scenarios run first and the interior ones, which need a generated
# desktop and are where the vanilla CarvedPumpkinBlock crash lives, run last.
set -u
cd "$(dirname "$0")/../.." || exit 1

ARM=${1:-}
shift || true

if [ -z "$ARM" ]; then echo "usage: $0 <armLabel> [scenarioFile ...]"; exit 2; fi

HOST=127.0.0.1; PORT=25632; PASS=aitperf
OUT=${MANIFEST_DIR:-run/debug/perf}/suite_${ARM}.tsv
mkdir -p "$(dirname "$OUT")"
: > "$OUT"

rcon() { python scripts/perf/rcon.py $HOST $PORT $PASS "$1" 2>&1; }
newest() { ls -t run/debug/profiling/*.zip 2>/dev/null | head -1; }

alive() {
  printf 'list\n' > /tmp/suite_list.txt
  rcon /tmp/suite_list.txt 2>&1 | grep -q 'There are 1 of'
}

if [ "$#" -gt 0 ]; then
  SCENARIOS=("$@")
else
  SCENARIOS=(
    scripts/perf/s00_empty.txt
    scripts/perf/s01_one_shut.txt
    scripts/perf/s02_one_open.txt
    scripts/perf/s03_five_open.txt
    scripts/perf/s04_twenty_open.txt
    scripts/perf/s18_forty_open.txt
    scripts/perf/s05_twenty_far.txt
    scripts/perf/s20_twenty_away.txt
    scripts/perf/x1_booth.txt
    scripts/perf/x5_booth.txt
    scripts/perf/x20_booth.txt
    scripts/perf/x40_booth.txt
    scripts/perf/s16_doom.txt
    scripts/perf/s11_siege.txt
    scripts/perf/s12_shields.txt
    scripts/perf/s13_alarm.txt
    scripts/perf/s14_nopower.txt
    scripts/perf/s10_unlinked.txt
    scripts/perf/s19_rift.txt
    scripts/perf/s17_paintings.txt
    scripts/perf/s08_vortex_flight.txt
    scripts/perf/s15_coral.txt
    scripts/perf/s06_interior_console.txt
    scripts/perf/s07_interior_away.txt
    scripts/perf/s21_console_behind.txt
    scripts/perf/s22_generators.txt
    scripts/perf/s09_exterior_back.txt
    scripts/perf/c02_copper.txt
    scripts/perf/c01_hartnell.txt
    scripts/perf/c04_copper_flight.txt
    scripts/perf/c03_hartnell_flight.txt
  )
fi

echo "== arm $ARM, ${#SCENARIOS[@]} scenarios"
started=$(date +%s)

for f in "${SCENARIOS[@]}"; do
  name=$(basename "$f" .txt)

  if [ ! -f "$f" ]; then
    printf '%s\tMISSING_FILE\tnone\n' "$name" >> "$OUT"
    echo "  $name  MISSING FILE"
    continue
  fi

  if ! alive; then
    printf '%s\tNO_CLIENT\tnone\n' "$name" >> "$OUT"
    echo "  $name  SKIPPED, no client connected"
    continue
  fi

  before=$(newest)
  rcon "$f" > /tmp/suite_out.log 2>&1
  after=$(newest)
  verify=$(grep -o 'PERF-VERIFY.*' /tmp/suite_out.log | tail -1)

  if [ "$after" = "$before" ] || [ -z "$after" ]; then
    printf '%s\tNO_DUMP\t%s\n' "$name" "${verify:-none}" >> "$OUT"
    echo "  $name  NO DUMP"
    continue
  fi

  printf '%s\t%s\t%s\n' "$name" "$after" "${verify:-none}" >> "$OUT"
  echo "  $name  $(basename "$after")"
  sleep 2
done

echo
echo "arm $ARM finished in $((($(date +%s) - started) / 60)) min"
echo "manifest: $OUT"
