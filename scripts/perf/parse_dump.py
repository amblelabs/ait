"""Parse a vanilla client profiler dump into absolute per-frame milliseconds.

The percentages in profiling.txt are relative; only the second one (share of the
whole frame) is usable, and it still has to be multiplied by the frame time.
Per-tick counts in the file are rounded with %.0f and counter averages use integer
division, so both are recomputed here from the raw totals.
"""
import csv, io, re, sys, zipfile

LINE = re.compile(r"^\[(\d+)\]\s+((?:\|\s+)*)([^(]+)\((\d+)/[\d.]+\)\s+-\s+([\d.]+)%/([\d.]+)%")
COUNTER_HEAD = re.compile(r"^-- Counter: (\S+) --")
COUNTER_ROOT = re.compile(r"^\[\d+\]\s*(?:\|\s+)*root total:(\d+)/(\d+)")


def parse(path):
    with zipfile.ZipFile(path) as z:
        text = z.read("client/profiling.txt").decode("utf8", "replace")
        gpu = None
        try:
            rows = list(csv.DictReader(io.StringIO(z.read("client/metrics/gpu.csv").decode("utf8"))))
            vals = [float(r["gpuUtilization"]) for r in rows if r.get("gpuUtilization")]
            gpu = sum(vals) / len(vals) if vals else None
        except KeyError:
            pass

    time_ms = int(re.search(r"Time span: (\d+) ms", text).group(1))
    ticks = int(re.search(r"Tick span: (\d+) ticks", text).group(1))
    frame_ms = time_ms / ticks

    zones, counters = [], {}
    in_counters = False
    current = None
    for raw in text.splitlines():
        head = COUNTER_HEAD.match(raw)
        if head:
            in_counters, current = True, head.group(1)
            continue
        if in_counters and current:
            root = COUNTER_ROOT.match(raw)
            if root:
                # total, not self: the whole subtree's count for this marker
                counters[current] = int(root.group(2)) / ticks
                current = None
            continue
        # "#marker" lines are inline counters in the timing tree, not zones
        m = LINE.match(raw)
        if m and not m.group(3).startswith("#"):
            depth = len(m.group(2).split("|")) - 1
            zones.append({
                "depth": depth,
                "name": m.group(3).strip(),
                "calls_per_frame": int(m.group(4)) / ticks,
                "pct_total": float(m.group(6)),
                "ms": float(m.group(6)) / 100.0 * frame_ms,
            })

    return {"frame_ms": frame_ms, "fps": 1000.0 / frame_ms, "ticks": ticks,
            "gpu_pct": gpu, "zones": zones, "counters": counters}


def main():
    for path in sys.argv[1:]:
        r = parse(path)
        print(f"\n=== {path.split('/')[-1]}")
        print(f"frame {r['frame_ms']:.3f} ms  ({r['fps']:.1f} fps, {r['ticks']} frames)"
              + (f"  gpu {r['gpu_pct']:.1f}%" if r["gpu_pct"] is not None else "  gpu n/a"))

        ait = [z for z in r["zones"] if "ait" in z["name"].lower()]
        if ait:
            print("  -- ait zones --")
            for z in sorted(ait, key=lambda z: -z["ms"]):
                print(f"    {z['ms']:7.4f} ms  {z['pct_total']:6.2f}%  {z['name']}")
        else:
            print("  -- no ait zones present --")

        print("  -- top zones --")
        for z in sorted(r["zones"], key=lambda z: -z["ms"])[:12]:
            print(f"    {z['ms']:7.4f} ms  {z['pct_total']:6.2f}%  {'  ' * z['depth']}{z['name']}")

        if r["counters"]:
            print("  -- counters (per frame) --")
            for k, v in sorted(r["counters"].items(), key=lambda kv: -kv[1]):
                print(f"    {v:10.3f}  {k}")


if __name__ == "__main__":
    main()
