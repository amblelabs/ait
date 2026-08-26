"""Minimal rcon driver for the AIT profiling harness.

Reads a scenario file: one command per line, blank lines and # comments ignored,
"SLEEP <seconds>" waits so the world can settle before the next step.
"""
import socket, struct, sys, time

class Rcon:
    def __init__(self, host, port, password):
        self.sock = socket.create_connection((host, port), timeout=30)
        self.rid = 0
        if self._cmd(3, password) is None:
            raise SystemExit("rcon auth failed")

    def _send(self, kind, body):
        self.rid += 1
        payload = struct.pack("<ii", self.rid, kind) + body.encode("utf8") + b"\x00\x00"
        self.sock.sendall(struct.pack("<i", len(payload)) + payload)
        return self.rid

    def _read(self):
        raw = self.sock.recv(4)
        if len(raw) < 4:
            return None, None, None
        size = struct.unpack("<i", raw)[0]
        data = b""
        while len(data) < size:
            data += self.sock.recv(size - len(data))
        rid, kind = struct.unpack("<ii", data[:8])
        return rid, kind, data[8:-2].decode("utf8", "replace")

    def _cmd(self, kind, body):
        sent = self._send(kind, body)
        rid, _, text = self._read()
        return None if rid == -1 else text

    def run(self, command):
        return self._cmd(2, command)


def main():
    host, port, password, scenario = sys.argv[1], int(sys.argv[2]), sys.argv[3], sys.argv[4]
    rcon = Rcon(host, port, password)

    for raw in open(scenario, encoding="utf8"):
        line = raw.strip()
        if not line or line.startswith("#"):
            continue
        if line.upper().startswith("SLEEP "):
            seconds = float(line.split()[1])
            print(f"... sleep {seconds}s", flush=True)
            time.sleep(seconds)
            continue
        reply = rcon.run(line)
        print(f"> {line}\n  {reply.strip() if reply else ''}", flush=True)


if __name__ == "__main__":
    main()
