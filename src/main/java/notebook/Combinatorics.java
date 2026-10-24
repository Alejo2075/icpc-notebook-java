package notebook;

import java.math.BigInteger;

public final class Combinatorics {
    private Combinatorics() {}

    /** Exact binomial coefficient. O(min(r,n-r)) big-integer operations. */
    public static BigInteger binomial(int n, int r) {
        if (r < 0 || r > n) return BigInteger.ZERO;
        r = Math.min(r, n - r);
        BigInteger ans = BigInteger.ONE;
        for (int i = 1; i <= r; i++)
            ans = ans.multiply(BigInteger.valueOf(n - i + 1)).divide(BigInteger.valueOf(i));
        return ans;
    }

    /** Catalan numbers C[0..n], exact. O(n) big-integer operations. */
    public static BigInteger[] catalan(int n) {
        if (n < 0) throw new IllegalArgumentException();
        BigInteger[] c = new BigInteger[n + 1];
        c[0] = BigInteger.ONE;
        for (int i = 1; i <= n; i++)
            c[i] = c[i - 1].multiply(BigInteger.valueOf(4L * i - 2)).divide(BigInteger.valueOf(i + 1));
        return c;
    }
}
