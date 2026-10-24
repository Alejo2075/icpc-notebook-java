package notebook;

import java.util.Arrays;

/** Minimum of linear functions at a fixed set of query x-coordinates. */
public final class LiChaoTree {
    private record Line(long slope, long intercept) {
        long value(long x) { return Math.addExact(Math.multiplyExact(slope, x), intercept); }
    }
    private final long[] xs;
    private final Line[] lines;

    public LiChaoTree(long[] queryCoordinates) {
        if (queryCoordinates.length == 0) throw new IllegalArgumentException("empty domain");
        xs = queryCoordinates.clone();
        Arrays.sort(xs);
        for (int i = 1; i < xs.length; i++) if (xs[i] == xs[i - 1])
            throw new IllegalArgumentException("query coordinates must be distinct");
        lines = new Line[4 * xs.length];
    }

    /** Add y = slope*x + intercept. O(log domain size). */
    public void add(long slope, long intercept) { add(1, 0, xs.length, new Line(slope, intercept)); }

    private void add(int node, int left, int right, Line incoming) {
        if (lines[node] == null) { lines[node] = incoming; return; }
        int middle = (left + right) >>> 1;
        boolean betterLeft = incoming.value(xs[left]) < lines[node].value(xs[left]);
        boolean betterMiddle = incoming.value(xs[middle]) < lines[node].value(xs[middle]);
        if (betterMiddle) { Line old = lines[node]; lines[node] = incoming; incoming = old; }
        if (right - left == 1) return;
        if (betterLeft != betterMiddle) add(node * 2, left, middle, incoming);
        else add(node * 2 + 1, middle, right, incoming);
    }

    /** Minimum at a registered x; Long.MAX_VALUE when no line was added. O(log domain size). */
    public long query(long x) {
        int index = Arrays.binarySearch(xs, x);
        if (index < 0) throw new IllegalArgumentException("x was not registered");
        int node = 1, left = 0, right = xs.length;
        long result = Long.MAX_VALUE;
        while (true) {
            if (lines[node] != null) result = Math.min(result, lines[node].value(x));
            if (right - left == 1) return result;
            int middle = (left + right) >>> 1;
            if (index < middle) { node *= 2; right = middle; }
            else { node = node * 2 + 1; left = middle; }
        }
    }
}
