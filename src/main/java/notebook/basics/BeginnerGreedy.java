package notebook;

import java.util.Arrays;
import java.util.Comparator;

/** Greedy choices with simple exchange-argument proofs. */
public final class BeginnerGreedy {
    private BeginnerGreedy() {}

    /** Maximum number of non-overlapping [start,end) intervals. O(n log n) time. */
    public static int maxNonOverlapping(long[][] intervals) {
        long[][] sorted = new long[intervals.length][];
        for (int i = 0; i < intervals.length; i++) {
            if (intervals[i].length != 2 || intervals[i][0] > intervals[i][1])
                throw new IllegalArgumentException("invalid interval");
            sorted[i] = intervals[i].clone();
        }
        Arrays.sort(sorted, Comparator.comparingLong((long[] interval) -> interval[1])
                .thenComparingLong(interval -> interval[0]));
        int chosen = 0;
        long lastEnd = Long.MIN_VALUE;
        for (long[] interval : sorted) if (interval[0] >= lastEnd) {
            chosen++;
            lastEnd = interval[1];
        }
        return chosen;
    }

    /** Minimum points that cover closed intervals [left,right]. O(n log n) time. */
    public static int pointsToCover(long[][] intervals) {
        long[][] sorted = new long[intervals.length][];
        for (int i = 0; i < intervals.length; i++) {
            if (intervals[i].length != 2 || intervals[i][0] > intervals[i][1])
                throw new IllegalArgumentException("invalid interval");
            sorted[i] = intervals[i].clone();
        }
        Arrays.sort(sorted, Comparator.comparingLong(interval -> interval[1]));
        int count = 0;
        long lastPoint = 0;
        boolean hasPoint = false;
        for (long[] interval : sorted) if (!hasPoint || interval[0] > lastPoint) {
            lastPoint = interval[1];
            hasPoint = true;
            count++;
        }
        return count;
    }
}
