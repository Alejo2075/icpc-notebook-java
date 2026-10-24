#!/bin/sh
set -eu
cd "$(dirname "$0")/.."

PROFILE="${1:-full}"
if [ "$PROFILE" != full ] && [ "$PROFILE" != contest ]; then
    echo "Usage: scripts/build-notebook.sh [full|contest] [maximum-pages]" >&2
    exit 2
fi
MAX_PAGES="${2:-0}"
scripts/check.sh
python3 scripts/generate_notebook.py --profile "$PROFILE" --output "build/$PROFILE/notebook.tex"

if command -v latexmk >/dev/null 2>&1; then
    (cd "build/$PROFILE" && latexmk -pdf -interaction=nonstopmode -halt-on-error notebook.tex)
elif command -v tectonic >/dev/null 2>&1; then
    (cd "build/$PROFILE" && tectonic notebook.tex)
elif command -v pdflatex >/dev/null 2>&1; then
    (cd "build/$PROFILE" && pdflatex -interaction=nonstopmode -halt-on-error notebook.tex >/dev/null && pdflatex -interaction=nonstopmode -halt-on-error notebook.tex >/dev/null)
else
    echo "LaTeX is missing. Install latexmk/TeX Live, Tectonic, or pdflatex." >&2
    exit 1
fi

if [ "$MAX_PAGES" -gt 0 ]; then
    if ! command -v pdfinfo >/dev/null 2>&1; then
        echo "pdfinfo is required to enforce a page limit" >&2
        exit 1
    fi
    PAGES="$(pdfinfo "build/$PROFILE/notebook.pdf" | awk '/^Pages:/ {print $2}')"
    if [ "$PAGES" -gt "$MAX_PAGES" ]; then
        echo "Notebook has $PAGES pages, exceeds the $MAX_PAGES page limit" >&2
        exit 1
    fi
fi
mkdir -p output/pdf
cp "build/$PROFILE/notebook.pdf" "output/pdf/icpc-notebook-java-$PROFILE.pdf"
echo "Ready: build/$PROFILE/notebook.tex and output/pdf/icpc-notebook-java-$PROFILE.pdf"
