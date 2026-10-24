package notebook;

/** Zeta, Mobius, and Walsh-Hadamard transforms on subset masks. */
public final class SubsetTransforms {
    private SubsetTransforms() {}

    private static int bits(long[] values) {
        int length = values.length;
        if (length == 0 || (length & (length - 1)) != 0)
            throw new IllegalArgumentException("length must be a power of two");
        return Integer.numberOfTrailingZeros(length);
    }

    /** Replace f[mask] by the sum of f[submask] over all submasks. O(k*2^k). */
    public static void subsetZeta(long[] values) {
        int k = bits(values);
        for (int bit = 0; bit < k; bit++)
            for (int mask = 0; mask < values.length; mask++) if ((mask & (1 << bit)) != 0)
                values[mask] = Math.addExact(values[mask], values[mask ^ (1 << bit)]);
    }

    /** Undo subsetZeta. O(k*2^k). */
    public static void subsetMobius(long[] values) {
        int k = bits(values);
        for (int bit = 0; bit < k; bit++)
            for (int mask = 0; mask < values.length; mask++) if ((mask & (1 << bit)) != 0)
                values[mask] = Math.subtractExact(values[mask], values[mask ^ (1 << bit)]);
    }

    /** XOR Walsh-Hadamard transform; inverse divides each result by the array length. O(k*2^k). */
    public static void xorHadamard(long[] values, boolean inverse) {
        bits(values);
        for (int width = 1; width < values.length; width <<= 1)
            for (int start = 0; start < values.length; start += width * 2)
                for (int i = 0; i < width; i++) {
                    long a = values[start + i], b = values[start + width + i];
                    values[start + i] = Math.addExact(a, b);
                    values[start + width + i] = Math.subtractExact(a, b);
                }
        if (inverse) for (int i = 0; i < values.length; i++) {
            if (values[i] % values.length != 0) throw new ArithmeticException("nonintegral inverse");
            values[i] /= values.length;
        }
    }
}
