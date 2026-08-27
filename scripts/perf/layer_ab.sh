#!/usr/bin/env bash
# Diagnostic: is the console cost the layer flush (and copper's translucent quad sort) rather than
# the emission or monitor work the zone names suggest?
set -u
cd "$(dirname "$0")/../.." || exit 1
HOST=127.0.0.1; PORT=25632; PASS=aitperf
printf 'list\n' > /tmp/s_list.txt

kill_client() { powershell.exe -NoProfile -ExecutionPolicy Bypass -File scripts/perf/kill_ait_java.ps1 -Kind client; sleep 5; }
require_server() {
  python scripts/perf/rcon.py $HOST $PORT $PASS /tmp/s_list.txt >/dev/null 2>&1 && return 0
  nohup ./gradlew runServer --console=plain > /tmp/la_server.log 2>&1 &
  for _ in $(seq 1 130); do python scripts/perf/rcon.py $HOST $PORT $PASS /tmp/s_list.txt >/dev/null 2>&1 && { echo "  server up"; return 0; }; sleep 5; done
  echo "  ABORT: no server"; return 1
}
start_client() {
  JAVA_TOOL_OPTIONS="$1" nohup ./gradlew runClient --console=plain --args="--quickPlayMultiplayer 127.0.0.1:25565" > /tmp/la_client.log 2>&1 &
  for _ in $(seq 1 140); do
    out=$(python scripts/perf/rcon.py $HOST $PORT $PASS /tmp/s_list.txt 2>/dev/null)
    case "$out" in *"There are 1 of"*) echo "  client in"; return 0;; *"There are 2 of"*) echo "  ABORT: two clients"; return 1;; esac
    sleep 5
  done
  echo "  client never joined"; return 1
}
newest() { ls -t run/debug/profiling/*.zip 2>/dev/null | head -1; }

phase() {
  kill_client; require_server || return 1
  start_client "$1" || { kill_client; start_client "$1" || return 1; }
  printf 'ait perf-clear\nSLEEP 2\nait perf-spawn 1 8 0 100 12\nSLEEP 30\nait perf-doors open\nSLEEP 4\n' > /tmp/seed.txt
  python scripts/perf/rcon.py $HOST $PORT $PASS /tmp/seed.txt >/dev/null 2>&1
  : > "$2"
  printf 'gamemode spectator @a\nSLEEP 4\nait profile-client @a\nSLEEP 15\n' > /tmp/warm.txt
  python scripts/perf/rcon.py $HOST $PORT $PASS /tmp/warm.txt >/dev/null 2>&1
  for name in c01_hartnell c02_copper; do
    before=$(newest)
    python scripts/perf/rcon.py $HOST $PORT $PASS "scripts/perf/${name}.txt" > "/tmp/la_${name}.log" 2>&1
    after=$(newest); [ "$after" = "$before" ] && after=NO_DUMP
    v=$(grep -o 'PERF-VERIFY.*' "/tmp/la_${name}.log" | tail -1)
    printf '%s\t%s\t%s\n' "$name" "$after" "${v:-none}" >> "$2"
    echo "  ok $name"; sleep 2
  done
}

echo "=========== copper on translucent layer (current)"
phase "-Dait.copperTranslucent=true" scripts/perf/layer_translucent.tsv || exit 1
echo "=========== copper on cutout layer"
phase "-Dait.copperTranslucent=false" scripts/perf/layer_cutout.tsv || exit 1
echo "=========== DONE"
