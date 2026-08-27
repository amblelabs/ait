"""Table of every scenario in the manifest: frame time, GPU, ait zones and counters.

The manifest is scenario, dump path, then the perf-verify line recorded at profile time, so a
row can be checked against the state it was actually measured in.
"""
import sys

sys.path.insert(0, "scripts/perf")
from parse_dump import parse

rows = []
for line in open("scripts/perf/manifest.tsv", encoding="utf8"):
    parts = line.rstrip("\n").split("\t")
    if len(parts) < 2:
        continue
    name, path = parts[0], parts[1]
    verify = parts[2] if len(parts) > 2 else ""
    rows.append((name, None if path == "NO_DUMP" else parse(path), verify))


def ait_zones(result):
    """Deduplicate: the same zone name appears at several depths in the profiler tree."""
    best = {}
    for zone in result["zones"]:
        if zone["name"].startswith("ait:"):
            best[zone["name"]] = max(best.get(zone["name"], 0), zone["ms"])
    return best


print(f"{'scenario':<22}{'frame_ms':>9}{'fps':>6}{'gpu%':>6}{'aitMs':>8}  top ait zone")
print("-" * 78)
for name, r, _ in rows:
    if r is None:
        print(f"{name:<22}  NO DUMP")
        continue
    zones = ait_zones(r)
    total = sum(zones.values())
    top = max(zones.items(), key=lambda kv: kv[1]) if zones else ("-", 0.0)
    print(f"{name:<22}{r['frame_ms']:>9.3f}{r['fps']:>6.0f}{r['gpu_pct'] or 0:>6.1f}"
          f"{total:>8.3f}  {top[0]} {top[1]:.4f}")

print("\n=== ait zone detail (ms) ===")
for name, r, _ in rows:
    if r is None:
        continue
    zones = ait_zones(r)
    if not zones:
        continue
    top = sorted(zones.items(), key=lambda kv: -kv[1])[:7]
    print(f"{name:<22} " + "  ".join(f"{k.replace('ait:', '')}={v:.4f}" for k, v in top))

KEYS = ["ait_exterior_dispatched", "ait_exterior_drawn", "ait_exterior_enqueued",
        "ait_exterior_doors_shut", "ait_boti_exterior_queued", "ait_boti_blit",
        "ait_boti_draw_flush", "ait_model_build", "ait_model_build_eager",
        "ait_renderlayer_alloc", "ait_boti_door_queued", "ait_boti_rift_queued",
        "ait_boti_gallifreyan_queued", "ait_star_shine_vertices"]

print("\n=== counters (per frame) ===")
head = "".join(f"{k.replace('ait_', '').replace('exterior', 'ext').replace('boti_', 'b')[:12]:>13}" for k in KEYS)
print(f"{'scenario':<22}{head}")
for name, r, _ in rows:
    if r is None:
        continue
    print(f"{name:<22}" + "".join(f"{r['counters'].get(k, 0):>13.2f}" for k in KEYS))

print("\n=== verified state at profile time ===")
for name, _, verify in rows:
    print(f"{name:<22} {verify.replace('PERF-VERIFY ', '')}")
