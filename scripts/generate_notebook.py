#!/usr/bin/env python3
"""Build a styled LaTeX team reference directly from the Java source catalog."""

import argparse
import json
import shutil
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
LOGO = ROOT / "ICPC Notebook Java Team Reference Logo.png"
LEVELS = ("beginner", "intermediate", "advanced")
ALIASES = {"full": "advanced", "contest": "intermediate"}


def escape(value):
    """Escape catalog prose before inserting it into LaTeX."""
    mapping = {
        "\\": r"\textbackslash{}", "&": r"\&", "%": r"\%", "$": r"\$",
        "#": r"\#", "_": r"\_", "{": r"\{", "}": r"\}",
        "~": r"\textasciitilde{}", "^": r"\textasciicircum{}",
    }
    return "".join(mapping.get(char, char) for char in str(value))


PREAMBLE = r"""\documentclass[10pt,letterpaper]{article}
\usepackage[english]{babel}
\usepackage[T1]{fontenc}
\usepackage[utf8]{inputenc}
\usepackage{lmodern,microtype}
\usepackage[margin=0.73in,headheight=15pt]{geometry}
\usepackage{graphicx,xcolor,booktabs,longtable,array}
\usepackage{listings,tcolorbox,titlesec,fancyhdr,hyperref,needspace}
\definecolor{ink}{HTML}{17253C}
\definecolor{accent}{HTML}{C85E32}
\definecolor{muted}{HTML}{536276}
\definecolor{wash}{HTML}{F1F5F8}
\definecolor{codebg}{HTML}{F7F9FB}
\hypersetup{colorlinks=true,linkcolor=ink,urlcolor=accent,pdftitle={ICPC Notebook Java Team Reference}}
\titleformat{\section}{\Large\bfseries\color{ink}}{\thesection}{0.7em}{}[\color{accent}\titlerule]
\titleformat{\subsection}{\large\bfseries\color{ink}}{\thesubsection}{0.7em}{}
\titlespacing*{\section}{0pt}{1.5em}{0.8em}
\titlespacing*{\subsection}{0pt}{1.2em}{0.4em}
\pagestyle{fancy}
\fancyhf{}
\fancyhead[L]{\small\color{muted}ICPC NOTEBOOK JAVA}
\fancyhead[R]{\small\color{muted}TEAM REFERENCE}
\fancyfoot[L]{\small\color{muted}Java 17}
\fancyfoot[R]{\small\color{muted}\thepage}
\renewcommand{\headrulewidth}{0.4pt}
\renewcommand{\footrulewidth}{0pt}
\lstdefinestyle{notebook}{language=Java,basicstyle=\ttfamily\footnotesize,
  keywordstyle=\bfseries\color{ink},commentstyle=\itshape\color{muted},
  stringstyle=\color{accent},backgroundcolor=\color{codebg},
  frame=single,rulecolor=\color{wash},breaklines=true,breakatwhitespace=true,
  showstringspaces=false,tabsize=4,columns=fullflexible,keepspaces=true,
  xleftmargin=0.5em,xrightmargin=0.5em,aboveskip=0.8em,belowskip=1.1em}
\lstset{style=notebook}
\newtcolorbox{guidebox}{colback=wash,colframe=wash,boxrule=0pt,
  arc=2pt,left=9pt,right=9pt,top=7pt,bottom=7pt}
\newcommand{\entrylabel}[1]{\textcolor{accent}{\bfseries #1}}
\begin{document}
\pagenumbering{roman}
"""


def entries_for(catalog, profile, selected):
    all_entries = {entry["id"]: entry for chapter in catalog["chapters"] for entry in chapter["entries"]}
    if selected:
        unknown = selected - all_entries.keys()
        if unknown:
            raise ValueError("Unknown entries: " + ", ".join(sorted(unknown)))
        chosen = selected
    else:
        chosen = {key for key, entry in all_entries.items() if profile in entry["profiles"]}
    return [(chapter, [entry for entry in chapter["entries"] if entry["id"] in chosen])
            for chapter in catalog["chapters"]]


def generate(profile: str, selected: set[str], output: Path) -> int:
    """Write LaTeX and its logo; return the number of included entries."""
    profile = ALIASES.get(profile, profile)
    if profile not in LEVELS:
        raise ValueError("profile must be beginner, intermediate, or advanced")
    catalog = json.loads((ROOT / "notebook.json").read_text(encoding="utf-8"))
    chapters = entries_for(catalog, profile, selected)
    guide = catalog["profileGuides"][profile]
    count = sum(len(entries) for _, entries in chapters)
    output = output.resolve()
    output.parent.mkdir(parents=True, exist_ok=True)
    shutil.copyfile(LOGO, output.parent / "logo.png")
    parts = [PREAMBLE]
    parts.append(r"\hypersetup{pageanchor=false}\begin{titlepage}\thispagestyle{empty}\centering" + "\n")
    parts.append(r"\vspace*{1.0in}\includegraphics[width=0.82\textwidth]{logo.png}\par" + "\n")
    parts.append(r"\vspace{0.8in}{\Huge\bfseries\color{ink} " + escape(catalog["title"]) + r"}\par" + "\n")
    parts.append(r"\vspace{0.25in}{\Large\color{muted} " + escape(catalog["subtitle"]) + r"}\par" + "\n")
    parts.append(r"\vfill\color{accent}\rule{0.75\textwidth}{1.5pt}\par" + "\n")
    parts.append(r"\vspace{0.2in}\large A progressive algorithm reference for team practice\par" + "\n")
    parts.append(r"\vspace{0.12in}\small Profile: " + escape(profile.title()) + r" \quad | \quad " + str(count) + r" entries\par" + "\n")
    parts.append(r"\end{titlepage}\hypersetup{pageanchor=true}\tableofcontents\clearpage\pagenumbering{arabic}" + "\n")
    parts.append(r"\section*{How to use this reference}\addcontentsline{toc}{section}{How to use this reference}" + "\n")
    parts.append("Read the algorithm's use case, cost, and caution before copying its code. "
                 "The listing is the same Java source compiled by the repository tests. "
                 "Use the exporter to combine the selected classes into one judge-ready Main.java.\n\n")
    parts.append(r"\subsection*{Learning path: " + escape(profile.title()) + r"}\addcontentsline{toc}{subsection}{Learning path}" + "\n")
    parts.append(r"\begin{guidebox}\entrylabel{Who this is for}\quad " + escape(guide["audience"]) + r"\par" + "\n")
    parts.append(r"\entrylabel{Before starting}\quad " + escape(guide["prerequisites"]) + r"\par" + "\n")
    parts.append(r"\entrylabel{Practice method}\quad " + escape(guide["practice"]) + r"\end{guidebox}" + "\n")
    parts.append(r"\textbf{Learning goals}\begin{itemize}" + "\n")
    for goal in guide["goals"]:
        parts.append(r"\item " + escape(goal) + "\n")
    parts.append(r"\end{itemize}" + "\n")
    parts.append(r"\begin{guidebox}\textbf{Conventions}\begin{itemize}" + "\n")
    for convention in catalog["conventions"]:
        parts.append(r"\item " + escape(convention) + "\n")
    parts.append(r"\end{itemize}\end{guidebox}" + "\n")
    parts.append(r"\subsection*{Choosing an algorithm}\addcontentsline{toc}{subsection}{Choosing an algorithm}" + "\n")
    parts.append(r"\small\begin{longtable}{>{\raggedright\arraybackslash}p{0.27\linewidth}>{\raggedright\arraybackslash}p{0.29\linewidth}>{\raggedright\arraybackslash}p{0.34\linewidth}}\toprule" + "\n")
    parts.append(r"\textbf{Problem signal} & \textbf{Choice} & \textbf{Typical cost}\\\midrule\endhead" + "\n")
    for row in catalog["quickReference"]:
        if LEVELS.index(row.get("minLevel", "beginner").lower()) > LEVELS.index(profile):
            continue
        parts.append(" & ".join(escape(row[key]) for key in ("signal", "choice", "cost")) + r"\\" + "\n")
    parts.append(r"\bottomrule\end{longtable}\normalsize" + "\n")
    parts.append(r"\subsection*{Java contest checklist}\addcontentsline{toc}{subsection}{Java contest checklist}" + "\n")
    parts.append(r"\begin{guidebox}\begin{itemize}" + "\n")
    for item in (
        "Check time and memory limits before choosing object-heavy structures.",
        "Use long for sums, distances, products, and counts that may exceed int.",
        "Check whether input is ASCII tokens or requires Unicode handling.",
        "Watch recursion depth on long paths; use an explicit stack if needed.",
        "Confirm whether edges are directed, whether weights can be negative, and whether ranges are inclusive.",
        "Test empty, singleton, duplicate, disconnected, and boundary cases.",
    ):
        parts.append(r"\item " + escape(item) + "\n")
    parts.append(r"\end{itemize}\end{guidebox}" + "\n")
    parts.append(r"\section*{Problem-solving foundations}\addcontentsline{toc}{section}{Problem-solving foundations}" + "\n")
    for note in catalog["learningNotes"][profile]:
        parts.append(r"\subsection*{" + escape(note["title"]) + "}\n")
        parts.append(escape(note["idea"]) + r"\par" + "\n")
        parts.append(r"\begin{guidebox}\entrylabel{Example}\quad " + escape(note["example"]) + r"\par" + "\n")
        parts.append(r"\entrylabel{Try it}\quad " + escape(note["exercise"]) + r"\end{guidebox}" + "\n")
    parts.append(r"\clearpage\section*{Guided problems}\addcontentsline{toc}{section}{Guided problems}" + "\n")
    for problem in catalog["guidedProblems"][profile]:
        parts.append(r"\subsection*{" + escape(problem["title"]) + "}\n")
        for label, key in (("Task", "task"), ("Result", "result"), ("Reasoning", "reason"),
                           ("Use", "method"), ("Cost", "cost"), ("Check", "check")):
            parts.append(r"\noindent\entrylabel{" + label + r"}\quad " + escape(problem[key]) + r"\par" + "\n")
    parts.append(r"\clearpage" + "\n")
    for chapter, entries in chapters:
        if not entries:
            continue
        parts.append(r"\Needspace{10\baselineskip}\section{" + escape(chapter["name"]) + "}\n")
        parts.append(r"\begin{guidebox}" + escape(chapter["intro"]) + r"\end{guidebox}" + "\n")
        for entry in entries:
            parts.append(r"\Needspace{12\baselineskip}\subsection{" + escape(entry["title"]) + "}\n")
            parts.append(r"\begin{guidebox}\small" + "\n")
            for label, key in (("Level", "level"), ("Use when", "when"), ("Cost", "complexity"), ("Watch for", "caution")):
                parts.append(r"\entrylabel{" + label + r"}\quad " + escape(entry[key]) + r"\par" + "\n")
            if entry.get("requires"):
                parts.append(r"\entrylabel{Dependencies}\quad " + escape(", ".join(entry["requires"])) + r"\par" + "\n")
            parts.append(r"\end{guidebox}" + "\n")
            if "study" in entry:
                study = entry["study"]
                parts.append(r"\noindent\textbf{Core idea.} " + escape(study["idea"]) + r"\par" + "\n")
                parts.append(r"\noindent\textbf{Worked example.} " + escape(study["example"]) + r"\par" + "\n")
                parts.append(r"\noindent\textbf{Practice.} " + escape(study["practice"]) + r"\par" + "\n")
            source = (ROOT / entry["file"]).read_text(encoding="utf-8")
            if "\\end{lstlisting}" in source:
                raise ValueError(f"Unsafe listing delimiter in {entry['file']}")
            parts.append(r"\begin{lstlisting}" + "\n" + source.rstrip() + "\n" + r"\end{lstlisting}" + "\n")
    parts.append(r"\end{document}" + "\n")
    output.write_text("".join(parts), encoding="utf-8")
    return count


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--profile", choices=(*LEVELS, *ALIASES), default="beginner")
    parser.add_argument("--include", help="Comma-separated entry IDs; overrides the profile")
    parser.add_argument("--output", type=Path, default=ROOT / "build/beginner/notebook.tex")
    args = parser.parse_args()
    chosen = set(args.include.split(",")) if args.include else set()
    count = generate(args.profile, chosen, args.output)
    print(f"Generated {args.output} with {count} entries")
