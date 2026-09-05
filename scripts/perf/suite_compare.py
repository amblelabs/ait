"""Compare two suite manifests scenario by scenario and print a before/after table.

Reads the tab separated manifests suite.sh writes and reports frame time and frames per second for
each arm, with the delta. Scenarios that failed in either arm are listed rather than dropped, so a
hole in the suite is visible instead of silently absent.

    python scripts/perf/suite_compare.py <beforeManifest> <afterManifest> [--md]
"""
import sys
from collections import OrderedDict

sys.path.insert(0, "scripts/perf")
from parse_dump import parse

FAILURES = ("NO_DUMP", "NO_CLIENT", "MISSING_FILE")

# Scenarios whose whole point is a zone that must therefore appear in a valid dump, keyed by the
# scenario's numeric prefix.
EXPECTED = {
    "s06": "console",
    "s07": "console",
    # s21 deliberately has no console zone on a build that culls it: the console is behind the
    # camera and being skipped is the result the scenario tests for.
    "s22": "console",
    "c01": "console",
    "c02": "console",
    "c03": "console",
    "c04": "console",
}


def load(path):
    out = OrderedDict()
    for line in open(path, encoding="utf8"):
        parts = line.rstrip("\n").split("\t")
        if len(parts) < 2:
            continue
        name, dump = parts[0], parts[1]
        if dump in FAILURES:
            out[name] = None
            continue
        try:
            r = parse(dump)
        except Exception as exc:                      # a truncated zip is a hole, not a crash
            print(f"# {name}: unreadable dump ({exc})", file=sys.stderr)
            out[name] = None
            continue

        # An interior scenario that profiled before its desktop finished generating never got the
        # player inside, and reports a spectacular frame time for a void. The tell is the absence of
        # the zone the scenario exists to measure, not the absence of terrain: a sparse overworld
        # frame legitimately never enters the terrain zone at all.
        want = EXPECTED.get(name.split("_", 1)[0])
        if want and not any(z["name"] == want for z in r["zones"]):
            print(f"# {name}: no {want!r} zone, the scenario did not reach what it measures",
                  file=sys.stderr)
            out[name] = None
            continue

        out[name] = r
    return out


def pretty(name):
    """Drop the ordering prefix but keep any count in it, so x1_booth and x5_booth stay distinct."""
    prefix, _, body = name.partition("_")
    body = body.replace("_", " ")
    count = "".join(c for c in prefix if c.isdigit())

    if prefix.startswith("x") and count:
        return f"{count} {body}"

    return body


def main():
    args = [a for a in sys.argv[1:] if not a.startswith("--")]
    md = "--md" in sys.argv
    before, after = load(args[0]), load(args[1])

    names = list(before)
    for n in after:
        if n not in names:
            names.append(n)

    rows, holes = [], []
    for n in names:
        b, a = before.get(n), after.get(n)
        if b is None or a is None:
            holes.append((n, "before" if b is None else "after"))
            continue
        rows.append((pretty(n), b["frame_ms"], b["fps"], a["frame_ms"], a["fps"]))

    if md:
        print("| scenario | main ms | main fps | branch ms | branch fps | fps change |")
        print("|---|---|---|---|---|---|")
        for name, bm, bf, am, af in rows:
            pct = (af - bf) / bf * 100.0
            print(f"| {name} | {bm:.2f} | {bf:.1f} | {am:.2f} | {af:.1f} | {pct:+.0f}% |")
    else:
        print(f"{'scenario':<22}{'main ms':>9}{'main fps':>10}{'branch ms':>11}{'branch fps':>12}{'change':>9}")
        for name, bm, bf, am, af in rows:
            pct = (af - bf) / bf * 100.0
            print(f"{name:<22}{bm:>9.2f}{bf:>10.1f}{am:>11.2f}{af:>12.1f}{pct:>8.0f}%")

    if rows:
        gains = [(af - bf) / bf * 100.0 for _, _, bf, _, af in rows]
        print()
        print(f"scenarios compared: {len(rows)}   median fps change: {sorted(gains)[len(gains) // 2]:+.0f}%"
              f"   best: {max(gains):+.0f}%   worst: {min(gains):+.0f}%")

    if holes:
        print()
        for n, which in holes:
            print(f"# no comparison for {n}: {which} arm produced no dump")


if __name__ == "__main__":
    main()
