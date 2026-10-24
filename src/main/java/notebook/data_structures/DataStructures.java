package notebook;

/** Zero-indexed contest data structures. */
public final class DataStructures {
    private DataStructures() {}

    /** Disjoint set union with path compression and union by size. */
    public static final class DSU {
        private final int[] parent, size;
        private int components;
        public DSU(int n) {
            if (n < 0) throw new IllegalArgumentException();
            parent = new int[n]; size = new int[n]; components = n;
            for (int i = 0; i < n; i++) { parent[i] = i; size[i] = 1; }
        }
        public int find(int x) {
            if (parent[x] != x) parent[x] = find(parent[x]);
            return parent[x];
        }
        public boolean union(int a, int b) {
            a = find(a); b = find(b);
            if (a == b) return false;
            if (size[a] < size[b]) { int t = a; a = b; b = t; }
            parent[b] = a; size[a] += size[b]; components--;
            return true;
        }
        public int size(int x) { return size[find(x)]; }
        public int components() { return components; }
    }

    /** Fenwick tree for point additions and half-open range sums. O(log n) per operation. */
    public static final class Fenwick {
        private final long[] tree;
        public Fenwick(int n) {
            if (n < 0) throw new IllegalArgumentException();
            tree = new long[n + 1];
        }
        public void add(int index, long delta) {
            if (index < 0 || index >= tree.length - 1) throw new IndexOutOfBoundsException();
            for (int i = index + 1; i < tree.length; i += i & -i) tree[i] += delta;
        }
        public long prefixSum(int endExclusive) {
            if (endExclusive < 0 || endExclusive >= tree.length) throw new IndexOutOfBoundsException();
            long result = 0;
            for (int i = endExclusive; i > 0; i -= i & -i) result += tree[i];
            return result;
        }
        public long rangeSum(int left, int rightExclusive) {
            if (left > rightExclusive) throw new IllegalArgumentException();
            return prefixSum(rightExclusive) - prefixSum(left);
        }
    }

    /** Iterative segment tree for point assignment and half-open range sums. */
    public static final class SegmentTree {
        private final long[] tree;
        private final int n, base;
        public SegmentTree(long[] values) {
            n = values.length;
            int power = 1;
            while (power < n) power <<= 1;
            base = power;
            tree = new long[2 * base];
            for (int i = 0; i < n; i++) tree[base + i] = values[i];
            for (int i = base - 1; i > 0; i--) tree[i] = tree[2 * i] + tree[2 * i + 1];
        }
        public void set(int index, long value) {
            if (index < 0 || index >= n) throw new IndexOutOfBoundsException();
            int p = base + index;
            tree[p] = value;
            for (p >>= 1; p > 0; p >>= 1) tree[p] = tree[2 * p] + tree[2 * p + 1];
        }
        public long sum(int left, int rightExclusive) {
            if (left < 0 || left > rightExclusive || rightExclusive > n) throw new IndexOutOfBoundsException();
            long result = 0;
            for (int l = left + base, r = rightExclusive + base; l < r; l >>= 1, r >>= 1) {
                if ((l & 1) != 0) result += tree[l++];
                if ((r & 1) != 0) result += tree[--r];
            }
            return result;
        }
    }
}
