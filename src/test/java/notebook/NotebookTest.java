package notebook;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/** Dependency-free regression and differential tests. Run with -ea. */
public final class NotebookTest {
    private static int checks;
    private static void eq(Object actual, Object expected) {
        checks++;
        if (!Objects.equals(actual, expected)) throw new AssertionError("expected " + expected + ", got " + actual);
    }
    private static void near(double actual, double expected) {
        checks++;
        if (Math.abs(actual - expected) > 1e-8) throw new AssertionError("expected " + expected + ", got " + actual);
    }
    private static void ok(boolean condition) {
        checks++;
        if (!condition) throw new AssertionError();
    }

    public static void main(String[] args) {
        Random random = new Random(2075);
        eq(SearchingSorting.binarySearch(new int[] {1, 3, 5}, 3), 1);
        eq(SearchingSorting.ternarySearch(new int[] {1, 3, 5}, 4), -1);
        eq(SearchingSorting.lowerBound(new int[] {1, 3, 3, 5}, 3), 1);
        char[] chars = {'\uffff', 'a', '\u0100'};
        SearchingSorting.countingSort(chars);
        ok(Arrays.equals(chars, new char[] {'a', '\u0100', '\uffff'}));
        for (int t = 0; t < 500; t++) {
            int[] a = random.ints(random.nextInt(30), -20, 21).sorted().toArray();
            int key = random.nextInt(45) - 22;
            int binary = SearchingSorting.binarySearch(a, key);
            int ternary = SearchingSorting.ternarySearch(a, key);
            eq(binary >= 0, Arrays.binarySearch(a, key) >= 0);
            eq(ternary >= 0, Arrays.binarySearch(a, key) >= 0);
            int lower = SearchingSorting.lowerBound(a, key);
            ok(lower == a.length || a[lower] >= key);
            ok(lower == 0 || a[lower - 1] < key);
        }

        eq(DynamicProgramming.coinChange(new int[] {1, 2, 5}, 5), 4L);
        eq(DynamicProgramming.countWays(3), 4L);
        eq(DynamicProgramming.editDistance("kitten", "sitting"), 3);
        eq(DynamicProgramming.knapsack(5, new int[] {2, 3, 4}, new int[] {3, 4, 5}), 7L);
        eq(DynamicProgramming.lcs("abcde", "ace"), 3);
        eq(DynamicProgramming.lis(new int[] {3, 1, 2, 5, 4}), 3);
        eq(DynamicProgramming.maxProductCutting(10), 36L);
        eq(DynamicProgramming.minimumPartition(new int[] {1, 6, 11, 5}), 1);
        eq(DynamicProgramming.minimumPartition(new int[] {1, 1}), 0);
        eq(DynamicProgramming.subsetSum(new int[] {3, 7, 10}, 13), true);
        for (int t = 0; t < 300; t++) {
            int[] a = random.ints(random.nextInt(11), 0, 12).toArray();
            int sum = Arrays.stream(a).sum(), best = sum;
            boolean[] possible = new boolean[sum + 1];
            for (int mask = 0; mask < (1 << a.length); mask++) {
                int s = 0;
                for (int i = 0; i < a.length; i++) if ((mask & (1 << i)) != 0) s += a[i];
                possible[s] = true;
                best = Math.min(best, Math.abs(sum - 2 * s));
            }
            eq(DynamicProgramming.minimumPartition(a), best);
            for (int s = 0; s <= sum; s++) eq(DynamicProgramming.subsetSum(a, s), possible[s]);
        }

        eq(StringsAlgorithms.kmp("ababa", "aba"), List.of(0, 2));
        eq(StringsAlgorithms.boyerMoore("ababa", "aba"), List.of(0, 2));
        eq(StringsAlgorithms.manacher("babad").length(), 3);
        eq(StringsAlgorithms.manacher("abba"), "abba");
        eq(StringsAlgorithms.manacher(""), "");
        for (int t = 0; t < 400; t++) {
            String text = randomString(random, random.nextInt(18));
            String pattern = randomString(random, random.nextInt(6));
            List<Integer> matches = new ArrayList<>();
            for (int i = 0; i + pattern.length() <= text.length(); i++)
                if (text.startsWith(pattern, i)) matches.add(i);
            eq(StringsAlgorithms.kmp(text, pattern), matches);
            eq(StringsAlgorithms.boyerMoore(text, pattern), matches);
            int best = 0;
            for (int i = 0; i < text.length(); i++) for (int j = i + 1; j <= text.length(); j++) {
                String candidate = text.substring(i, j);
                if (new StringBuilder(candidate).reverse().toString().equals(candidate)) best = Math.max(best, j - i);
            }
            String longest = StringsAlgorithms.manacher(text);
            eq(longest.length(), best);
            eq(new StringBuilder(longest).reverse().toString(), longest);
            int[] z = StringsAlgorithms.zFunction(text);
            for (int i = 1; i < text.length(); i++) {
                int expected = 0;
                while (i + expected < text.length() && text.charAt(expected) == text.charAt(i + expected)) expected++;
                eq(z[i], expected);
            }
        }

        eq(NumberTheory.gcd(54, 24), 6L);
        eq(NumberTheory.lcm(6, 8), 24L);
        long[] eg = NumberTheory.extendedGcd(240, 46);
        eq(240 * eg[1] + 46 * eg[2], eg[0]);
        eq(NumberTheory.modPow(2, 10, 1000), 24L);
        eq(NumberTheory.isPrime(2), true);
        eq(NumberTheory.isPrime(561), false);
        eq(NumberTheory.isPrime(2_305_843_009_213_693_951L), true);
        eq(NumberTheory.sieve(10), List.of(2, 3, 5, 7));
        eq(NumberTheory.divisors(12), List.of(1L, 2L, 3L, 4L, 6L, 12L));
        eq(NumberTheory.factorial(20), new BigInteger("2432902008176640000"));
        eq(Arrays.toString(NumberTheory.chineseRemainder(new long[] {4, 6}, new long[] {2, 4})), "[10, 12]");
        eq(NumberTheory.chineseRemainder(new long[] {2, 4}, new long[] {0, 1}), null);
        near(NumberTheory.newtonRaphson(x -> x*x - 2, x -> 2*x, 1, 1e-12, 20), Math.sqrt(2));
        for (int t = 0; t < 500; t++) {
            long mod = random.nextLong(1, Long.MAX_VALUE);
            long a = random.nextLong(), b = random.nextLong();
            long expected = BigInteger.valueOf(a).multiply(BigInteger.valueOf(b))
                .mod(BigInteger.valueOf(mod)).longValue();
            eq(NumberTheory.modMul(a, b, mod), expected);
        }
        for (int n = 0; n < 1000; n++) eq(NumberTheory.isPrime(n), BigInteger.valueOf(n).isProbablePrime(100));

        eq(Combinatorics.binomial(5, 2), BigInteger.TEN);
        eq(Combinatorics.catalan(4)[4], BigInteger.valueOf(14));
        eq(GameTheory.firstWinsNim(new int[] {1, 2, 3}), false);
        eq(GameTheory.firstWinsNim(new int[] {1, 2, 4}), true);
        eq(GameTheory.grundy(new int[][] {{}, {0}, {0, 1}})[2], 2);
        eq(GameTheory.minimax(new int[] {3, 5}), 5);
        eq(GameTheory.minimax(new int[] {3, 5, 2, 9}), 3);

        Geometry.Point[] points = {new Geometry.Point(0, 0), new Geometry.Point(1, 0),
            new Geometry.Point(1, 1), new Geometry.Point(0, 1)};
        near(Geometry.polygonArea(Arrays.asList(points)), 1);
        near(Geometry.polygonPerimeter(Arrays.asList(points)), 4);
        eq(Geometry.convexHull(points).size(), 4);
        near(Geometry.closestPair(points), 1);
        near(Geometry.farthestPair(points), Math.sqrt(2));
        for (int t = 0; t < 250; t++) {
            Geometry.Point[] p = new Geometry.Point[2 + random.nextInt(12)];
            for (int i = 0; i < p.length; i++) p[i] = new Geometry.Point(random.nextInt(20), random.nextInt(20));
            double min = Double.POSITIVE_INFINITY, max = 0;
            for (int i = 0; i < p.length; i++) for (int j = i + 1; j < p.length; j++) {
                min = Math.min(min, Geometry.distance(p[i], p[j]));
                max = Math.max(max, Geometry.distance(p[i], p[j]));
            }
            near(Geometry.closestPair(p), min);
            near(Geometry.farthestPair(p), max);
        }

        DataStructures.DSU dsu = new DataStructures.DSU(3);
        ok(dsu.union(0, 1)); ok(!dsu.union(1, 0)); eq(dsu.size(0), 2);
        DataStructures.Fenwick fenwick = new DataStructures.Fenwick(3);
        fenwick.add(0, 4); fenwick.add(2, 7); eq(fenwick.rangeSum(0, 3), 11L);
        DataStructures.SegmentTree segment = new DataStructures.SegmentTree(new long[] {4, 2, 7});
        eq(segment.sum(1, 3), 9L); segment.set(1, 5); eq(segment.sum(0, 3), 16L);
        List<List<Integer>> graph = Graphs.newGraph(4);
        graph.get(0).add(1); graph.get(1).add(2);
        eq(Arrays.toString(Graphs.bfs(graph, 0)), "[0, 1, 2, -1]");
        eq(Graphs.dfs(graph, 0), List.of(0, 1, 2));
        eq(Graphs.topologicalSort(graph).size(), 4);
        List<List<Graphs.WeightedEdge>> weighted = new ArrayList<>();
        for (int i = 0; i < 3; i++) weighted.add(new ArrayList<>());
        weighted.get(0).add(new Graphs.WeightedEdge(1, 4));
        weighted.get(0).add(new Graphs.WeightedEdge(2, 10));
        weighted.get(1).add(new Graphs.WeightedEdge(2, 3));
        eq(Arrays.toString(Graphs.dijkstra(weighted, 0)), "[0, 4, 7]");
        eq(Graphs.kruskal(3, List.of(new Graphs.Edge(0, 1, 4), new Graphs.Edge(0, 2, 10),
            new Graphs.Edge(1, 2, 3))), 7L);

        List<List<Integer>> sccGraph = Graphs.newGraph(4);
        sccGraph.get(0).add(1); sccGraph.get(1).add(0);
        sccGraph.get(1).add(2); sccGraph.get(2).add(3); sccGraph.get(3).add(2);
        int[] component = AdvancedGraphs.stronglyConnectedComponents(sccGraph);
        eq(component[0], component[1]); eq(component[2], component[3]); ok(component[0] != component[2]);
        eq(Arrays.toString(AdvancedGraphs.bellmanFord(3, List.of(
            new Graphs.Edge(0, 1, 4), new Graphs.Edge(0, 2, 10), new Graphs.Edge(1, 2, -3)), 0)),
            "[0, 4, 1]");
        List<List<Integer>> tree = Graphs.newGraph(5);
        for (int[] edge : new int[][] {{0, 1}, {0, 2}, {1, 3}, {1, 4}}) {
            tree.get(edge[0]).add(edge[1]); tree.get(edge[1]).add(edge[0]);
        }
        AdvancedGraphs.LCA lca = new AdvancedGraphs.LCA(tree, 0);
        eq(lca.query(3, 4), 1); eq(lca.query(2, 4), 0);
        AdvancedGraphs.Dinic flow = new AdvancedGraphs.Dinic(4);
        flow.addEdge(0, 1, 3); flow.addEdge(0, 2, 2);
        flow.addEdge(1, 2, 1); flow.addEdge(1, 3, 2); flow.addEdge(2, 3, 4);
        eq(flow.maxFlow(0, 3), 5L);
        eq(UndirectedCuts.find(4, List.of(new UndirectedCuts.Edge(0, 1),
            new UndirectedCuts.Edge(1, 2), new UndirectedCuts.Edge(1, 3))).bridges(), List.of(0, 1, 2));
        eq(UndirectedCuts.find(4, List.of(new UndirectedCuts.Edge(0, 1),
            new UndirectedCuts.Edge(1, 2), new UndirectedCuts.Edge(1, 3))).articulationPoints(), List.of(1));
        eq(UndirectedCuts.find(2, List.of(new UndirectedCuts.Edge(0, 1),
            new UndirectedCuts.Edge(0, 1))).bridges(), List.of());
        AdvancedStrings.Trie trie = new AdvancedStrings.Trie();
        trie.add("ab"); trie.add("ab"); trie.add("abc");
        eq(trie.count("ab"), 2); eq(trie.count("a"), 0); ok(trie.hasPrefix("a"));
        AdvancedStrings.RollingHash hash = new AdvancedStrings.RollingHash("ababa");
        eq(hash.hash(0, 3), hash.hash(2, 5));
        ok(hash.hash(0, 2) != hash.hash(1, 3));
        for (int t = 0; t < 120; t++) {
            int n = 2 + random.nextInt(6);
            List<List<Integer>> directed = Graphs.newGraph(n);
            for (int a = 0; a < n; a++) for (int b = 0; b < n; b++)
                if (random.nextInt(4) == 0) directed.get(a).add(b);
            int[] labels = AdvancedGraphs.stronglyConnectedComponents(directed);
            for (int a = 0; a < n; a++) for (int b = 0; b < n; b++)
                eq(labels[a] == labels[b], reachable(directed, a, b) && reachable(directed, b, a));

            List<UndirectedCuts.Edge> edges = new ArrayList<>();
            for (int a = 0; a < n; a++) for (int b = a + 1; b < n; b++)
                if (random.nextInt(3) == 0) edges.add(new UndirectedCuts.Edge(a, b));
            UndirectedCuts.Result cuts = UndirectedCuts.find(n, edges);
            int base = components(n, edges, -1, -1);
            for (int e = 0; e < edges.size(); e++)
                eq(cuts.bridges().contains(e), components(n, edges, e, -1) > base);
            for (int v = 0; v < n; v++)
                eq(cuts.articulationPoints().contains(v), components(n, edges, -1, v) > base);
        }

        System.out.println("OK: " + checks + " checks");
    }

    private static String randomString(Random random, int length) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; i++) result.append((char) ('a' + random.nextInt(3)));
        return result.toString();
    }

    private static boolean reachable(List<List<Integer>> graph, int start, int goal) {
        return Graphs.bfs(graph, start)[goal] != -1;
    }

    private static int components(int n, List<UndirectedCuts.Edge> edges, int removedEdge, int removedVertex) {
        DataStructures.DSU dsu = new DataStructures.DSU(n);
        for (int i = 0; i < edges.size(); i++) {
            UndirectedCuts.Edge edge = edges.get(i);
            if (i != removedEdge && edge.u() != removedVertex && edge.v() != removedVertex)
                dsu.union(edge.u(), edge.v());
        }
        return dsu.components() - (removedVertex >= 0 ? 1 : 0);
    }
}
