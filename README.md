<div align="center">

<img src="ICPC%20Notebook%20Java%20Team%20Reference%20Logo.png" alt="ICPC Notebook Java team reference logo" width="760">

# ICPC Notebook Java

**A progressive Java 17 learning guide and competition reference for ICPC teams.**

[![Verify notebook](https://github.com/Alejo2075/icpc-notebook-java/actions/workflows/verify.yml/badge.svg)](https://github.com/Alejo2075/icpc-notebook-java/actions/workflows/verify.yml)

</div>

ICPC Notebook Java combines a reusable Java algorithm library, a guided curriculum, and tools for preparing judge submissions. It serves teams at three stages: beginner, intermediate, and advanced. Each stage has its own searchable HTML notebook, styled LaTeX source, and ready-to-print PDF.

Algorithm implementations live in topic folders under [`src/main/java/notebook`](src/main/java/notebook): basics, data structures, dynamic programming, graphs, trees, strings, mathematics, numerical methods, geometry, and game theory. They share the `notebook` Java package, so existing imports remain simple. The shared [`notebook.json`](notebook.json) catalog describes the topics, learning material, assumptions, examples, complexity, and profile membership. The generators use that catalog and read Java source from the library, so the printed and web references stay connected to the implementation.

## Start with a team level

The profiles are cumulative: Intermediate includes Beginner material, and Advanced includes both earlier levels.

| Profile | For teams who... | Main focus |
| --- | --- | --- |
| **Beginner** | Are learning to turn a problem statement into a working solution. | Contest setup, fast input, variables and loops, arrays, maps and sets, stacks, recursion, brute force, sorting, binary search, greedy intervals, introductory dynamic programming, basic math, strings, and BFS on graphs and grids. |
| **Intermediate** | Know the fundamentals and are choosing standard algorithms to meet constraints. | Two pointers, range queries and data structures, classic DP, graph traversal and shortest paths, MST, matching, number theory, combinatorics, string algorithms, planar geometry, and 3D vectors. |
| **Advanced** | Are combining techniques or solving specialized contest problems. | Flow and cuts, 2-SAT, rollback DSU, heavy-light decomposition, advanced DP, suffix structures, modular algorithms, transforms, exact geometry, KD-trees, and game theory. |

Every profile includes an audience description, prerequisites, learning goals, practice advice, foundational notes, worked examples, guided problems with reasoning, and algorithm references with usage guidance and cautions. The beginner material introduces patterns before the more specialized implementations.

## Topic coverage

The catalog is arranged into twelve chapters:

1. **Contest setup:** standalone `Main` template and reusable fast input.
2. **Fundamentals:** arrays, loops, prefix sums, brute force, maps, sets, stacks, recursion, subsets, two pointers, binary search, sorting, and interval greedy methods.
3. **Range queries and data structures:** DSU, Fenwick and segment trees, 2D prefix sums, sparse tables, lazy propagation, rollback DSU, 2D Fenwick trees, Mo's algorithm, and Li Chao trees.
4. **Dynamic programming:** first DP examples, classic patterns, subset, interval, scheduling and digit DP, and Knuth optimization.
5. **Graphs:** BFS, grid flood fill, DFS, shortest paths, MST, components, bipartite algorithms, 0-1 BFS, Floyd-Warshall, Euler tours, SCC, Bellman-Ford, LCA, max flow, bridges, articulation points, 2-SAT, min-cost flow, matching, global min-cut, Matrix-Tree theorem, assignment, and binary graph cuts.
6. **Trees:** diameter, Euler tour, and heavy-light decomposition.
7. **Strings:** character processing, direct matching, KMP, Manacher, Boyer-Moore, Z algorithm, tries, rolling hash, suffix arrays and LCP, Aho-Corasick, cyclic rotation, and suffix automata.
8. **Number theory and combinatorics:** GCD, divisors, modular arithmetic, primality, CRT, factorization, totient, modular inverse, binomial coefficients, matrices, Catalan numbers, discrete logarithm, modular roots, Möbius function, derangements, Stirling numbers, partitions, and Burnside's lemma.
9. **Polynomial methods:** number-theoretic transform (NTT).
10. **Numerical methods:** Gaussian elimination, determinants, subset zeta/Möbius transforms, XOR transforms, and FFT convolution.
11. **Geometry:** distances, polygons, convex hull, closest pair, exact predicates and intersections, circles, 2D KD-tree, 3D vectors, and spherical distance.
12. **Game theory:** Nim, mex, Grundy numbers, and minimax.

The exact algorithms available at each level are listed in `notebook.json`; profile membership is cumulative and can evolve with the catalog.

## Use the references

The generated references are checked into the repository:

| Profile | Searchable HTML | LaTeX source | PDF |
| --- | --- | --- | --- |
| Beginner | [Open HTML](output/html/beginner/notebook.html) | [Source](output/latex/beginner/notebook.tex) | [Download PDF](output/pdf/icpc-notebook-java-beginner.pdf) |
| Intermediate | [Open HTML](output/html/intermediate/notebook.html) | [Source](output/latex/intermediate/notebook.tex) | [Download PDF](output/pdf/icpc-notebook-java-intermediate.pdf) |
| Advanced | [Open HTML](output/html/advanced/notebook.html) | [Source](output/latex/advanced/notebook.tex) | [Download PDF](output/pdf/icpc-notebook-java-advanced.pdf) |

The HTML references include topic search, navigation, Java syntax coloring, and collapsible source listings. They are self-contained apart from the adjacent logo image and can be opened locally. The LaTeX references include the source listings and team-reference styling; each output directory includes the logo needed to compile its `.tex` file.

## Build the notebooks

Requirements: Java 17 or newer and Python 3. PDF builds also require `latexmk` with TeX Live, `pdflatex`, or Tectonic. `pdfinfo` is needed only when enforcing a page limit. Set `JDK_HOME` if Java is not on `PATH`; on macOS, the scripts also check `~/.jdks/jdk17`.

```sh
# Build and validate the Java library, tests, and standalone exports.
scripts/check.sh

# Generate and publish a styled LaTeX source and PDF.
scripts/build-notebook.sh beginner
scripts/build-notebook.sh intermediate
scripts/build-notebook.sh advanced

# Generate the matching searchable HTML pages.
python3 scripts/generate_html.py --profile beginner
python3 scripts/generate_html.py --profile intermediate
python3 scripts/generate_html.py --profile advanced
```

Build outputs go to `output/latex/<profile>/`, `output/pdf/`, and `output/html/<profile>/`. To enforce a local PDF page limit, provide it as the second argument, for example `scripts/build-notebook.sh beginner 40`. Check the rules for your contest before printing or submitting a notebook.

Generate a LaTeX source without compiling a PDF, or select exact catalog entries by ID:

```sh
python3 scripts/generate_notebook.py --profile beginner --output build/custom/notebook.tex
python3 scripts/generate_notebook.py --profile advanced --include template,structures,graphs --output build/custom/notebook.tex
```

The `full` and `contest` profile names are retained as aliases for `advanced` and `intermediate` in the LaTeX generator.

## Export a single-file judge submission

The exporter assembles the selected implementations and their dependencies into one Java file with a public `Main` class and no `notebook` package. A `.solve` file contains statements for the body of `main`.

```sh
python3 scripts/export_main.py --profile intermediate \
  --solution tests/judges/atcoder-dsu.solve \
  --output build/Main.java
javac --release 17 build/Main.java
```

Choose `--profile beginner`, `intermediate`, or `advanced`; use `--include id1,id2` to select catalog IDs directly. `--include` takes precedence over a profile. If `--solution` is omitted, the exporter creates an empty `main` body.

For local development, compile the library and import a class directly:

```java
import notebook.BeginnerArrays;

class Example {
    public static void main(String[] args) {
        int[] values = {3, -2, 5};
        long[] prefix = BeginnerArrays.prefixSums(values);
        System.out.println(BeginnerArrays.rangeSum(prefix, 1, 3)); // 3
    }
}
```

## Conventions and assumptions

- Vertices and array indices are zero-based.
- Ranges are half-open `[left, right)` unless an entry documents otherwise.
- Read each method's Javadoc and catalog caution before use. Implementations can have assumptions about edge weights, coordinate ranges, moduli, recursion depth, precision, or input size.
- Check numeric bounds and memory use against the problem constraints; Java `long`, floating-point arithmetic, and recursion each have practical limits.
- Prefer the simplest algorithm that fits the constraints, and validate complex methods against small brute-force cases when possible.

## Verification and project workflow

[`scripts/check.sh`](scripts/check.sh) compiles the library and template, runs Java regression tests, runs Python generator/exporter tests, compiles standalone exports for all three profiles and a selected topic set, and runs judge-example smoke cases. The judge examples and `.solve` files are in [`tests/judges`](tests/judges). [`scripts/benchmark_judges.py`](scripts/benchmark_judges.py) provides local DSU and Fenwick measurements.

GitHub Actions runs the verification workflow, builds all three PDFs, generates all three HTML references, and uploads the generated artifacts. Passing tests increases confidence but cannot guarantee that a method fits every judge's time, memory, and numeric limits.

See [`CONTRIBUTING.md`](CONTRIBUTING.md) for contribution and catalog guidance. The project uses the [MIT License](LICENSE). The logo and judge examples may have separate terms; consult their original sources before reusing them outside this repository.
