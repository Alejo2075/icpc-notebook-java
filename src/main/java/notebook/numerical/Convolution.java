package notebook;

import java.util.Arrays;

/** NTT convolution modulo 998244353 (primitive root 3). O(n log n). */
public final class Convolution {
    public static final int MOD = 998244353;
    private Convolution() {}

    public static int[] multiply(int[] a, int[] b) {
        if (a.length == 0 || b.length == 0) return new int[0];
        int required = a.length + b.length - 1, size = 1;
        while (size < required) size <<= 1;
        if (size > (1 << 23)) throw new IllegalArgumentException("NTT size exceeds 2^23");
        int[] left = Arrays.copyOf(a, size), right = Arrays.copyOf(b, size);
        for (int i = 0; i < size; i++) {
            left[i] = Math.floorMod(left[i], MOD);
            right[i] = Math.floorMod(right[i], MOD);
        }
        transform(left, false); transform(right, false);
        for (int i = 0; i < size; i++) left[i] = (int) ((long) left[i] * right[i] % MOD);
        transform(left, true);
        return Arrays.copyOf(left, required);
    }

    private static void transform(int[] a, boolean inverse) {
        int n = a.length;
        for (int i = 1, j = 0; i < n; i++) {
            int bit = n >>> 1;
            for (; (j & bit) != 0; bit >>>= 1) j ^= bit;
            j ^= bit;
            if (i < j) { int t = a[i]; a[i] = a[j]; a[j] = t; }
        }
        for (int len = 2; len <= n; len <<= 1) {
            long root = NumberTheory.modPow(3, (MOD - 1) / len, MOD);
            if (inverse) root = NumberTheory.modPow(root, MOD - 2, MOD);
            for (int start = 0; start < n; start += len) {
                long omega = 1;
                for (int j = 0; j < len / 2; j++) {
                    int u = a[start + j];
                    int v = (int) (omega * a[start + j + len / 2] % MOD);
                    a[start + j] = (int) (((long) u + v) % MOD);
                    a[start + j + len / 2] = (u - v + MOD) % MOD;
                    omega = omega * root % MOD;
                }
            }
        }
        if (inverse) {
            long invN = NumberTheory.modPow(n, MOD - 2, MOD);
            for (int i = 0; i < n; i++) a[i] = (int) (a[i] * invN % MOD);
        }
    }
}
