package notebook;

import java.util.Arrays;

/** Building blocks used before choosing a more specialized algorithm. */
public final class Fundamentals {
    private Fundamentals() {}

    /** prefix[i] is the sum of a[0..i); query [l,r) as prefix[r]-prefix[l]. O(n). */
    public static long[] prefixSums(int[] a) {
        long[] prefix = new long[a.length + 1];
        for (int i = 0; i < a.length; i++) prefix[i + 1] = prefix[i] + a[i];
        return prefix;
    }

    /** Apply [l,r) additions in O(updates+n). Each update is {l,r,delta}. */
    public static long[] rangeAdd(int n, long[][] updates) {
        long[] difference = new long[n + 1];
        for (long[] update : updates) {
            int l = Math.toIntExact(update[0]), r = Math.toIntExact(update[1]);
            if (l < 0 || l > r || r > n) throw new IndexOutOfBoundsException();
            difference[l] += update[2];
            difference[r] -= update[2];
        }
        long[] result = new long[n];
        long sum = 0;
        for (int i = 0; i < n; i++) { sum += difference[i]; result[i] = sum; }
        return result;
    }

    /** Sorted distinct values. Use Arrays.binarySearch(result, x) for compressed IDs. O(n log n). */
    public static int[] sortedUnique(int[] a) {
        int[] sorted = a.clone();
        Arrays.sort(sorted);
        int size = 0;
        for (int value : sorted)
            if (size == 0 || value != sorted[size - 1]) sorted[size++] = value;
        return Arrays.copyOf(sorted, size);
    }

    /** Two pointers: number of pairs i<j with a[i]+a[j] <= limit. Input must be sorted. O(n). */
    public static long countPairsAtMost(int[] sorted, long limit) {
        long count = 0;
        for (int l = 0, r = sorted.length - 1; l < r;) {
            if ((long) sorted[l] + sorted[r] <= limit) { count += r - l; l++; }
            else r--;
        }
        return count;
    }

    /** Longest window with sum <= limit; requires nonnegative elements. O(n). */
    public static int longestWindowAtMost(int[] a, long limit) {
        long sum = 0;
        int best = 0, left = 0;
        for (int right = 0; right < a.length; right++) {
            if (a[right] < 0) throw new IllegalArgumentException("negative element");
            sum += a[right];
            while (left <= right && sum > limit) sum -= a[left++];
            best = Math.max(best, right - left + 1);
        }
        return best;
    }

    /** Maximum nonempty subarray sum. O(n); returns 0 for empty input. */
    public static long maxSubarraySum(int[] a) {
        if (a.length == 0) return 0;
        long ending = a[0], best = ending;
        for (int i = 1; i < a.length; i++) {
            ending = Math.max(a[i], ending + a[i]);
            best = Math.max(best, ending);
        }
        return best;
    }

    /** Number of index pairs i<j with a[i]>a[j]. O(n log n). */
    public static long inversions(int[] a) {
        int[] copy = a.clone(), scratch = new int[a.length];
        return mergeCount(copy, scratch, 0, a.length);
    }

    private static long mergeCount(int[] a, int[] tmp, int lo, int hi) {
        if (hi - lo <= 1) return 0;
        int mid = (lo + hi) >>> 1;
        long count = mergeCount(a, tmp, lo, mid) + mergeCount(a, tmp, mid, hi);
        int i = lo, j = mid;
        for (int k = lo; k < hi; k++) {
            if (j == hi || (i < mid && a[i] <= a[j])) tmp[k] = a[i++];
            else { tmp[k] = a[j++]; count += mid - i; }
        }
        System.arraycopy(tmp, lo, a, lo, hi - lo);
        return count;
    }

    /** Lexicographically next permutation in place; false after wrapping to sorted order. O(n). */
    public static boolean nextPermutation(int[] a) {
        int i = a.length - 2;
        while (i >= 0 && a[i] >= a[i + 1]) i--;
        if (i >= 0) {
            int j = a.length - 1;
            while (a[j] <= a[i]) j--;
            int t = a[i]; a[i] = a[j]; a[j] = t;
        }
        for (int l = i + 1, r = a.length - 1; l < r; l++, r--) {
            int t = a[l]; a[l] = a[r]; a[r] = t;
        }
        return i >= 0;
    }

    /** Enumerate nonempty submasks of mask; mask must be nonnegative. O(2^popcount(mask)). */
    public static int[] nonemptySubmasks(int mask) {
        if (mask < 0) throw new IllegalArgumentException();
        if (Integer.bitCount(mask) > 20) throw new IllegalArgumentException("too many submasks to materialize");
        int[] result = new int[(1 << Integer.bitCount(mask)) - 1];
        int index = 0;
        for (int sub = mask; sub != 0; sub = (sub - 1) & mask) result[index++] = sub;
        return result;
    }
}
