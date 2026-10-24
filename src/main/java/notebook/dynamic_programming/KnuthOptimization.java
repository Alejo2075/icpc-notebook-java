package notebook;

/** Optimal cost to merge adjacent weighted groups using Knuth's monotone split bounds. */
public final class KnuthOptimization {
    private KnuthOptimization() {}

    /** Nonnegative weights; each merge costs the sum of the merged interval. O(n^2) time and space. */
    public static long optimalAdjacentMergeCost(long[] weights) {
        int n = weights.length;
        for (long weight : weights) if (weight < 0) throw new IllegalArgumentException("weights must be nonnegative");
        if (n <= 1) return 0;
        long[] prefix = new long[n + 1];
        for (int i = 0; i < n; i++) {
            prefix[i + 1] = Math.addExact(prefix[i], weights[i]);
        }
        long[][] dp = new long[n][n];
        int[][] split = new int[n][n];
        for (int i = 0; i < n; i++) split[i][i] = i;
        for (int length = 2; length <= n; length++)
            for (int left = 0; left + length <= n; left++) {
                int right = left + length - 1;
                long best = Long.MAX_VALUE;
                int first = split[left][right - 1], last = Math.min(right - 1, split[left + 1][right]);
                for (int middle = first; middle <= last; middle++) {
                    long cost = Math.addExact(Math.addExact(dp[left][middle], dp[middle + 1][right]),
                            prefix[right + 1] - prefix[left]);
                    if (cost < best) { best = cost; split[left][right] = middle; }
                }
                dp[left][right] = best;
            }
        return dp[0][n - 1];
    }
}
