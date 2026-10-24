# Contributing

## Implementation criteria

- Use Java 17 with no runtime dependencies outside the standard library.
- Give methods clear contracts, zero-based indices, and half-open ranges by default.
- Document purpose, complexity, preconditions, edge cases, and numeric limits in English Javadoc.
- Add regression cases and, for subtle algorithms, randomized comparisons against a simple reference.
- Run `scripts/check.sh` and compile all three PDF levels after catalog or LaTeX changes; regenerate all three HTML levels when catalog content changes.
- Register every new library source exactly once in `notebook.json` and place it in the right chapter.
- Assign `beginner`, `intermediate`, and `advanced` profiles cumulatively: a beginner entry belongs to all three; an intermediate entry belongs to the last two; an advanced entry belongs to advanced.
- Add a core idea, worked example, and practice prompt to each catalog entry. Keep examples accurate and small enough to check by hand.
- Declare cross-file dependencies in `requires` and verify `scripts/export_main.py` can build every profile.
- Keep source, comments, documentation, and generated text in English.
- Cite the author, URL, and compatible license for externally derived code. Do not paste source without checking permissions.

## Reference workflow

`notebook.json` defines chapters, learning paths, entry metadata, dependencies, and profiles. `scripts/generate_notebook.py` embeds the current Java source in LaTeX and `scripts/generate_html.py` builds searchable HTML from the same data, so an algorithm has one maintained implementation. Review all three PDFs for readability, broken lines, page count, and event-specific rules. Levels are learning paths, not fixed page counts.

## Edge cases to review

- Intermediate `int` and `long` overflow, not only final results.
- Empty and disconnected graphs, cycles, parallel edges, and negative weights.
- Empty strings, UTF-16 code units, and input outside an ASCII-only contract.
- Duplicate and collinear points, exact predicates, and floating-point tolerance.
- Java allocations, recursion depth, stack size, and realistic memory use.
