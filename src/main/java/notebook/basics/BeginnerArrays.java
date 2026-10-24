package notebook;

import java.util.Arrays;

/** Small array patterns that are useful before learning advanced data structures. */
public final class BeginnerArrays {
    private BeginnerArrays() {}

    /** Sum all values. O(n) time, O(1) extra space; throws if the sum exceeds long. */
    public static long sum(int[] values) {
        long result = 0;
        for (int value : values) result = Math.addExact(result, value);
        return result;
    }

    /** Return the index of the first target, or -1. O(n) time, O(1) space. */
    public static int linearSearch(int[] values, int target) {
        for (int i = 0; i < values.length; i++) if (values[i] == target) return i;
        return -1;
    }

    /** Count occurrences of a target. O(n) time, O(1) space. */
    public static int count(int[] values, int target) {
        int result = 0;
        for (int value : values) if (value == target) result++;
        return result;
    }

    /** Reverse an array in place. O(n) time, O(1) extra space. */
    public static void reverse(int[] values) {
        for (int left = 0, right = values.length - 1; left < right; left++, right--) {
            int temp = values[left]; values[left] = values[right]; values[right] = temp;
        }
    }

    /** Inclusive prefix sums with prefix[0] = 0. O(n) time and space. */
    public static long[] prefixSums(int[] values) {
        long[] prefix = new long[values.length + 1];
        for (int i = 0; i < values.length; i++) prefix[i + 1] = Math.addExact(prefix[i], values[i]);
        return prefix;
    }

    /** Sum [left, right) using a prefix array. O(1) time. */
    public static long rangeSum(long[] prefix, int left, int right) {
        if (left < 0 || right < left || right >= prefix.length) throw new IndexOutOfBoundsException();
        return prefix[right] - prefix[left];
    }

    /** Two-sum by trying every pair; returns indices or an empty array. O(n^2) time. */
    public static int[] pairWithSum(int[] values, long target) {
        for (int i = 0; i < values.length; i++)
            for (int j = i + 1; j < values.length; j++)
                if ((long) values[i] + values[j] == target) return new int[] {i, j};
        return new int[0];
    }

    /** Copy and sort values without modifying the input. O(n log n) time. */
    public static int[] sortedCopy(int[] values) {
        int[] result = values.clone();
        Arrays.sort(result);
        return result;
    }
}
