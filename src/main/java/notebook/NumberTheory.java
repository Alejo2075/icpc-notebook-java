package notebook;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

/** Integer number theory. Modular operations require mod > 0. */
public final class NumberTheory {
    private NumberTheory() {}

    public static long gcd(long a, long b) {
        if (a == Long.MIN_VALUE || b == Long.MIN_VALUE)
            return BigInteger.valueOf(a).gcd(BigInteger.valueOf(b)).longValueExact();
        a = Math.abs(a); b = Math.abs(b);
        while (b != 0) { long t = a % b; a = b; b = t; }
        return a;
    }

    public static long lcm(long a, long b) {
        if (a == 0 || b == 0) return 0;
        long product = Math.multiplyExact(a / gcd(a, b), b);
        if (product == Long.MIN_VALUE) throw new ArithmeticException("lcm does not fit in long");
        return Math.abs(product);
    }

    /** Returns [gcd(a,b), x, y] with ax+by=gcd; coefficients must fit in long. */
    public static long[] extendedGcd(long a, long b) {
        BigInteger aa = BigInteger.valueOf(a), bb = BigInteger.valueOf(b);
        BigInteger oldR = aa, r = bb, oldS = BigInteger.ONE, s = BigInteger.ZERO;
        BigInteger oldT = BigInteger.ZERO, t = BigInteger.ONE;
        while (r.signum() != 0) {
            BigInteger q = oldR.divide(r);
            BigInteger nr = oldR.subtract(q.multiply(r)); oldR = r; r = nr;
            BigInteger ns = oldS.subtract(q.multiply(s)); oldS = s; s = ns;
            BigInteger nt = oldT.subtract(q.multiply(t)); oldT = t; t = nt;
        }
        if (oldR.signum() < 0) { oldR = oldR.negate(); oldS = oldS.negate(); oldT = oldT.negate(); }
        return new long[] { oldR.longValueExact(), oldS.longValueExact(), oldT.longValueExact() };
    }

    /** Exact multiplication modulo mod without signed-long overflow. O(log b). */
    public static long modMul(long a, long b, long mod) {
        if (mod <= 0) throw new IllegalArgumentException("mod must be positive");
        long x = Math.floorMod(a, mod), y = Math.floorMod(b, mod), result = 0;
        while (y > 0) {
            if ((y & 1) != 0) result = addMod(result, x, mod);
            y >>>= 1;
            if (y > 0) x = addMod(x, x, mod);
        }
        return result;
    }

    private static long addMod(long a, long b, long mod) {
        return a >= mod - b ? a - (mod - b) : a + b;
    }

    /** a^exponent modulo mod. O(log exponent * log mod). */
    public static long modPow(long a, long exponent, long mod) {
        if (exponent < 0 || mod <= 0) throw new IllegalArgumentException();
        long result = 1 % mod, base = Math.floorMod(a, mod);
        while (exponent > 0) {
            if ((exponent & 1) != 0) result = modMul(result, base, mod);
            exponent >>>= 1;
            if (exponent > 0) base = modMul(base, base, mod);
        }
        return result;
    }

    /** Deterministic Miller-Rabin for signed 64-bit positive integers. */
    public static boolean isPrime(long n) {
        if (n < 2) return false;
        for (long p : new long[] {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37}) {
            if (n % p == 0) return n == p;
        }
        long d = n - 1;
        int s = Long.numberOfTrailingZeros(d);
        d >>= s;
        for (long witness : new long[] {2, 325, 9375, 28178, 450775, 9780504, 1795265022}) {
            long a = witness % n;
            if (a == 0) continue;
            long x = modPow(a, d, n);
            if (x == 1 || x == n - 1) continue;
            boolean passed = false;
            for (int r = 1; r < s; r++) {
                x = modMul(x, x, n);
                if (x == n - 1) { passed = true; break; }
            }
            if (!passed) return false;
        }
        return true;
    }

    /** Primes <= n; O(n log log n) time, O(n) space. */
    public static List<Integer> sieve(int n) {
        if (n < 2) return new ArrayList<>();
        boolean[] composite = new boolean[n + 1];
        for (int p = 2; (long) p * p <= n; p++)
            if (!composite[p]) for (int x = p * p; x <= n; x += p) composite[x] = true;
        List<Integer> primes = new ArrayList<>();
        for (int i = 2; i <= n; i++) if (!composite[i]) primes.add(i);
        return primes;
    }

    /** Sorted positive divisors of n; n > 0. O(sqrt n). */
    public static List<Long> divisors(long n) {
        if (n <= 0) throw new IllegalArgumentException();
        List<Long> result = new ArrayList<>();
        for (long d = 1; d <= n / d; d++) {
            if (n % d == 0) {
                result.add(d);
                if (d != n / d) result.add(n / d);
            }
        }
        Collections.sort(result);
        return result;
    }

    /** Exact factorial; n >= 0. */
    public static BigInteger factorial(int n) {
        if (n < 0) throw new IllegalArgumentException();
        BigInteger result = BigInteger.ONE;
        for (int i = 2; i <= n; i++) result = result.multiply(BigInteger.valueOf(i));
        return result;
    }

    /** Generalized CRT. Returns [least nonnegative solution, lcm], or null if inconsistent. */
    public static BigInteger[] chineseRemainder(long[] moduli, long[] residues) {
        if (moduli.length != residues.length) throw new IllegalArgumentException();
        BigInteger x = BigInteger.ZERO, m = BigInteger.ONE;
        for (int i = 0; i < moduli.length; i++) {
            if (moduli[i] <= 0) throw new IllegalArgumentException("moduli must be positive");
            BigInteger next = BigInteger.valueOf(moduli[i]);
            BigInteger target = BigInteger.valueOf(residues[i]).mod(next);
            BigInteger g = m.gcd(next), delta = target.subtract(x);
            if (!delta.mod(g).equals(BigInteger.ZERO)) return null;
            BigInteger reduced = next.divide(g);
            BigInteger k = reduced.equals(BigInteger.ONE) ? BigInteger.ZERO
                : delta.divide(g).multiply(m.divide(g).modInverse(reduced)).mod(reduced);
            x = x.add(m.multiply(k));
            m = m.multiply(reduced);
            x = x.mod(m);
        }
        return new BigInteger[] { x, m };
    }

    /** Newton iteration; derivative must remain nonzero near the root. */
    public static double newtonRaphson(DoubleUnaryOperator f, DoubleUnaryOperator derivative,
                                        double start, double tolerance, int maxIterations) {
        if (!(tolerance > 0) || maxIterations <= 0) throw new IllegalArgumentException();
        double x = start;
        for (int i = 0; i < maxIterations; i++) {
            double fx = f.applyAsDouble(x), dx = derivative.applyAsDouble(x);
            if (Math.abs(fx) <= tolerance) return x;
            if (!Double.isFinite(dx) || dx == 0) throw new ArithmeticException("zero derivative");
            x -= fx / dx;
            if (!Double.isFinite(x)) throw new ArithmeticException("non-finite iterate");
        }
        throw new ArithmeticException("did not converge");
    }
}
