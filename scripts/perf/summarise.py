"""Table of every scenario in the manifest: frame time, GPU, and the ait zones/counters."""
import sys
sys.path.insert(0, "scripts/perf")
from parse_dump import parse

rows = []
for line in open("scripts/perf/manifest.tsv", encoding="utf8"):
    name, path = line.rstrip("\n").split("\t")
    if path == "NO_DUMP":
        rows.append((name, None)); continue
    rows.append((name, parse(path)))

print(f"{'scenario':<22}{'frame_ms':>9}{'fps':>7}{'gpu%':>7}{'aitTotal':>10}  top ait zone")
print("-" * 90)
for name, r in rows:
    if r is None:
        print(f"{name:<22}  NO DUMP"); continue
    ait = [z for z in r["zones"] if z["name"].startswith("ait:")]
    # de-duplicate: the same zone appears at several depths in the tree
    seen = {}
    for z in ait:
        seen[z["name"]] = max(seen.get(z["name"], 0), z["ms"])
    total = sum(seen.values())
    top = max(seen.items(), key=lambda kv: kv[1]) if seen else ("-", 0)
    print(f"{name:<22}{r['frame_ms']:>9.3f}{r['fps']:>7.0f}{r['gpu_pct'] or 0:>7.1f}"
          f"{total:>10.4f}  {top[0]} {top[1]:.4f}")

print("\n=== per-scenario ait zone detail (ms) ===")
for name, r in rows:
    if r is None: continue
    seen = {}
    for z in r["zones"]:
        if z["name"].startswith("ait:"):
            seen[z["name"]] = max(seen.get(z["name"], 0), z["ms"])
    if not seen: continue
    parts = "  ".join(f"{k.replace('ait:','')}={v:.4f}" for k, v in sorted(seen.items(), key=lambda kv: -kv[1])[:7])
    print(f"{name:<22} {parts}")

print("\n=== key counters (per frame) ===")
KEYS = ["ait_exterior_dispatched","ait_exterior_drawn","ait_exterior_enqueued","ait_exterior_doors_shut",
        "ait_boti_exterior_queued","ait_boti_exterior_drawn","ait_boti_blit","ait_boti_draw_flush",
        "ait_model_build","ait_model_build_eager","ait_renderlayer_alloc","ait_boti_door_queued",
        "ait_boti_gallifreyan_queued","ait_boti_trenzalore_queued","ait_boti_rift_queued"]
print(f"{'scenario':<22}" + "".join(f"{k.replace('ait_','').replace('exterior','ext').replace('boti_','b_'):>14}" for k in KEYS))
for name, r in rows:
    if r is None: continue
    print(f"{name:<22}" + "".join(f"{r['counters'].get(k,0):>14.2f}" for k in KEYS))
