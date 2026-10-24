#!/usr/bin/env python3
"""Assemble a standalone Main.java from the tested notebook sources."""

import argparse
import json
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
TEMPLATE = ROOT / "src/template/Main.java"
MARKER = "        // Solve the problem here. Read with in.nextInt(), write with out.println()."
PROFILE_ALIASES = {"full": "advanced", "contest": "intermediate"}


def catalog_entries():
    catalog = json.loads((ROOT / "notebook.json").read_text(encoding="utf-8"))
    return {entry["id"]: entry for chapter in catalog["chapters"] for entry in chapter["entries"]}


def resolve_ids(selected, entries):
    result = []
    visiting = set()

    def visit(entry_id):
        if entry_id not in entries:
            raise ValueError(f"Unknown entry: {entry_id}")
        if entry_id in result:
            return
        if entry_id in visiting:
            raise ValueError(f"Dependency cycle at {entry_id}")
        visiting.add(entry_id)
        for required in entries[entry_id].get("requires", []):
            visit(required)
        visiting.remove(entry_id)
        result.append(entry_id)

    for entry_id in selected:
        visit(entry_id)
    return [entry_id for entry_id in result if entry_id != "template"]


def export_main(selected, output, solution=None):
    entries = catalog_entries()
    selected = resolve_ids(selected, entries)
    template = TEMPLATE.read_text(encoding="utf-8")
    if template.count(MARKER) != 1:
        raise ValueError("Main template marker missing or duplicated")
    if solution is not None:
        lines = solution.read_text(encoding="utf-8").strip("\n").splitlines()
        if any(re.match(r"\s*(package|import)\s", line) for line in lines):
            raise ValueError("Solution must contain statements for main(), not imports or a package")
        template = template.replace(MARKER, "\n".join("        " + line for line in lines))

    imports = set(re.findall(r"^import [^;]+;$", template, flags=re.MULTILINE))
    bodies = []
    for entry_id in selected:
        source = (ROOT / entries[entry_id]["file"]).read_text(encoding="utf-8")
        source = re.sub(r"^package notebook;\s*\n", "", source, count=1)
        if re.search(r"^package\s", source, flags=re.MULTILINE):
            raise ValueError(f"Unexpected package in {entry_id}")
        imports.update(re.findall(r"^import [^;]+;$", source, flags=re.MULTILINE))
        source = re.sub(r"^import [^;]+;\s*\n", "", source, flags=re.MULTILINE)
        source, count = re.subn(r"^public\s+(?=(?:final\s+)?class\s)", "", source, count=1, flags=re.MULTILINE)
        if count != 1:
            raise ValueError(f"Expected exactly one public top-level class in {entry_id}")
        bodies.append(f"// Notebook entry: {entry_id}\n{source.strip()}")

    template = re.sub(r"^import [^;]+;\s*\n", "", template, flags=re.MULTILINE).lstrip()
    result = "\n".join(sorted(imports)) + "\n\n" + template.rstrip() + "\n"
    if bodies:
        result += "\n" + "\n\n".join(bodies) + "\n"
    output = output.resolve()
    if output == TEMPLATE:
        raise ValueError("Cannot overwrite the source template")
    output.parent.mkdir(parents=True, exist_ok=True)
    output.write_text(result, encoding="utf-8")
    return selected


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--profile", choices=("beginner", "intermediate", "advanced", "full", "contest"), default="intermediate")
    parser.add_argument("--include", help="Comma-separated notebook IDs; overrides profile")
    parser.add_argument("--solution", type=Path, help="Java statements inserted inside main()")
    parser.add_argument("--output", type=Path, default=ROOT / "build/Main.java")
    args = parser.parse_args()
    entries = catalog_entries()
    profile = PROFILE_ALIASES.get(args.profile, args.profile)
    ids = args.include.split(",") if args.include else [
        entry_id for entry_id, entry in entries.items() if profile in entry["profiles"]
    ]
    result = export_main(ids, args.output, args.solution)
    print(f"Generated {args.output} with {len(result)} library entries")
