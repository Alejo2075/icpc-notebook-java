package notebook;

/** Small contest DP routines. Counts are long where practical and may overflow. */
public final class DynamicProgramming {
    private DynamicProgramming() {}

    /** Number of unordered combinations; positive coin values required. O(coins * target). */
    public static long coinChange(int[] coins, int target) {
        if (target < 0) return 0;
        long[] dp = new long[target + 1];
        dp[0] = 1;
        for (int coin : coins) {
            if (coin <= 0) throw new IllegalArgumentException("coin must be positive");
            for (int s = coin; s <= target; s++) dp[s] += dp[s - coin];
        }
        return dp[target];
    }

    /** Number of ordered ways to climb using steps of 1, 2, or 3. O(n). */
    public static long countWays(int n) {
        if (n < 0) return 0;
        long[] dp = new long[n + 1];
        dp[0] = 1;
        for (int i = 1; i <= n; i++)
            for (int step = 1; step <= 3; step++)
                if (i >= step) dp[i] += dp[i - step];
        return dp[n];
    }

    /** Levenshtein distance. O(a.length * b.length) time, O(b.length) space. */
    public static int editDistance(String a, String b) {
        int[] prev = new int[b.length() + 1];
        for (int j = 0; j <= b.length(); j++) prev[j] = j;
        for (int i = 1; i <= a.length(); i++) {
            int[] cur = new int[b.length() + 1];
            cur[0] = i;
            for (int j = 1; j <= b.length(); j++)
                cur[j] = a.charAt(i - 1) == b.charAt(j - 1) ? prev[j - 1]
                    : 1 + Math.min(prev[j - 1], Math.min(prev[j], cur[j - 1]));
            prev = cur;
        }
        return prev[b.length()];
    }

    /** 0/1 knapsack with nonnegative weights and capacity. O(items * capacity). */
    public static long knapsack(int capacity, int[] weights, int[] values) {
        if (capacity < 0 || weights.length != values.length) throw new IllegalArgumentException();
        long[] dp = new long[capacity + 1];
        for (int i = 0; i < weights.length; i++) {
            if (weights[i] < 0) throw new IllegalArgumentException("negative weight");
            for (int w = capacity; w >= weights[i]; w--)
                dp[w] = Math.max(dp[w], dp[w - weights[i]] + values[i]);
        }
        return dp[capacity];
    }

    /** Longest common subsequence length. O(mn) time, O(n) space. */
    public static int lcs(String a, String b) {
        int[] dp = new int[b.length() + 1];
        for (int i = 1; i <= a.length(); i++) {
            int diag = 0;
            for (int j = 1; j <= b.length(); j++) {
                int old = dp[j];
                dp[j] = a.charAt(i - 1) == b.charAt(j - 1) ? diag + 1
                    : Math.max(dp[j], dp[j - 1]);
                diag = old;
            }
        }
        return dp[b.length()];
    }

    /** Strictly increasing subsequence length. O(n log n). */
    public static int lis(int[] a) {
        int[] tails = new int[a.length];
        int size = 0;
        for (int x : a) {
            int lo = 0, hi = size;
            while (lo < hi) {
                int mid = (lo + hi) >>> 1;
                if (tails[mid] < x) lo = mid + 1;
                else hi = mid;
            }
            tails[lo] = x;
            if (lo == size) size++;
        }
        return size;
    }

    /** Maximum product after at least one cut; n >= 2. O(n). */
    public static long maxProductCutting(int n) {
        if (n < 2) throw new IllegalArgumentException();
        if (n <= 3) return n - 1;
        long result = 1;
        while (n > 4) { result = Math.multiplyExact(result, 3); n -= 3; }
        return Math.multiplyExact(result, n);
    }

    /** Minimum difference between subset sums; nonnegative values. O(n * sum) time. */
    public static int minimumPartition(int[] a) {
        int sum = 0;
        for (int x : a) {
            if (x < 0) throw new IllegalArgumentException("negative value");
            sum = Math.addExact(sum, x);
        }
        boolean[] dp = new boolean[sum / 2 + 1];
        dp[0] = true;
        for (int x : a)
            for (int s = dp.length - 1; s >= x; s--) dp[s] |= dp[s - x];
        for (int s = dp.length - 1; s >= 0; s--) if (dp[s]) return sum - 2 * s;
        throw new AssertionError();
    }

    /** Subset sum for nonnegative values and target. O(n * target). */
    public static boolean subsetSum(int[] a, int target) {
        if (target < 0) return false;
        boolean[] dp = new boolean[target + 1];
        dp[0] = true;
        for (int x : a) {
            if (x < 0) throw new IllegalArgumentException("negative value");
            for (int s = target; s >= x; s--) dp[s] |= dp[s - x];
        }
        return dp[target];
    }
}
