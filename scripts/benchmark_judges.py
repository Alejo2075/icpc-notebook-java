#!/usr/bin/env python3
"""Local large-input checks against published AtCoder size limits (not judge timing)."""

import subprocess
import tempfile
import time
from pathlib import Path

from export_main import ROOT, export_main
from smoke_judges import java_tools


def run_case(name, solution, data, expected, timeout=30):
    javac, java = java_tools()
    with tempfile.TemporaryDirectory(prefix="bench-", dir=ROOT / "build") as folder:
        source = Path(folder) / "Main.java"
        export_main(["structures"], source, ROOT / solution)
        subprocess.run([javac, "--release", "17", "-d", folder, str(source)],
                       check=True, capture_output=True, timeout=30)
        start = time.monotonic()
        result = subprocess.run([java, "-Xmx512m", "-cp", folder, "Main"],
                                input=data, text=True, capture_output=True, timeout=timeout, check=True)
        elapsed = time.monotonic() - start
        if result.stdout != expected:
            raise AssertionError(f"{name}: wrong output")
        print(f"OK: {name}, {elapsed:.2f}s locally")


def main():
    (ROOT / "build").mkdir(exist_ok=True)
    n, q = 200_000, 200_000
    dsu_input = f"{n} {q}\n" + "".join(f"0 {i} {i + 1}\n" for i in range(100_000))
    dsu_input += "1 0 100000\n" * 100_000
    run_case("DSU at N=Q=200000", "tests/judges/atcoder-dsu.solve",
             dsu_input, "1\n" * 100_000)

    n, q = 500_000, 500_000
    fenwick_input = f"{n} {q}\n" + "0 " * (n - 1) + "0\n"
    fenwick_input += "".join(f"0 {i} 1\n" for i in range(250_000))
    fenwick_input += "1 0 500000\n" * 250_000
    run_case("Fenwick at N=Q=500000", "tests/judges/atcoder-fenwick.solve",
             fenwick_input, "250000\n" * 250_000)


if __name__ == "__main__":
    main()
