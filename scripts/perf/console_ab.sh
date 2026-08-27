#!/usr/bin/env bash
# Before/after for the bone lookup cache, toggling the mixin between the two halves.
#
# Both halves restart the server as well as the client. perf-console is a server command, and a
# stale server silently answers "Incorrect argument for command", which looks identical to a bad
# argument rather than a missing command.
set -u
cd "$(dirname "$0")/../.." || exit 1

HOST=127.0.0.1
PORT=25632
PASS=aitperf
printf 'list\n' > /tmp/s_list.txt

fresh_world() {
  rm -rf run/server/perfworld
  rm -f run/logs/latest.log run/server/logs/latest.log
  echo "  world deleted"
}

# A killed build can leave build/resources/main half-copied, and then processResources fails while
# compileJava reports UP-TO-DATE, so the launcher silently runs a stale binary. Verified, not assumed.
require_build() {
  if grep -qE "BUILD FAILED|FAILURE:" "$1"; then
    echo "  ABORT: build failed, would have run a stale binary"
    grep -A3 "What went wrong" "$1" | head -5
    return 1
  fi
  return 0
}

kill_all() {
  powershell.exe -NoProfile -Command 'Get-CimInstance Win32_Process -Filter "Name=''java.exe''" | Where-Object { $_.CommandLine -like "*loom-cache*" -and $_.CommandLine -like "*ait*" } | ForEach-Object { Stop-Process -Id $_.ProcessId -Force -ErrorAction SilentlyContinue }' >/dev/null 2>&1
  sleep 5
}

start_server() {
  nohup ./gradlew runServer --console=plain > /tmp/ab_server.log 2>&1 &
  for _ in $(seq 1 130); do
    if python scripts/perf/rcon.py $HOST $PORT $PASS /tmp/s_list.txt >/dev/null 2>&1; then
      echo "  server up"
      return 0
    fi
    sleep 5
  done
  echo "  server never came up"
  return 1
}

start_client() {
  nohup ./gradlew runClient --console=plain --args="--quickPlayMultiplayer 127.0.0.1:25565" > /tmp/ab_client.log 2>&1 &
  for _ in $(seq 1 140); do
    out=$(python scripts/perf/rcon.py $HOST $PORT $PASS /tmp/s_list.txt 2>/dev/null)
    case "$out" in
      *"There are 1 of"*) echo "  client in"; return 0 ;;
    esac
    sleep 5
  done
  echo "  client never joined"
  return 1
}

rules() {
  printf 'gamerule doDaylightCycle false\ngamerule doWeatherCycle false\ngamerule randomTickSpeed 0\ngamerule doMobSpawning false\ngamerule doMobLoot false\ntime set noon\nweather clear\n' > /tmp/h_rules.txt
  python scripts/perf/rcon.py $HOST $PORT $PASS /tmp/h_rules.txt >/dev/null 2>&1
}

# Sanity gate: refuse to measure if the server does not know the command that pins the variant.
require_perf_console() {
  printf 'ait perf-console "console/hartnell"\n' > /tmp/s_pc.txt
  if python scripts/perf/rcon.py $HOST $PORT $PASS /tmp/s_pc.txt 2>&1 | grep -q "PERF-CONSOLE"; then
    echo "  perf-console present"
    return 0
  fi
  echo "  ABORT: server does not have perf-console, the variant would not be pinned"
  return 1
}

set_mixin() {
  python - "$1" <<'PY'
import collections, json, sys

mode = sys.argv[1]
path = 'src/main/resources/ait.mixins.json'
data = json.load(open(path, encoding='utf8'), object_pairs_hook=collections.OrderedDict)
name = "client.rendering.SinglePartEntityModelMixin"

if mode == "off" and name in data["client"]:
    data["client"].remove(name)
if mode == "on" and name not in data["client"]:
    data["client"].append(name)
    data["client"].sort()

open(path, 'w', encoding='utf8', newline='\n').write(json.dumps(data, indent=2) + "\n")
print("  bone cache mixin:", mode)
PY
}

phase() {   # $1 = on|off, $2 = manifest
  set_mixin "$1"
  kill_all
  fresh_world
  start_server || return 1
  require_build /tmp/ab_server.log || return 1
  rules
  start_client || return 1
  require_build /tmp/ab_client.log || return 1
  require_perf_console || return 1
  # The interior scenarios need a TARDIS with a generated desktop before the console exists.
  printf 'ait perf-clear\nSLEEP 2\nait perf-spawn 1 8 0 100 12\nSLEEP 30\nait perf-doors open\nSLEEP 4\n' > /tmp/seed.txt
  python scripts/perf/rcon.py $HOST $PORT $PASS /tmp/seed.txt >/dev/null 2>&1
  bash scripts/perf/run_console.sh "$2"
}

echo "=========== BEFORE (bone cache off)"
phase off scripts/perf/console_before.tsv || exit 1

echo "=========== AFTER (bone cache on)"
phase on scripts/perf/console_after.tsv || exit 1

echo "=========== DONE"
