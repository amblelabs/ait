#!/usr/bin/env bash
# Brings the harness up on a guaranteed-fresh world. State leaking between sweeps has already
# invalidated two runs: a leftover rift costs about a millisecond a frame, and stale TARDISes
# inflate the supposedly-empty baseline. Deleting the world is the only way to be certain.
set -u
HOST=127.0.0.1; PORT=25632; PASS=aitperf

echo "== stopping any running client/server"
powershell.exe -NoProfile -Command "Get-CimInstance Win32_Process -Filter \"Name='java.exe'\" | Where-Object { \$_.CommandLine -like '*loom-cache*' -and \$_.CommandLine -like '*ait*' } | ForEach-Object { Stop-Process -Id \$_.ProcessId -Force -ErrorAction SilentlyContinue }" >/dev/null 2>&1
sleep 5

echo "== deleting the world"
rm -rf run/server/perfworld
rm -f run/logs/latest.log run/server/logs/latest.log

echo "== starting server"
nohup ./gradlew runServer --console=plain > /tmp/harness_srv.log 2>&1 &
printf 'list\n' > /tmp/h_list.txt
for i in $(seq 1 120); do
  python scripts/perf/rcon.py $HOST $PORT $PASS /tmp/h_list.txt >/dev/null 2>&1 && break
  sleep 5
done
python scripts/perf/rcon.py $HOST $PORT $PASS /tmp/h_list.txt >/dev/null 2>&1 || { echo "server never came up"; exit 1; }
echo "== server up"

echo "== starting client"
nohup ./gradlew runClient --console=plain --args="--quickPlayMultiplayer 127.0.0.1:25565" > /tmp/harness_cli.log 2>&1 &
for i in $(seq 1 140); do
  out=$(python scripts/perf/rcon.py $HOST $PORT $PASS /tmp/h_list.txt 2>/dev/null)
  case "$out" in *"There are 1 of"*) echo "== client connected"; break;; esac
  sleep 5
done

# World-level determinism, applied once on the fresh world.
printf 'gamerule doDaylightCycle false\ngamerule doWeatherCycle false\ngamerule randomTickSpeed 0\ngamerule doMobSpawning false\ngamerule doMobLoot false\ngamerule doFireTick false\ntime set noon\nweather clear\ndifficulty peaceful\n' > /tmp/h_rules.txt
python scripts/perf/rcon.py $HOST $PORT $PASS /tmp/h_rules.txt >/dev/null 2>&1
echo "== harness ready on a fresh world"
