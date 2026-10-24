package notebook;

/** Elementary integer math before modular arithmetic and number theory. */
public final class BeginnerMath {
    private BeginnerMath() {}

    /** Euclid's algorithm for nonnegative inputs. O(log min(a,b)) time. */
    public static long gcd(long a, long b) {
        if (a < 0 || b < 0) throw new IllegalArgumentException("expected nonnegative values");
        while (b != 0) { long next = a % b; a = b; b = next; }
        return a;
    }

    /** Test primality by trial division. O(sqrt(n)) time, O(1) space. */
    public static boolean isPrime(long value) {
        if (value < 2) return false;
        for (long divisor = 2; divisor <= value / divisor; divisor++)
            if (value % divisor == 0) return false;
        return true;
    }

    /** Count positive divisors of a positive integer. O(sqrt(n)) time. */
    public static long divisorCount(long value) {
        if (value <= 0) throw new IllegalArgumentException("expected a positive value");
        long count = 0;
        for (long divisor = 1; divisor <= value / divisor; divisor++)
            if (value % divisor == 0) count += divisor == value / divisor ? 1 : 2;
        return count;
    }

    /** Compute base^exponent for a nonnegative exponent. O(log exponent) time. */
    public static long power(long base, long exponent) {
        if (exponent < 0) throw new IllegalArgumentException("negative exponent");
        long result = 1;
        while (exponent != 0) {
            if ((exponent & 1) != 0) result = Math.multiplyExact(result, base);
            exponent >>= 1;
            if (exponent != 0) base = Math.multiplyExact(base, base);
        }
        return result;
    }
}
