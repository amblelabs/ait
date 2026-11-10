#!/usr/bin/env bash
# Switches the working tree to one benchmark arm and rebuilds.
#
#   arm.sh branch     the perf branch as it stands
#   arm.sh main       origin/main, plus the harness commands and nothing else
#   arm.sh restore    back to the branch, discarding any arm tree
#
# The main arm needs the harness back-ported because origin/main has no perf-spawn and no
# profile-client, so there is no way to drive a scenario or take a dump on it. What is added is the
# two command classes, the packet identifier and the client side receiver that calls the same
# toggleDebugProfiler F3+L calls. No renderer, model or render layer is touched, so what gets
# measured is main's rendering.
set -u
cd "$(dirname "$0")/../.." || exit 1

MODE=${1:-}

case "$MODE" in
  branch|restore)
    git checkout HEAD -- src/main || exit 1
    echo "tree: perf branch at $(git rev-parse --short HEAD)"
    ;;

  main)
    git checkout origin/main -- src/main || exit 1

    # checkout writes and overwrites but never deletes, so anything the branch added and main does
    # not have would still be sitting in the tree, compiled in and measured. Clear those first.
    for stale in $(git diff --name-only --diff-filter=A origin/main -- src/main); do
      case "$stale" in
        *PerfScenarioCommand.java|*ProfileClientCommand.java) continue ;;
      esac
      echo "  dropping branch-only $stale"
      rm -f "$stale"
    done

    git checkout HEAD -- \
        src/main/java/dev/amble/ait/core/commands/PerfScenarioCommand.java \
        src/main/java/dev/amble/ait/core/commands/ProfileClientCommand.java || exit 1

    python - <<'PYEOF' || exit 1
import io, re

# The packet identifier and the command registrations, into main's AITMod.
p = 'src/main/java/dev/amble/ait/AITMod.java'
s = io.open(p, encoding='utf-8').read()

if 'PROFILE_CLIENT' not in s:
    m = re.search(r'^(\s*)public static final Identifier ', s, re.M)
    assert m, 'no Identifier constant to anchor to in AITMod'
    s = s[:m.start()] + m.group(1) + 'public static final Identifier PROFILE_CLIENT = AITMod.id("profile_client");\n' + s[m.start():]

if 'ProfileClientCommand.register' not in s:
    # Any existing command registration in the same lambda is a valid anchor.
    m = re.search(r'^(\s*)(\w+Command\.register\(dispatcher\);)', s, re.M)
    assert m, 'no command registration to anchor to in AITMod'
    s = s[:m.start()] + m.group(1) + 'ProfileClientCommand.register(dispatcher);\n' \
        + m.group(1) + 'PerfScenarioCommand.register(dispatcher);\n' + s[m.start():]

io.open(p, 'w', encoding='utf-8').write(s)

# The client receiver, into main's AITModClient.
p = 'src/main/java/dev/amble/ait/client/AITModClient.java'
s = io.open(p, encoding='utf-8').read()

if 'PROFILE_CLIENT' not in s:
    m = re.search(r'^(\s*)ClientPlayNetworking\.registerGlobalReceiver\(', s, re.M)
    assert m, 'no ClientPlayNetworking receiver to anchor to in AITModClient'
    block = (m.group(1) + 'ClientPlayNetworking.registerGlobalReceiver(AITMod.PROFILE_CLIENT, (client, handler, buf, responseSender) ->\n'
             + m.group(1) + '        client.execute(() -> client.toggleDebugProfiler(\n'
             + m.group(1) + '                text -> AITMod.LOGGER.info("[ait-profile] {}", text.getString()))));\n\n')
    s = s[:m.start()] + block + s[m.start():]
    io.open(p, 'w', encoding='utf-8').write(s)

print('harness back-ported onto main')
PYEOF
    echo "tree: origin/main at $(git rev-parse --short origin/main) plus harness"
    ;;

  *)
    echo "usage: $0 branch|main|restore"; exit 2
    ;;
esac

echo "== building"
if ! ./gradlew build -q -x test > /tmp/arm_build.log 2>&1; then
  echo "BUILD FAILED"; grep -E 'error:' /tmp/arm_build.log | sort -u | head -10; exit 3
fi
echo "== built"
