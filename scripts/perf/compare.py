"""Compare two manifests scenario by scenario. Used for before/after on one optimisation."""
import sys
sys.path.insert(0, "scripts/perf")
from parse_dump import parse

def load(path):
    out = {}
    for line in open(path, encoding="utf8"):
        p = line.rstrip("\n").split("\t")
        if len(p) < 2 or p[1] == "NO_DUMP":
            continue
        out[p[0]] = parse(p[1])
    return out

before, after = load(sys.argv[1]), load(sys.argv[2])

def zones(r):
    best = {}
    for z in r["zones"]:
        if z["name"].startswith("ait:"):
            best[z["name"]] = max(best.get(z["name"], 0), z["ms"])
    return best

print(f"{'scenario':<22}{'fps b':>8}{'fps a':>8}{'delta':>8}{'boti b':>9}{'boti a':>9}{'portals b':>11}{'portals a':>11}")
for name in before:
    if name not in after:
        continue
    b, a = before[name], after[name]
    bz, az = zones(b), zones(a)
    bb, ab = bz.get("ait:boti_exterior", 0), az.get("ait:boti_exterior", 0)
    bp = b["counters"].get("ait_boti_exterior_queued", 0)
    ap = a["counters"].get("ait_boti_exterior_queued", 0)
    print(f"{name:<22}{b['fps']:>8.0f}{a['fps']:>8.0f}{a['fps'] - b['fps']:>+8.0f}"
          f"{bb:>9.3f}{ab:>9.3f}{bp:>11.2f}{ap:>11.2f}")

print("\ncounters per frame, before -> after")
KEYS = ["ait_exterior_dispatched", "ait_exterior_drawn", "ait_exterior_enqueued",
        "ait_boti_exterior_queued", "ait_boti_blit", "ait_boti_draw_flush", "ait_model_build"]
for name in before:
    if name not in after:
        continue
    parts = []
    for k in KEYS:
        bv = before[name]["counters"].get(k, 0)
        av = after[name]["counters"].get(k, 0)
        if bv or av:
            parts.append(f"{k.replace('ait_', '')}={bv:.1f}->{av:.1f}")
    print(f"  {name:<22} " + "  ".join(parts))
