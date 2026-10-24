package notebook;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/** Discrete logarithm, modular square root, and the Mobius function. */
public final class ModularExtras {
    private ModularExtras() {}

    /** Smallest x >= 0 with base^x = target (mod modulus), or -1. Requires coprime base and modulus. */
    public static long discreteLog(long base, long target, long modulus) {
        if (modulus <= 1 || modulus > 2_000_000_000L) throw new IllegalArgumentException("modulus out of range");
        base = Math.floorMod(base, modulus); target = Math.floorMod(target, modulus);
        if (BigInteger.valueOf(base).gcd(BigInteger.valueOf(modulus)).longValue() != 1)
            throw new IllegalArgumentException("base and modulus must be coprime");
        long step = (long) Math.ceil(Math.sqrt(modulus));
        Map<Long, Long> baby = new HashMap<>();
        long value = 1;
        for (long j = 0; j < step; j++) {
            baby.putIfAbsent(value, j);
            value = value * base % modulus;
        }
        long factor = BigInteger.valueOf(value).modInverse(BigInteger.valueOf(modulus)).longValue();
        long giant = target;
        for (long i = 0; i <= step; i++) {
            Long j = baby.get(giant);
            if (j != null) return i * step + j;
            giant = giant * factor % modulus;
        }
        return -1;
    }

    /** One square root of value modulo an odd prime, or -1 if none exists. Tonelli-Shanks. */
    public static long modularSquareRoot(long value, long prime) {
        if (prime == 2) return Math.floorMod(value, 2);
        if (prime <= 2 || (prime & 1) == 0 || prime > 2_000_000_000L)
            throw new IllegalArgumentException("expected an odd prime at most 2e9");
        value = Math.floorMod(value, prime);
        if (value == 0) return 0;
        if (power(value, (prime - 1) / 2, prime) != 1) return -1;
        if (prime % 4 == 3) return power(value, (prime + 1) / 4, prime);
        long odd = prime - 1;
        int twos = 0;
        while ((odd & 1) == 0) { odd >>= 1; twos++; }
        long nonResidue = 2;
        while (power(nonResidue, (prime - 1) / 2, prime) != prime - 1) nonResidue++;
        long c = power(nonResidue, odd, prime), x = power(value, (odd + 1) / 2, prime);
        long t = power(value, odd, prime);
        int m = twos;
        while (t != 1) {
            int i = 1;
            long squared = t * t % prime;
            while (i < m && squared != 1) { squared = squared * squared % prime; i++; }
            if (i == m) throw new IllegalStateException("modulus may not be prime");
            long b = power(c, 1L << (m - i - 1), prime);
            x = x * b % prime;
            t = t * b % prime * b % prime;
            c = b * b % prime;
            m = i;
        }
        return x;
    }

    private static long power(long base, long exponent, long modulus) {
        long result = 1;
        while (exponent != 0) {
            if ((exponent & 1) != 0) result = result * base % modulus;
            base = base * base % modulus;
            exponent >>= 1;
        }
        return result;
    }

    /** Mobius values mu[0..limit] via a linear sieve. O(limit) time and space. */
    public static int[] mobius(int limit) {
        if (limit < 0) throw new IllegalArgumentException();
        int[] mu = new int[limit + 1];
        if (limit == 0) return mu;
        mu[1] = 1;
        int[] primes = new int[limit + 1];
        boolean[] composite = new boolean[limit + 1];
        int count = 0;
        for (int value = 2; value <= limit; value++) {
            if (!composite[value]) { primes[count++] = value; mu[value] = -1; }
            for (int i = 0; i < count && (long) value * primes[i] <= limit; i++) {
                int next = value * primes[i]; composite[next] = true;
                if (value % primes[i] == 0) { mu[next] = 0; break; }
                mu[next] = -mu[value];
            }
        }
        return mu;
    }
}
