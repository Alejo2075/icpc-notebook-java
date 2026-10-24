package notebook;

/** Immutable and mutable range query structures; all ranges are half-open. */
public final class RangeQueries {
    private RangeQueries() {}

    /** Rectangle sums on [top,bottom) x [left,right). O(rows*cols) build, O(1) query. */
    public static final class Prefix2D {
        private final long[][] prefix;
        public Prefix2D(int[][] grid) {
            int rows = grid.length, cols = rows == 0 ? 0 : grid[0].length;
            prefix = new long[rows + 1][cols + 1];
            for (int r = 0; r < rows; r++) {
                if (grid[r].length != cols) throw new IllegalArgumentException("ragged grid");
                for (int c = 0; c < cols; c++)
                    prefix[r + 1][c + 1] = grid[r][c] + prefix[r][c + 1]
                        + prefix[r + 1][c] - prefix[r][c];
            }
        }
        public long sum(int top, int left, int bottom, int right) {
            return prefix[bottom][right] - prefix[top][right]
                - prefix[bottom][left] + prefix[top][left];
        }
    }

    /** Static range minimum. O(n log n) build, O(1) query, O(n log n) space. */
    public static final class SparseTableMin {
        private final int[][] table;
        private final int[] log;
        public SparseTableMin(int[] a) {
            log = new int[a.length + 1];
            for (int i = 2; i < log.length; i++) log[i] = log[i / 2] + 1;
            table = new int[a.length == 0 ? 0 : log[a.length] + 1][a.length];
            if (a.length == 0) return;
            table[0] = a.clone();
            for (int k = 1; k < table.length; k++)
                for (int i = 0; i + (1 << k) <= a.length; i++)
                    table[k][i] = Math.min(table[k - 1][i], table[k - 1][i + (1 << (k - 1))]);
        }
        public int min(int left, int right) {
            if (left < 0 || left >= right || right > log.length - 1) throw new IndexOutOfBoundsException();
            int k = log[right - left];
            return Math.min(table[k][left], table[k][right - (1 << k)]);
        }
    }

    /** Range addition and range sum. O(n) build, O(log n) operations. */
    public static final class LazySegmentTree {
        private final long[] sum, lazy;
        private final int n;
        public LazySegmentTree(long[] values) {
            n = values.length;
            sum = new long[4 * Math.max(1, n)];
            lazy = new long[sum.length];
            if (n > 0) build(1, 0, n, values);
        }
        private void build(int node, int l, int r, long[] values) {
            if (r - l == 1) { sum[node] = values[l]; return; }
            int mid = (l + r) >>> 1;
            build(node * 2, l, mid, values);
            build(node * 2 + 1, mid, r, values);
            sum[node] = sum[node * 2] + sum[node * 2 + 1];
        }
        private void apply(int node, int length, long delta) {
            sum[node] += delta * length;
            lazy[node] += delta;
        }
        private void push(int node, int l, int r) {
            if (lazy[node] == 0 || r - l <= 1) return;
            int mid = (l + r) >>> 1;
            apply(node * 2, mid - l, lazy[node]);
            apply(node * 2 + 1, r - mid, lazy[node]);
            lazy[node] = 0;
        }
        public void add(int left, int right, long delta) {
            if (left < 0 || left > right || right > n) throw new IndexOutOfBoundsException();
            if (n > 0) add(1, 0, n, left, right, delta);
        }
        private void add(int node, int l, int r, int ql, int qr, long delta) {
            if (qr <= l || r <= ql) return;
            if (ql <= l && r <= qr) { apply(node, r - l, delta); return; }
            push(node, l, r);
            int mid = (l + r) >>> 1;
            add(node * 2, l, mid, ql, qr, delta);
            add(node * 2 + 1, mid, r, ql, qr, delta);
            sum[node] = sum[node * 2] + sum[node * 2 + 1];
        }
        public long sum(int left, int right) {
            if (left < 0 || left > right || right > n) throw new IndexOutOfBoundsException();
            return n == 0 ? 0 : sum(1, 0, n, left, right);
        }
        private long sum(int node, int l, int r, int ql, int qr) {
            if (qr <= l || r <= ql) return 0;
            if (ql <= l && r <= qr) return sum[node];
            push(node, l, r);
            int mid = (l + r) >>> 1;
            return sum(node * 2, l, mid, ql, qr) + sum(node * 2 + 1, mid, r, ql, qr);
        }
    }
}
