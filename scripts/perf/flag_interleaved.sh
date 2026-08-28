#!/usr/bin/env bash
# Interleaved A/B of one client render flag.
#
#   flag_interleaved.sh <flagName> [consoleVariant] [reps]
#
# Both arms alternate inside a single client session, so machine drift lands on both equally rather
# than on whichever ran second. Sequential arms are not good enough here: a scenario the switch could
# not affect still drifted fifteen percent between two phases measured minutes apart.
#
# Parameterised by flag name on purpose. There used to be a copy of this file per flag, which meant a
# flag being deleted left a script that set something nobody read, took its dumps, and reported two
# arms that were the same code.
set -u
cd "$(dirname "$0")/../.." || exit 1

FLAG=${1:-}
VARIANT=${2:-console/copper}
REPS=${3:-3}

if [ -z "$FLAG" ]; then
  echo "usage: $0 <flagName> [consoleVariant] [reps]"
  exit 2
fi

HOST=127.0.0.1; PORT=25632; PASS=aitperf
OUT=${MANIFEST_DIR:-run/debug/perf}/flag_${FLAG}.tsv
mkdir -p "$(dirname "$OUT")"
: > "$OUT"

newest() { ls -t run/debug/profiling/*.zip 2>/dev/null | head -1; }
rcon() { python scripts/perf/rcon.py $HOST $PORT $PASS "$1" 2>&1; }

# Verified, not assumed. Every one of these has silently produced a bad run at least once.
printf 'list\n' > /tmp/fi_list.txt
out=$(rcon /tmp/fi_list.txt)
case "$out" in
  *"There are 1 of"*) echo "one client connected" ;;
  *"There are 0 of"*)
    echo "ABORT: no client connected. Every arm would profile nothing and report NO_DUMP."
    exit 1 ;;
  *"There are "*)
    echo "ABORT: more than one client. profile-client @a profiles all of them, each writing its own"
    echo "       dump, and the reader takes whichever landed last."
    echo "       $out"
    exit 1 ;;
  *) echo "ABORT: server did not answer rcon."; exit 1 ;;
esac

# Prove the flag actually reaches the client before spending a run on it. A flag that no code reads
# still reports "sent to 1 client(s)", so this checks the client's own acknowledgement.
printf 'ait perf-flag @a %s true\n' "$FLAG" > /tmp/fi_probe.txt
case "$(rcon /tmp/fi_probe.txt)" in
  *"sent to 1 client"*) echo "flag $FLAG accepted by the server" ;;
  *) echo "ABORT: perf-flag $FLAG was not accepted. Does the flag still exist?"; exit 1 ;;
esac

run_one() {   # $1 = on|off value, $2 = rep
  printf 'ait perf-flag @a %s %s\nait perf-console "%s"\nSLEEP 5\nait perf-tp console\nSLEEP 5\nait perf-verify\nait profile-client @a\nSLEEP 15\n' \
      "$FLAG" "$1" "$VARIANT" > /tmp/fi_one.txt
  before=$(newest)
  rcon /tmp/fi_one.txt > /tmp/fi_out.log
  after=$(newest); [ "$after" = "$before" ] && after=NO_DUMP
  flag=$(grep -o 'PERF-FLAG.*' /tmp/fi_out.log | tail -1)
  verify=$(grep -o 'PERF-VERIFY.*' /tmp/fi_out.log | tail -1)
  printf '%s=%s\trep%s\t%s\t%s\t%s\n' "$FLAG" "$1" "$2" "$after" "${flag:-none}" "${verify:-none}" >> "$OUT"
  echo "  rep$2 $FLAG=$1  ${after##*/}  ${flag:-NO FLAG}"
  [ "$after" = NO_DUMP ] && { echo "  ABORT: that arm produced no dump."; return 1; }
  sleep 2
  return 0
}

echo "seeding"
printf 'ait perf-clear\nSLEEP 2\nait perf-spawn 1 8 0 100 12\nSLEEP 32\nait perf-doors open\nSLEEP 4\ngamemode spectator @a\n' > /tmp/fi_seed.txt
rcon /tmp/fi_seed.txt >/dev/null
printf 'ait profile-client @a\nSLEEP 15\n' > /tmp/fi_warm.txt
rcon /tmp/fi_warm.txt >/dev/null
echo "warmup discarded, the first profile after a launch is reliably the slowest"

for rep in $(seq 1 "$REPS"); do
  run_one true  "$rep" || exit 1
  run_one false "$rep" || exit 1
done

echo "DONE -> $OUT"
