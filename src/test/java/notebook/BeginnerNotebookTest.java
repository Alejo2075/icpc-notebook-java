package notebook;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/** Regression and small exhaustive checks for the introductory curriculum. */
public final class BeginnerNotebookTest {
    private static int checks;

    private static void expect(boolean condition) {
        checks++;
        if (!condition) throw new AssertionError("Beginner check " + checks + " failed");
    }

    private static void examples() {
        expect(BeginnerArrays.sum(new int[] {3, -2, 5}) == 6);
        expect(BeginnerArrays.linearSearch(new int[] {4, 2, 4}, 4) == 0);
        expect(BeginnerArrays.linearSearch(new int[0], 4) == -1);
        expect(BeginnerArrays.count(new int[] {4, 2, 4}, 4) == 2);
        int[] values = {1, 2, 3};
        BeginnerArrays.reverse(values);
        expect(Arrays.equals(values, new int[] {3, 2, 1}));
        expect(BeginnerArrays.rangeSum(BeginnerArrays.prefixSums(values), 1, 3) == 3);
        expect(Arrays.equals(BeginnerArrays.sortedCopy(values), new int[] {1, 2, 3}));
        expect(BeginnerArrays.pairWithSum(new int[] {1, 3, 4}, 7).length == 2);
        expect(BeginnerCollections.frequencies(new int[] {4, 2, 4}).get(4) == 2);
        expect(BeginnerCollections.distinctCount(new int[] {4, 2, 4}) == 2);
        expect(BeginnerCollections.balancedBrackets("([{}])"));
        expect(!BeginnerCollections.balancedBrackets("([)]"));
        expect(BeginnerBacktracking.factorial(5) == 120);
        expect(BeginnerBacktracking.countSubsetsWithSum(new int[] {1, 2, 3}, 3) == 2);
        expect(BeginnerStrings.isPalindrome("racecar"));
        expect(!BeginnerStrings.isPalindrome("contest"));
        expect(BeginnerStrings.lowercaseFrequencies("aab")[0] == 2);
        expect(BeginnerStrings.countOccurrences("aaaa", "aa") == 3);
        expect(BeginnerStrings.commonPrefix("prefix", "prelude").equals("pre"));
        expect(BeginnerMath.gcd(54, 24) == 6);
        expect(!BeginnerMath.isPrime(1) && BeginnerMath.isPrime(97));
        expect(BeginnerMath.divisorCount(36) == 9);
        expect(BeginnerMath.power(-2, 3) == -8);
        expect(BeginnerGreedy.maxNonOverlapping(new long[][] {{0, 3}, {1, 2}, {2, 4}}) == 2);
        expect(BeginnerGreedy.pointsToCover(new long[][] {{1, 3}, {2, 5}, {6, 7}}) == 2);
        expect(BeginnerDynamicProgramming.fibonacci(10) == 55);
        expect(BeginnerDynamicProgramming.fibonacci(92) == 7540113804746346429L);
        expect(BeginnerDynamicProgramming.minimumCoins(new int[] {1, 3, 4}, 6) == 2);
        expect(BeginnerDynamicProgramming.minimumCoins(new int[] {2}, 3) == -1);
        expect(BeginnerDynamicProgramming.gridPaths(new boolean[][] {{false, false}, {false, false}}) == 2);
        expect(BeginnerDynamicProgramming.gridPaths(new boolean[][] {{false, true}, {false, false}}) == 1);
        List<List<Integer>> graph = BeginnerGraphs.undirectedGraph(4, new int[][] {{0, 1}, {1, 2}});
        expect(Arrays.equals(BeginnerGraphs.distances(graph, 0), new int[] {0, 1, 2, -1}));
        expect(BeginnerGraphs.gridComponents(new boolean[][] {
            {true, false, true}, {true, false, false}, {false, true, true}
        }) == 3);
    }

    private static void randomized() {
        Random random = new Random(20261024);
        for (int trial = 0; trial < 500; trial++) {
            int[] values = new int[random.nextInt(14)];
            for (int i = 0; i < values.length; i++) values[i] = random.nextInt(21) - 10;
            long[] prefix = BeginnerArrays.prefixSums(values);
            for (int left = 0; left <= values.length; left++)
                for (int right = left; right <= values.length; right++) {
                    long expected = 0;
                    for (int i = left; i < right; i++) expected += values[i];
                    expect(BeginnerArrays.rangeSum(prefix, left, right) == expected);
                }
            int target = random.nextInt(21) - 10;
            int first = -1, count = 0;
            for (int i = 0; i < values.length; i++) if (values[i] == target) {
                if (first == -1) first = i;
                count++;
            }
            expect(BeginnerArrays.linearSearch(values, target) == first);
            expect(BeginnerArrays.count(values, target) == count);
        }
        for (int value = 0; value <= 500; value++) {
            int divisors = 0;
            for (int divisor = 1; divisor <= value; divisor++) if (value % divisor == 0) divisors++;
            if (value > 0) expect(BeginnerMath.divisorCount(value) == divisors);
            expect(BeginnerMath.isPrime(value) == (divisors == 2));
        }
        for (int trial = 0; trial < 200; trial++) {
            int[] values = new int[random.nextInt(10)];
            for (int i = 0; i < values.length; i++) values[i] = random.nextInt(9) - 4;
            long target = random.nextInt(21) - 10;
            long expected = 0;
            for (int mask = 0; mask < (1 << values.length); mask++) {
                long sum = 0;
                for (int i = 0; i < values.length; i++) if ((mask & (1 << i)) != 0) sum += values[i];
                if (sum == target) expected++;
            }
            expect(BeginnerBacktracking.countSubsetsWithSum(values, target) == expected);
        }
    }

    public static void main(String[] args) {
        examples();
        randomized();
        System.out.println("OK: " + checks + " beginner checks");
    }
}
