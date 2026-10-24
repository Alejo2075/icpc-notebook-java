package notebook;

import java.util.Arrays;
import java.util.Comparator;

/** DP patterns beyond the standard one-dimensional and sequence examples. */
public final class AdvancedDynamicProgramming {
    private AdvancedDynamicProgramming() {}

    /** Minimum Hamiltonian cycle starting and ending at vertex 0. O(n^2 2^n); n<=20. */
    public static long travelingSalesperson(long[][] distance) {
        int n = distance.length;
        if (n == 0) return 0;
        if (n > 20) throw new IllegalArgumentException("n must be at most 20");
        for (long[] row : distance) if (row.length != n) throw new IllegalArgumentException("square matrix required");
        long inf = Long.MAX_VALUE / 4;
        long[][] dp = new long[1 << n][n];
        for (long[] row : dp) Arrays.fill(row, inf);
        dp[1][0] = 0;
        for (int mask = 1; mask < (1 << n); mask += 2)
            for (int last = 0; last < n; last++) {
                if (dp[mask][last] == inf) continue;
                for (int next = 0; next < n; next++) if ((mask & (1 << next)) == 0)
                    dp[mask | (1 << next)][next] = Math.min(
                        dp[mask | (1 << next)][next], dp[mask][last] + distance[last][next]);
            }
        long answer = inf;
        for (int last = 0; last < n; last++)
            answer = Math.min(answer, dp[(1 << n) - 1][last] + distance[last][0]);
        return answer;
    }

    /** Matrix-chain multiplication cost; dimensions[i-1] x dimensions[i]. O(n^3). */
    public static long matrixChainCost(int[] dimensions) {
        int n = dimensions.length - 1;
        if (n <= 0) return 0;
        long[][] dp = new long[n][n];
        for (int length = 2; length <= n; length++)
            for (int left = 0; left + length <= n; left++) {
                int right = left + length - 1;
                dp[left][right] = Long.MAX_VALUE;
                for (int split = left; split < right; split++) {
                    long product = (long) dimensions[left] * dimensions[split + 1] * dimensions[right + 1];
                    dp[left][right] = Math.min(dp[left][right], dp[left][split] + dp[split + 1][right] + product);
                }
            }
        return dp[0][n - 1];
    }

    /** Maximum profit from non-overlapping [start,end) jobs. O(n log n). */
    public static long weightedIntervals(long[][] jobs) {
        long[][] sorted = jobs.clone();
        for (int i = 0; i < sorted.length; i++) {
            if (sorted[i].length != 3 || sorted[i][0] > sorted[i][1]) throw new IllegalArgumentException();
            sorted[i] = sorted[i].clone();
        }
        Arrays.sort(sorted, Comparator.comparingLong(a -> a[1]));
        long[] ends = new long[sorted.length], dp = new long[sorted.length + 1];
        for (int i = 0; i < sorted.length; i++) ends[i] = sorted[i][1];
        for (int i = 0; i < sorted.length; i++) {
            int lo = 0, hi = i;
            while (lo < hi) {
                int mid = (lo + hi) >>> 1;
                if (ends[mid] <= sorted[i][0]) lo = mid + 1;
                else hi = mid;
            }
            dp[i + 1] = Math.max(dp[i], dp[lo] + sorted[i][2]);
        }
        return dp[sorted.length];
    }

    /** Count integers in [0, bound] without equal adjacent decimal digits. O(digits*10). */
    public static long countNoEqualAdjacent(long bound) {
        if (bound < 0) return 0;
        char[] digits = Long.toString(bound).toCharArray();
        long[][][] dp = new long[2][11][2];
        dp[1][10][0] = 1;
        for (char digit : digits) {
            long[][][] next = new long[2][11][2];
            for (int tight = 0; tight <= 1; tight++)
                for (int previous = 0; previous <= 10; previous++)
                    for (int started = 0; started <= 1; started++) {
                        long ways = dp[tight][previous][started];
                        for (int value = 0; value <= (tight == 1 ? digit - '0' : 9); value++) {
                            int nextTight = tight == 1 && value == digit - '0' ? 1 : 0;
                            int nextStarted = started == 1 || value != 0 ? 1 : 0;
                            if (nextStarted == 1 && started == 1 && previous == value) continue;
                            int nextPrevious = nextStarted == 1 ? value : 10;
                            next[nextTight][nextPrevious][nextStarted] += ways;
                        }
                    }
            dp = next;
        }
        long result = 0;
        for (long[][] state : dp) for (long[] prev : state) for (long ways : prev) result += ways;
        return result;
    }
}
