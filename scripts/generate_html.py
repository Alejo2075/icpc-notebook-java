#!/usr/bin/env python3
"""Generate a searchable, self-contained HTML notebook from the Java catalog."""

import argparse
import html
import json
import re
import shutil
from pathlib import Path

from generate_notebook import LEVELS, ROOT, entries_for

JAVA_KEYWORDS = set("""abstract assert boolean break byte case catch char class const continue default do
double else enum extends final finally float for if goto implements import instanceof int interface long
native new package private protected public return short static strictfp super switch synchronized this
throw throws transient try void volatile while record sealed permits non-sealed var yield""".split())
JAVA_TOKEN = re.compile(
    r"//[^\n]*|/\*[\s\S]*?\*/|\"(?:\\.|[^\"\\])*\"|'(?:\\.|[^'\\])*'|\b\d+(?:\.\d+)?[fFdDlL]?\b|\b[A-Za-z_$][\w$]*\b"
)


def highlight_java(source: str) -> str:
    """Apply dependency-free token coloring to Java listings."""
    parts = []
    cursor = 0
    for match in JAVA_TOKEN.finditer(source):
        parts.append(html.escape(source[cursor:match.start()]))
        token = match.group(0)
        if token.startswith("//") or token.startswith("/*"):
            style = "comment"
        elif token.startswith('"') or token.startswith("'"):
            style = "string"
        elif token[0].isdigit():
            style = "number"
        elif token in JAVA_KEYWORDS:
            style = "keyword"
        else:
            parts.append(html.escape(token))
            cursor = match.end()
            continue
        parts.append('<span class="' + style + '">' + html.escape(token) + "</span>")
        cursor = match.end()
    parts.append(html.escape(source[cursor:]))
    return "".join(parts)


def render(profile: str, output: Path, selected: set[str] | None = None) -> int:
    if profile not in LEVELS:
        raise ValueError("profile must be beginner, intermediate, or advanced")
    catalog = json.loads((ROOT / "notebook.json").read_text(encoding="utf-8"))
    chapters = entries_for(catalog, profile, selected or set())
    guide = catalog["profileGuides"][profile]
    notes = catalog["learningNotes"][profile]
    problems = catalog["guidedProblems"][profile]
    count = sum(len(entries) for _, entries in chapters)
    output = output.resolve()
    output.parent.mkdir(parents=True, exist_ok=True)
    shutil.copyfile(ROOT / "ICPC Notebook Java Team Reference Logo.png", output.parent / "logo.png")
    esc = html.escape
    parts = ['''<!doctype html>
<html lang="en"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1">
<title>ICPC Notebook Java - ''' + esc(profile.title()) + '''</title>
<style>
:root{color-scheme:light;--ink:#17253c;--muted:#536276;--accent:#c85e32;--wash:#f1f5f8;--paper:#fff;--code:#f7f9fb}
*{box-sizing:border-box}body{margin:0;background:var(--paper);color:var(--ink);font:16px/1.55 system-ui,-apple-system,"Segoe UI",sans-serif}
a{color:var(--accent)}header{padding:3rem 1rem 2rem;text-align:center;background:linear-gradient(140deg,#f1f5f8,#fff 60%);border-bottom:3px solid var(--accent)}header img{width:min(560px,90vw);height:auto}h1{margin:.8rem 0 .2rem;font-size:clamp(2rem,5vw,3.1rem)}.subtitle,.muted{color:var(--muted)}main{max-width:1100px;margin:auto;padding:1rem 1.3rem 5rem}.toolbar{position:sticky;top:0;z-index:2;background:#ffffffed;padding:.8rem 0;border-bottom:1px solid #d9e0e7;backdrop-filter:blur(8px)}input[type=search]{width:min(100%,38rem);padding:.75rem 1rem;border:1px solid #bbc5d0;border-radius:.5rem;font:inherit}nav{display:flex;flex-wrap:wrap;gap:.5rem;margin:1rem 0}nav a{padding:.25rem .55rem;background:var(--wash);border-radius:.35rem;text-decoration:none;font-size:.9rem}.panel,.metadata{background:var(--wash);border-radius:.6rem;padding:1rem 1.2rem;margin:1rem 0}.goals{padding-left:1.3rem}.entry{border-top:1px solid #d9e0e7;padding:1.3rem 0;scroll-margin-top:6rem}.entry h3{margin:.2rem 0 .6rem}.metadata{display:grid;grid-template-columns:max-content 1fr;gap:.15rem .8rem;font-size:.95rem}.metadata strong{color:var(--accent)}.study{margin:.8rem 0}.study p{margin:.35rem 0}.problem{padding:1rem 0;border-top:1px solid #d9e0e7}.problem strong{color:var(--accent)}details{margin-top:.8rem}summary{cursor:pointer;color:var(--accent);font-weight:650}pre{overflow:auto;padding:1rem;background:var(--code);border:1px solid #e0e6ec;border-radius:.5rem;font:13px/1.45 ui-monospace,SFMono-Regular,Consolas,monospace;tab-size:4}.keyword{color:#173b75;font-weight:700}.comment{color:#64748b;font-style:italic}.string{color:#a54420}.number{color:#7251a3}code{font-family:ui-monospace,SFMono-Regular,Consolas,monospace}.badge{display:inline-block;padding:.12rem .5rem;background:#e8edf2;border-radius:99px;font-size:.8rem;color:var(--muted)}@media(max-width:650px){main{padding:1rem}.metadata{grid-template-columns:1fr}.metadata strong{margin-top:.3rem}}
</style></head><body>
<header><img src="logo.png" alt="ICPC Notebook Java team reference logo"><h1>''' + esc(catalog["title"]) + '''</h1>
<div class="subtitle">''' + esc(catalog["subtitle"]) + '''</div><p><span class="badge">''' + esc(profile.title()) + '''</span> <span class="badge">''' + str(count) + ''' entries</span></p></header><main>
<div class="toolbar"><label for="search"><strong>Search this reference</strong></label><br><input id="search" type="search" placeholder="Search topics, methods, examples, and code" autocomplete="off"></div>
<section class="panel"><h2>Learning path</h2><p><strong>Audience:</strong> ''' + esc(guide["audience"]) + '''</p><p><strong>Prerequisites:</strong> ''' + esc(guide["prerequisites"]) + '''</p><p><strong>Practice:</strong> ''' + esc(guide["practice"]) + '''</p><h3>Learning goals</h3><ul class="goals">''']
    for goal in guide["goals"]:
        parts.append("<li>" + esc(goal) + "</li>")
    parts.append("</ul></section><h2>Problem-solving foundations</h2>")
    for note in notes:
        parts.append('<article class="panel searchable"><h3>' + esc(note["title"]) + "</h3><p>" + esc(note["idea"])
                     + '</p><p><strong>Example:</strong> ' + esc(note["example"])
                     + '</p><p><strong>Try it:</strong> ' + esc(note["exercise"]) + "</p></article>")
    parts.append('<h2>Guided problems</h2>')
    for problem in problems:
        parts.append('<article class="problem searchable"><h3>' + esc(problem["title"]) + "</h3>")
        for label, key in (("Task", "task"), ("Result", "result"), ("Reasoning", "reason"),
                           ("Use", "method"), ("Cost", "cost"), ("Check", "check")):
            parts.append("<p><strong>" + label + ":</strong> " + esc(problem[key]) + "</p>")
        parts.append("</article>")
    parts.append('<h2>Quick reference</h2><div class="panel"><ul>')
    for row in catalog["quickReference"]:
        if LEVELS.index(row.get("minLevel", "beginner").lower()) <= LEVELS.index(profile):
            parts.append("<li><strong>" + esc(row["signal"]) + ":</strong> " + esc(row["choice"])
                         + " <span class=\"muted\">(" + esc(row["cost"]) + ")</span></li>")
    parts.append("</ul></div><h2>Algorithm chapters</h2><nav aria-label=\"Chapter index\">")
    for chapter, entries in chapters:
        if entries:
            parts.append('<a href="#' + esc(chapter["name"].replace(" ", "-").lower()) + '">' + esc(chapter["name"]) + "</a>")
    parts.append("</nav>")
    for chapter, entries in chapters:
        if not entries:
            continue
        chapter_id = chapter["name"].replace(" ", "-").lower()
        parts.append('<section id="' + esc(chapter_id) + '"><h2>' + esc(chapter["name"]) + "</h2><p class=\"muted\">"
                     + esc(chapter["intro"]) + "</p>")
        for entry in entries:
            source = (ROOT / entry["file"]).read_text(encoding="utf-8")
            search_text = " ".join((entry["title"], entry["when"], entry["complexity"],
                                    entry["caution"], json.dumps(entry.get("study", {})), source))
            parts.append('<article class="entry searchable" id="entry-' + esc(entry["id"]) + '" data-search="'
                         + esc(search_text.lower()) + '"><h3>' + esc(entry["title"]) + ' <span class="badge">'
                         + esc(entry["level"]) + "</span></h3><p>" + esc(entry["when"]) + "</p>")
            parts.append('<div class="metadata"><strong>Complexity</strong><span>' + esc(entry["complexity"])
                         + '</span><strong>Caution</strong><span>' + esc(entry["caution"]) + "</span>")
            if entry.get("requires"):
                parts.append('<strong>Dependencies</strong><span>' + esc(", ".join(entry["requires"])) + "</span>")
            parts.append("</div>")
            if "study" in entry:
                study = entry["study"]
                parts.append('<div class="study"><p><strong>Core idea:</strong> ' + esc(study["idea"])
                             + '</p><p><strong>Worked example:</strong> ' + esc(study["example"])
                             + '</p><p><strong>Practice:</strong> ' + esc(study["practice"]) + "</p></div>")
            parts.append("<details><summary>Show Java source</summary><pre><code>" + highlight_java(source) + "</code></pre></details></article>")
        parts.append("</section>")
    parts.append('''<footer class="panel"><strong>Conventions:</strong> zero-based indices and half-open ranges unless documented otherwise. Source code is tested by the repository workflow.</footer>
</main><script>
const box=document.querySelector('#search');const cards=[...document.querySelectorAll('.searchable')];
box.addEventListener('input',()=>{const q=box.value.trim().toLowerCase();for(const card of cards){const text=(card.dataset.search||card.textContent).toLowerCase();card.hidden=!text.includes(q)}});
</script></body></html>''')
    output.write_text("\n".join(parts), encoding="utf-8")
    return count


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--profile", choices=LEVELS, default="beginner")
    parser.add_argument("--include", help="Comma-separated entry IDs")
    parser.add_argument("--output", type=Path)
    args = parser.parse_args()
    target = args.output or ROOT / "output/html" / args.profile / "notebook.html"
    chosen = set(args.include.split(",")) if args.include else None
    count = render(args.profile, target, chosen)
    print(f"Generated {target} with {count} entries")
