package notebook;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/** Offline static range queries ordered by Mo's square-root decomposition. */
public final class MoQueries {
    private MoQueries() {}

    /** Distinct-value counts for half-open [left,right) queries. O((n+q)*sqrt(n)) typical. */
    public static int[] distinctCounts(int[] values, int[][] ranges) {
        int n = values.length, block = Math.max(1, (int) Math.sqrt(n));
        Map<Integer, Integer> compressed = new HashMap<>();
        int[] ids = new int[n];
        for (int i = 0; i < n; i++) {
            if (!compressed.containsKey(values[i])) compressed.put(values[i], compressed.size());
            ids[i] = compressed.get(values[i]);
        }
        Integer[] order = new Integer[ranges.length];
        for (int i = 0; i < ranges.length; i++) {
            if (ranges[i].length != 2 || ranges[i][0] < 0 || ranges[i][0] > ranges[i][1]
                    || ranges[i][1] > n) throw new IllegalArgumentException("invalid range");
            order[i] = i;
        }
        Arrays.sort(order, (a, b) -> {
            int leftBlock = ranges[a][0] / block, rightBlock = ranges[b][0] / block;
            if (leftBlock != rightBlock) return Integer.compare(leftBlock, rightBlock);
            return leftBlock % 2 == 0 ? Integer.compare(ranges[a][1], ranges[b][1])
                    : Integer.compare(ranges[b][1], ranges[a][1]);
        });
        int[] frequency = new int[compressed.size()], result = new int[ranges.length];
        int left = 0, right = 0, distinct = 0;
        for (int index : order) {
            int targetLeft = ranges[index][0], targetRight = ranges[index][1];
            while (left > targetLeft) if (frequency[ids[--left]]++ == 0) distinct++;
            while (right < targetRight) if (frequency[ids[right++]]++ == 0) distinct++;
            while (left < targetLeft) if (--frequency[ids[left++]] == 0) distinct--;
            while (right > targetRight) if (--frequency[ids[--right]] == 0) distinct--;
            result[index] = distinct;
        }
        return result;
    }
}
