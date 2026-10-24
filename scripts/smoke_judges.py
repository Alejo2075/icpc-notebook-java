#!/usr/bin/env python3
"""Compile standalone submissions and check official public judge samples locally."""

import json
import os
import shutil
import subprocess
import tempfile
from pathlib import Path

from export_main import ROOT, export_main


def java_tools():
    homes = [os.environ.get("JDK_HOME"), str(Path.home() / ".jdks/jdk17")]
    for home in filter(None, homes):
        javac = Path(home) / "bin/javac"
        java = Path(home) / "bin/java"
        if javac.is_file() and java.is_file():
            return str(javac), str(java)
    return shutil.which("javac") or "javac", shutil.which("java") or "java"


def main():
    cases = json.loads((ROOT / "tests/judges/cases.json").read_text(encoding="utf-8"))
    javac, java = java_tools()
    (ROOT / "build").mkdir(exist_ok=True)
    count = 0
    for case in cases:
        with tempfile.TemporaryDirectory(prefix="judge-", dir=ROOT / "build") as folder:
            source = Path(folder) / "Main.java"
            export_main(case["include"], source, ROOT / case["solution"])
            subprocess.run([javac, "--release", "17", "-d", folder, str(source)], check=True, timeout=30)
            for sample in case["samples"]:
                result = subprocess.run([java, "-cp", folder, "Main"], input=sample["input"],
                                        text=True, capture_output=True, check=True, timeout=15)
                if result.stdout.strip() != sample["output"].strip():
                    raise AssertionError(f"{case['name']}: expected {sample['output']!r}, got {result.stdout!r}")
                count += 1
        print(f"OK: {case['name']}")
    print(f"OK: {count} official samples")


if __name__ == "__main__":
    main()
