#!/usr/bin/env python3
"""Generate LaTeX source and a local logo from the tested Java files."""

import argparse
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
LOGO = ROOT / "ICPC Notebook Java Team Reference Logo.png"


def tex_escape(value: str) -> str:
    return "".join({"\\": r"\textbackslash{}", "&": r"\&", "%": r"\%", "$": r"\$",
                    "#": r"\#", "_": r"\_", "{": r"\{", "}": r"\}",
                    "~": r"\textasciitilde{}", "^": r"\textasciicircum{}"}.get(c, c)
                   for c in value)


def generate(profile: str, selected: set[str], output: Path) -> int:
    catalog = json.loads((ROOT / "notebook.json").read_text(encoding="utf-8"))
    all_ids = {entry["id"] for chapter in catalog["chapters"] for entry in chapter["entries"]}
    unknown = selected - all_ids
    if unknown:
        raise ValueError(f"Unknown entries: {', '.join(sorted(unknown))}")
    chapters = []
    for chapter in catalog["chapters"]:
        entries = [entry for entry in chapter["entries"]
                   if (not selected and profile in entry["profiles"]) or entry["id"] in selected]
        if entries:
            chapters.append((chapter["name"], entries))
    if not chapters:
        raise ValueError("No entries selected")
    output.parent.mkdir(parents=True, exist_ok=True)
    logo_target = output.parent / "logo.png"
    logo_target.write_bytes(LOGO.read_bytes())
    lines = [
        r"\documentclass[10pt,a4paper]{article}",
        r"\usepackage[utf8]{inputenc}",
        r"\usepackage[T1]{fontenc}",
        r"\usepackage[spanish]{babel}",
        r"\usepackage{graphicx,geometry,listings,xcolor,fancyhdr,hyperref}",
        r"\geometry{margin=15mm,headheight=20pt}",
        r"\definecolor{navy}{HTML}{123262}",
        r"\definecolor{orange}{HTML}{DD4B17}",
        r"\lstset{language=Java,basicstyle=\ttfamily\footnotesize,keywordstyle=\color{navy}\bfseries,commentstyle=\color{gray},numbers=left,numberstyle=\tiny\color{gray},numbersep=5pt,columns=fixed,keepspaces=true,breaklines=true,breakatwhitespace=false,breakautoindent=false,breakindent=0pt,frame=single,framerule=0.2pt,tabsize=2,showstringspaces=false}",
        r"\pagestyle{fancy}\fancyhf{}\fancyhead[L]{\footnotesize ICPC Notebook Java}\fancyhead[R]{\footnotesize Team Reference}\fancyfoot[C]{\thepage}",
        r"\hypersetup{colorlinks=true,linkcolor=navy}",
        r"\begin{document}",
        r"\begin{center}",
        r"\includegraphics[width=0.88\linewidth]{logo.png}\\[4mm]",
        r"{\Large\bfseries ICPC Notebook Java}\\[2mm]",
        r"{\color{orange} Team Reference}",
        r"\end{center}",
        r"\vspace{3mm}",
        r"\noindent\textbf{Convenciones:} Java 17; indices desde cero; intervalos semiabiertos salvo indicacion contraria. Los fragmentos se compilan y prueban desde el repositorio.",
        r"\tableofcontents",
    ]
    count = 0
    for chapter_name, entries in chapters:
        lines.append(r"\section{" + tex_escape(chapter_name) + "}")
        for entry in entries:
            source = ROOT / entry["file"]
            if not source.is_file():
                raise FileNotFoundError(source)
            code = source.read_text(encoding="utf-8")
            if r"\end{lstlisting}" in code:
                raise ValueError(f"LaTeX delimiter in {source}")
            lines.extend([
                r"\subsection{" + tex_escape(entry["title"]) + "}",
                r"\noindent\textit{" + tex_escape(entry["note"]) + "}",
                r"\begin{lstlisting}",
                code.rstrip(),
                r"\end{lstlisting}",
            ])
            count += 1
    lines.append(r"\end{document}")
    output.write_text("\n".join(lines) + "\n", encoding="utf-8")
    return count


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--profile", choices=("full", "contest"), default="full")
    parser.add_argument("--include", help="Comma-separated entry IDs; overrides profile")
    parser.add_argument("--output", type=Path, default=ROOT / "build" / "notebook.tex")
    args = parser.parse_args()
    chosen = set(args.include.split(",")) if args.include else set()
    count = generate(args.profile, chosen, args.output.resolve())
    print(f"Generated {args.output} with {count} entries")
