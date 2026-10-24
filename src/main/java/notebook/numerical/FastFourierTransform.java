package notebook;

/** Floating-point FFT convolution for integer coefficient sequences. */
public final class FastFourierTransform {
    private FastFourierTransform() {}

    /** Rounded convolution. Use NTT for exact modular arithmetic; floating error grows with input size. */
    public static long[] convolution(long[] first, long[] second) {
        if (first.length == 0 || second.length == 0) return new long[0];
        int resultLength = Math.addExact(first.length, second.length) - 1;
        int size = 1;
        while (size < resultLength) {
            if (size > (1 << 29)) throw new IllegalArgumentException("transform too large");
            size <<= 1;
        }
        double[] realA = new double[size], imagA = new double[size];
        double[] realB = new double[size], imagB = new double[size];
        for (int i = 0; i < first.length; i++) realA[i] = first[i];
        for (int i = 0; i < second.length; i++) realB[i] = second[i];
        transform(realA, imagA, false);
        transform(realB, imagB, false);
        for (int i = 0; i < size; i++) {
            double real = realA[i] * realB[i] - imagA[i] * imagB[i];
            double imag = realA[i] * imagB[i] + imagA[i] * realB[i];
            realA[i] = real; imagA[i] = imag;
        }
        transform(realA, imagA, true);
        long[] result = new long[resultLength];
        for (int i = 0; i < resultLength; i++) {
            double value = Math.rint(realA[i]);
            if (!Double.isFinite(value) || value < -0x1.0p63 || value >= 0x1.0p63)
                throw new ArithmeticException("coefficient outside long range");
            result[i] = (long) value;
        }
        return result;
    }

    private static void transform(double[] real, double[] imag, boolean inverse) {
        int n = real.length;
        for (int i = 1, j = 0; i < n; i++) {
            int bit = n >>> 1;
            for (; (j & bit) != 0; bit >>>= 1) j ^= bit;
            j ^= bit;
            if (i < j) {
                double temp = real[i]; real[i] = real[j]; real[j] = temp;
                temp = imag[i]; imag[i] = imag[j]; imag[j] = temp;
            }
        }
        for (int length = 2; length <= n; length <<= 1) {
            double angle = 2 * Math.PI / length * (inverse ? -1 : 1);
            double rootReal = Math.cos(angle), rootImag = Math.sin(angle);
            for (int start = 0; start < n; start += length) {
                double factorReal = 1, factorImag = 0;
                for (int offset = 0; offset < length / 2; offset++) {
                    int even = start + offset, odd = even + length / 2;
                    double oddReal = real[odd] * factorReal - imag[odd] * factorImag;
                    double oddImag = real[odd] * factorImag + imag[odd] * factorReal;
                    real[odd] = real[even] - oddReal; imag[odd] = imag[even] - oddImag;
                    real[even] += oddReal; imag[even] += oddImag;
                    double nextReal = factorReal * rootReal - factorImag * rootImag;
                    factorImag = factorReal * rootImag + factorImag * rootReal;
                    factorReal = nextReal;
                }
            }
            if (length == n) break;
        }
        if (inverse) for (int i = 0; i < n; i++) { real[i] /= n; imag[i] /= n; }
    }
}
