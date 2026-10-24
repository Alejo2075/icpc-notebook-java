package notebook;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/** Additional arithmetic routines. Integer overflow is rejected where practical. */
public final class NumberTheoryAdvanced {
    private NumberTheoryAdvanced() {}

    /** Smallest prime factor for every value in [2,n]. O(n log log n). */
    public static int[] smallestPrimeFactors(int n) {
        if (n < 0) throw new IllegalArgumentException();
        int[] spf = new int[n + 1];
        for (int i = 2; i <= n; i++) if (spf[i] == 0) {
            spf[i] = i;
            if ((long) i * i <= n)
                for (int j = i * i; j <= n; j += i) if (spf[j] == 0) spf[j] = i;
        }
        return spf;
    }

    /** Prime factors with multiplicity, ascending. Trial division; O(sqrt n). */
    public static List<Long> primeFactors(long n) {
        if (n <= 0) throw new IllegalArgumentException();
        List<Long> factors = new ArrayList<>();
        for (long p = 2; p <= n / p; p += p == 2 ? 1 : 2)
            while (n % p == 0) { factors.add(p); n /= p; }
        if (n > 1) factors.add(n);
        return factors;
    }

    /** Euler phi(n), n >= 1. O(sqrt n). */
    public static long phi(long n) {
        if (n <= 0) throw new IllegalArgumentException();
        long result = n, remaining = n;
        for (long p = 2; p <= remaining / p; p++) if (remaining % p == 0) {
            while (remaining % p == 0) remaining /= p;
            result -= result / p;
        }
        if (remaining > 1) result -= result / remaining;
        return result;
    }

    /** Modular inverse for coprime a and mod; throws otherwise. */
    public static long modInverse(long a, long mod) {
        if (mod <= 1) throw new IllegalArgumentException();
        return BigInteger.valueOf(a).mod(BigInteger.valueOf(mod))
            .modInverse(BigInteger.valueOf(mod)).longValueExact();
    }

    /** O(limit) setup, O(1) binomial queries; requires prime mod and limit < mod. */
    public static final class PrimeCombinations {
        private final long mod;
        private final long[] factorial, inverseFactorial;
        public PrimeCombinations(int limit, long primeMod) {
            if (limit < 0 || primeMod <= limit || !NumberTheory.isPrime(primeMod))
                throw new IllegalArgumentException("mod must be prime and greater than limit");
            mod = primeMod;
            factorial = new long[limit + 1];
            inverseFactorial = new long[limit + 1];
            factorial[0] = 1;
            for (int i = 1; i <= limit; i++) factorial[i] = NumberTheory.modMul(factorial[i - 1], i, mod);
            inverseFactorial[limit] = NumberTheory.modPow(factorial[limit], mod - 2, mod);
            for (int i = limit; i > 0; i--) inverseFactorial[i - 1] = NumberTheory.modMul(inverseFactorial[i], i, mod);
        }
        public long choose(int n, int r) {
            if (n < 0 || n >= factorial.length) throw new IndexOutOfBoundsException();
            if (r < 0 || r > n) return 0;
            return NumberTheory.modMul(factorial[n],
                NumberTheory.modMul(inverseFactorial[r], inverseFactorial[n - r], mod), mod);
        }
    }

    /** Square matrix exponentiation modulo mod. O(size^3 log exponent). */
    public static long[][] matrixPower(long[][] matrix, long exponent, long mod) {
        if (exponent < 0 || mod <= 0) throw new IllegalArgumentException();
        int n = matrix.length;
        long[][] result = new long[n][n], base = new long[n][n];
        for (int i = 0; i < n; i++) {
            if (matrix[i].length != n) throw new IllegalArgumentException("square matrix required");
            result[i][i] = 1 % mod;
            for (int j = 0; j < n; j++) base[i][j] = Math.floorMod(matrix[i][j], mod);
        }
        while (exponent > 0) {
            if ((exponent & 1) != 0) result = multiply(result, base, mod);
            exponent >>>= 1;
            if (exponent > 0) base = multiply(base, base, mod);
        }
        return result;
    }

    private static long[][] multiply(long[][] a, long[][] b, long mod) {
        int n = a.length;
        long[][] product = new long[n][n];
        for (int i = 0; i < n; i++) for (int k = 0; k < n; k++)
            for (int j = 0; j < n; j++) {
                long term = NumberTheory.modMul(a[i][k], b[k][j], mod);
                product[i][j] = product[i][j] >= mod - term
                    ? product[i][j] - (mod - term) : product[i][j] + term;
            }
        return product;
    }
}
