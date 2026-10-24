package notebook;

import java.math.BigInteger;

/** Exact counting formulas for permutations, partitions, and rotational symmetry. */
public final class CombinatoricsExtras {
    private CombinatoricsExtras() {}

    /** Derangements of n labeled items. O(n) BigInteger operations. */
    public static BigInteger derangements(int n) {
        if (n < 0) throw new IllegalArgumentException();
        if (n == 0) return BigInteger.ONE;
        BigInteger previous = BigInteger.ONE, current = BigInteger.ZERO;
        for (int i = 2; i <= n; i++) {
            BigInteger next = previous.add(current).multiply(BigInteger.valueOf(i - 1));
            previous = current; current = next;
        }
        return current;
    }

    /** Stirling number S(n,k): partitions into exactly k nonempty unlabeled groups. O(n*k). */
    public static BigInteger stirlingSecond(int n, int k) {
        if (n < 0 || k < 0) throw new IllegalArgumentException();
        if (k > n) return BigInteger.ZERO;
        BigInteger[] dp = new BigInteger[k + 1];
        for (int i = 0; i <= k; i++) dp[i] = BigInteger.ZERO;
        dp[0] = BigInteger.ONE;
        for (int value = 1; value <= n; value++)
            for (int groups = Math.min(value, k); groups >= 0; groups--)
                dp[groups] = groups == 0 ? BigInteger.ZERO
                        : dp[groups - 1].add(dp[groups].multiply(BigInteger.valueOf(groups)));
        return dp[k];
    }

    /** Partition numbers p(0)..p(limit) using generalized pentagonal numbers. O(n*sqrt(n)). */
    public static BigInteger[] partitions(int limit) {
        if (limit < 0) throw new IllegalArgumentException();
        BigInteger[] result = new BigInteger[limit + 1];
        result[0] = BigInteger.ONE;
        for (int n = 1; n <= limit; n++) {
            BigInteger sum = BigInteger.ZERO;
            for (long k = 1; k * (3 * k - 1) / 2 <= n; k++) {
                int first = (int) (k * (3 * k - 1) / 2);
                int second = (int) (k * (3 * k + 1) / 2);
                if ((k & 1) == 1) {
                    sum = sum.add(result[n - first]);
                    if (second <= n) sum = sum.add(result[n - second]);
                } else {
                    sum = sum.subtract(result[n - first]);
                    if (second <= n) sum = sum.subtract(result[n - second]);
                }
            }
            result[n] = sum;
        }
        return result;
    }

    /** Number of length-n necklaces over colors colors, up to rotation. Burnside in O(n log n). */
    public static BigInteger necklaces(int n, int colors) {
        if (n <= 0 || colors < 0) throw new IllegalArgumentException();
        BigInteger sum = BigInteger.ZERO, base = BigInteger.valueOf(colors);
        for (int shift = 0; shift < n; shift++) sum = sum.add(base.pow(gcd(n, shift)));
        return sum.divide(BigInteger.valueOf(n));
    }

    private static int gcd(int a, int b) {
        while (b != 0) { int next = a % b; a = b; b = next; }
        return a;
    }
}
