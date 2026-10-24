package notebook;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/** Regression and small-instance differential tests for the extended reference. */
public final class ExtendedNotebookTest {
    private static int checks;
    private static void check(boolean value) { checks++; if (!value) throw new AssertionError("check failed"); }
    private static void eq(long actual, long expected) {
        checks++; if (actual != expected) throw new AssertionError("expected " + expected + ", got " + actual);
    }
    private static void eq(Object actual, Object expected) {
        checks++; if (!java.util.Objects.equals(actual, expected))
            throw new AssertionError("expected " + expected + ", got " + actual);
    }

    public static void main(String[] args) {
        Random random = new Random(20261023);
        eq(Arrays.toString(Fundamentals.prefixSums(new int[] {3, -2, 5})), "[0, 3, 1, 6]");
        eq(Arrays.toString(Fundamentals.rangeAdd(4, new long[][] {{0, 3, 2}, {2, 4, -1}})), "[2, 2, 1, -1]");
        eq(Arrays.toString(Fundamentals.sortedUnique(new int[] {5, 1, 5, 3})), "[1, 3, 5]");
        eq(Fundamentals.countPairsAtMost(new int[] {1, 2, 3, 4}, 5), 4);
        eq(Fundamentals.longestWindowAtMost(new int[] {2, 1, 3, 1}, 4), 2);
        eq(Fundamentals.maxSubarraySum(new int[] {-5, 2, 3, -1}), 5);
        eq(Fundamentals.inversions(new int[] {3, 1, 2}), 2);
        int[] permutation = {1, 3, 2};
        check(Fundamentals.nextPermutation(permutation));
        eq(Arrays.toString(permutation), "[2, 1, 3]");
        eq(Fundamentals.nonemptySubmasks(5).length, 3);
        eq(SearchingSorting.upperBound(new int[] {1, 2, 2, 3}, 2), 3);
        eq(SearchingSorting.firstTrue(0, 100, x -> x * x >= 200), 15);
        for (int t = 0; t < 300; t++) {
            int[] a = random.ints(random.nextInt(9), -5, 6).toArray();
            long inversions = 0, best = Long.MIN_VALUE;
            for (int i = 0; i < a.length; i++) {
                long sum = 0;
                for (int j = i; j < a.length; j++) {
                    sum += a[j]; best = Math.max(best, sum);
                }
                for (int j = i + 1; j < a.length; j++) if (a[i] > a[j]) inversions++;
            }
            eq(Fundamentals.inversions(a), inversions);
            eq(Fundamentals.maxSubarraySum(a), a.length == 0 ? 0 : best);
        }

        RangeQueries.Prefix2D prefix = new RangeQueries.Prefix2D(new int[][] {{1, 2, 3}, {4, 5, 6}});
        eq(prefix.sum(0, 1, 2, 3), 16);
        RangeQueries.SparseTableMin sparse = new RangeQueries.SparseTableMin(new int[] {5, 2, 4, 1});
        eq(sparse.min(1, 3), 2);
        RangeQueries.LazySegmentTree lazy = new RangeQueries.LazySegmentTree(new long[] {1, 2, 3, 4});
        lazy.add(1, 4, 5); eq(lazy.sum(0, 4), 25); eq(lazy.sum(2, 4), 17);
        for (int t = 0; t < 100; t++) {
            int n = 1 + random.nextInt(15);
            long[] brute = new long[n];
            RangeQueries.LazySegmentTree tree = new RangeQueries.LazySegmentTree(brute);
            for (int q = 0; q < 100; q++) {
                int l = random.nextInt(n), r = l + 1 + random.nextInt(n - l);
                if (random.nextBoolean()) {
                    int delta = random.nextInt(21) - 10;
                    tree.add(l, r, delta);
                    for (int i = l; i < r; i++) brute[i] += delta;
                } else {
                    long sum = 0;
                    for (int i = l; i < r; i++) sum += brute[i];
                    eq(tree.sum(l, r), sum);
                }
            }
        }

        List<List<Integer>> graph = Graphs.newGraph(4);
        undirected(graph, 0, 1); undirected(graph, 1, 2);
        eq(Arrays.toString(GraphEssentials.components(graph)), "[0, 0, 0, 1]");
        check(GraphEssentials.bipartiteColors(graph) != null);
        undirected(graph, 0, 2);
        check(GraphEssentials.bipartiteColors(graph) == null);
        List<List<GraphEssentials.BinaryEdge>> binary = new ArrayList<>();
        for (int i = 0; i < 3; i++) binary.add(new ArrayList<>());
        binary.get(0).add(new GraphEssentials.BinaryEdge(1, 1));
        binary.get(0).add(new GraphEssentials.BinaryEdge(2, 0));
        binary.get(2).add(new GraphEssentials.BinaryEdge(1, 0));
        eq(Arrays.toString(GraphEssentials.zeroOneBfs(binary, 0)), "[0, 0, 0]");
        long inf = Long.MAX_VALUE;
        long[][] paths = GraphEssentials.floydWarshall(new long[][] {{0, 3, inf}, {inf, 0, 4}, {inf, inf, 0}});
        eq(paths[0][2], 7);
        List<Integer> trail = GraphEssentials.directedEulerTrail(3, new int[][] {{0, 1}, {1, 2}, {2, 0}});
        eq(trail.size(), 4);
        check(GraphEssentials.directedEulerTrail(3, new int[][] {{0, 1}, {0, 2}}).isEmpty());
        TwoSat sat = new TwoSat(2);
        sat.addOr(0, true, 1, true);
        sat.addOr(0, false, 1, true);
        boolean[] assignment = sat.solve();
        check(assignment != null && assignment[1]);
        TwoSat impossible = new TwoSat(1);
        impossible.addOr(0, true, 0, true);
        impossible.addOr(0, false, 0, false);
        check(impossible.solve() == null);

        List<List<Integer>> tree = Graphs.newGraph(5);
        undirected(tree, 0, 1); undirected(tree, 0, 2);
        undirected(tree, 1, 3); undirected(tree, 1, 4);
        eq(TreeAlgorithms.diameter(tree), 3);
        TreeAlgorithms.EulerTour tour = new TreeAlgorithms.EulerTour(tree, 0);
        eq(tour.tout[1] - tour.tin[1], 3);
        TreeAlgorithms.HeavyLight heavy = new TreeAlgorithms.HeavyLight(tree, new long[] {1, 2, 3, 4, 5}, 0);
        eq(heavy.pathSum(3, 2), 10);
        heavy.set(1, 10);
        eq(heavy.pathSum(3, 4), 19);

        eq(AdvancedDynamicProgramming.travelingSalesperson(new long[][] {{0, 2, 9}, {2, 0, 3}, {9, 3, 0}}), 14);
        eq(AdvancedDynamicProgramming.matrixChainCost(new int[] {10, 30, 5, 60}), 4500);
        eq(AdvancedDynamicProgramming.weightedIntervals(new long[][] {{1, 3, 5}, {2, 5, 6}, {3, 6, 5}}), 10);
        for (int bound = 0; bound < 1000; bound++) {
            int count = 0;
            for (int value = 0; value <= bound; value++) {
                String digits = Integer.toString(value);
                boolean good = true;
                for (int i = 1; i < digits.length(); i++) if (digits.charAt(i) == digits.charAt(i - 1)) good = false;
                if (good) count++;
            }
            eq(AdvancedDynamicProgramming.countNoEqualAdjacent(bound), count);
        }

        eq(Arrays.toString(NumberTheoryAdvanced.smallestPrimeFactors(10)), "[0, 0, 2, 3, 2, 5, 2, 7, 2, 3, 2]");
        eq(NumberTheoryAdvanced.primeFactors(84), List.of(2L, 2L, 3L, 7L));
        eq(NumberTheoryAdvanced.phi(36), 12);
        eq(NumberTheoryAdvanced.modInverse(3, 11), 4);
        eq(new NumberTheoryAdvanced.PrimeCombinations(10, 1_000_000_007L).choose(5, 2), 10);
        long[][] fib = NumberTheoryAdvanced.matrixPower(new long[][] {{1, 1}, {1, 0}}, 10, 1_000_000_007);
        eq(fib[0][1], 55);
        for (int t = 0; t < 250; t++) {
            int[] a = random.ints(random.nextInt(12), -20, 21).toArray();
            int[] b = random.ints(random.nextInt(12), -20, 21).toArray();
            int[] convolution = Convolution.multiply(a, b);
            eq(convolution.length, a.length == 0 || b.length == 0 ? 0 : a.length + b.length - 1);
            for (int i = 0; i < convolution.length; i++) {
                long value = 0;
                for (int j = 0; j < a.length; j++) if (i - j >= 0 && i - j < b.length)
                    value += (long) a[j] * b[i - j];
                eq(convolution[i], Math.floorMod(value, Convolution.MOD));
            }
        }

        eq(Arrays.toString(SuffixAlgorithms.suffixArray("banana")), "[5, 3, 1, 0, 4, 2]");
        eq(Arrays.toString(SuffixAlgorithms.lcp("banana", SuffixAlgorithms.suffixArray("banana"))), "[0, 1, 3, 0, 0, 2]");
        eq(SuffixAlgorithms.distinctSubstrings("aaa"), 3);
        for (int t = 0; t < 150; t++) {
            String text = randomString(random, random.nextInt(10));
            int[] sa = SuffixAlgorithms.suffixArray(text);
            for (int i = 1; i < sa.length; i++)
                check(text.substring(sa[i - 1]).compareTo(text.substring(sa[i])) < 0);
            java.util.Set<String> unique = new java.util.HashSet<>();
            for (int i = 0; i < text.length(); i++)
                for (int j = i + 1; j <= text.length(); j++) unique.add(text.substring(i, j));
            eq(SuffixAlgorithms.distinctSubstrings(text), unique.size());
        }
        AhoCorasick aho = new AhoCorasick();
        aho.add("he"); aho.add("she"); aho.add("hers");
        eq(aho.countMatches("ushers"), 3);

        GeometryAdvanced.Point a = new GeometryAdvanced.Point(0, 0), b = new GeometryAdvanced.Point(4, 0);
        GeometryAdvanced.Point c = new GeometryAdvanced.Point(2, -1), d = new GeometryAdvanced.Point(2, 1);
        check(GeometryAdvanced.segmentsIntersect(a, b, c, d));
        List<GeometryAdvanced.Point> square = List.of(a, b, new GeometryAdvanced.Point(4, 4), new GeometryAdvanced.Point(0, 4));
        check(GeometryAdvanced.pointInPolygon(square, new GeometryAdvanced.Point(2, 2)));
        check(GeometryAdvanced.pointInPolygon(square, b));
        check(!GeometryAdvanced.pointInPolygon(square, new GeometryAdvanced.Point(5, 2)));
        eq(GeometryAdvanced.signedDoubleArea(square), BigInteger.valueOf(32));
        check(GeometryAdvanced.cross(new GeometryAdvanced.Point(Long.MIN_VALUE, 0),
            new GeometryAdvanced.Point(Long.MAX_VALUE, 0), new GeometryAdvanced.Point(0, 1)).signum() > 0);

        MinCostFlow flow = new MinCostFlow(4);
        flow.addEdge(0, 1, 2, 1); flow.addEdge(0, 2, 1, 3);
        flow.addEdge(1, 3, 2, 2); flow.addEdge(2, 3, 1, 1);
        eq(flow.send(0, 3, 3), new MinCostFlow.Result(3, 10));
        System.out.println("OK: " + checks + " extended checks");
    }

    private static void undirected(List<List<Integer>> graph, int a, int b) {
        graph.get(a).add(b); graph.get(b).add(a);
    }
    private static String randomString(Random random, int length) {
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) text.append((char) ('a' + random.nextInt(3)));
        return text.toString();
    }
}
