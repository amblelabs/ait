#!/usr/bin/env bash
# Before/after for the duplicate-draw skip, toggled with -Dait.dedupeDraws.
#
# The property means no rebuild between phases and the server can stay up, since the change is
# client side only. Only the client is cycled.
set -u
cd "$(dirname "$0")/../.." || exit 1

HOST=127.0.0.1
PORT=25632
PASS=aitperf
SCENARIOS="c01_hartnell c02_copper c04_copper_flight x20_booth"
printf 'list\n' > /tmp/s_list.txt

kill_client() {
  # Client only. The unparameterised kill takes the server with it, and then the client has
  # nothing to join.
  powershell.exe -NoProfile -ExecutionPolicy Bypass -File scripts/perf/kill_ait_java.ps1 -Kind client
  sleep 5
}

require_server() {
  if python scripts/perf/rcon.py $HOST $PORT $PASS /tmp/s_list.txt >/dev/null 2>&1; then
    return 0
  fi
  echo "  server is not up, starting it"
  nohup ./gradlew runServer --console=plain > /tmp/dd_server.log 2>&1 &
  for _ in $(seq 1 130); do
    python scripts/perf/rcon.py $HOST $PORT $PASS /tmp/s_list.txt >/dev/null 2>&1 && { echo "  server up"; return 0; }
    sleep 5
  done
  echo "  ABORT: server never came up"
  return 1
}

start_client() {   # $1 = extra JVM options
  JAVA_TOOL_OPTIONS="$1" nohup ./gradlew runClient --console=plain \
      --args="--quickPlayMultiplayer 127.0.0.1:25565" > /tmp/dd_client.log 2>&1 &
  for _ in $(seq 1 140); do
    out=$(python scripts/perf/rcon.py $HOST $PORT $PASS /tmp/s_list.txt 2>/dev/null)
    case "$out" in
      *"There are 1 of"*) echo "  client in"; return 0 ;;
      *"There are 2 of"*|*"There are 3 of"*) echo "  ABORT: more than one client: $out"; return 1 ;;
    esac
    sleep 5
  done
  echo "  client never joined"
  return 1
}

newest() { ls -t run/debug/profiling/*.zip 2>/dev/null | head -1; }

seed() {
  printf 'ait perf-clear\nSLEEP 2\nait perf-spawn 1 8 0 100 12\nSLEEP 30\nait perf-doors open\nSLEEP 4\n' > /tmp/seed.txt
  python scripts/perf/rcon.py $HOST $PORT $PASS /tmp/seed.txt >/dev/null 2>&1
}

phase() {   # $1 = jvm opts, $2 = manifest
  kill_client
  require_server || return 1

  if ! start_client "$1"; then
    echo "  first launch did not join, retrying once"
    kill_client
    start_client "$1" || return 1
  fi
  seed
  : > "$2"
  printf 'gamemode spectator @a\nSLEEP 4\nait profile-client @a\nSLEEP 15\n' > /tmp/warm.txt
  python scripts/perf/rcon.py $HOST $PORT $PASS /tmp/warm.txt >/dev/null 2>&1
  for name in $SCENARIOS; do
    before=$(newest)
    python scripts/perf/rcon.py $HOST $PORT $PASS "scripts/perf/${name}.txt" > "/tmp/dd_${name}.log" 2>&1
    after=$(newest)
    v=$(grep -o 'PERF-VERIFY.*' "/tmp/dd_${name}.log" | tail -1)
    [ "$after" = "$before" ] && after=NO_DUMP
    printf '%s\t%s\t%s\n' "$name" "$after" "${v:-none}" >> "$2"
    echo "  ok $name"
    sleep 2
  done
}

echo "=========== BEFORE (dedupe off)"
phase "-Dait.dedupeDraws=false" scripts/perf/dedupe_before.tsv || exit 1

echo "=========== AFTER (dedupe on)"
phase "-Dait.dedupeDraws=true" scripts/perf/dedupe_after.tsv || exit 1

echo "=========== DONE"
