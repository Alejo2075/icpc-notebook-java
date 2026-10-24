package notebook;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;

/** Algorithms for connected, undirected trees with vertices 0..n-1. */
public final class TreeAlgorithms {
    private TreeAlgorithms() {}

    /** Diameter length in edges. O(n). */
    public static int diameter(List<List<Integer>> tree) {
        if (tree.isEmpty()) return 0;
        int endpoint = farthest(tree, 0)[0];
        return farthest(tree, endpoint)[1];
    }

    private static int[] farthest(List<List<Integer>> tree, int start) {
        int[] dist = Graphs.bfs(tree, start);
        int best = start;
        for (int i = 0; i < dist.length; i++) if (dist[i] > dist[best]) best = i;
        return new int[] {best, dist[best]};
    }

    /** Euler entry/exit times; subtree(v) is [tin[v], tout[v]). O(n). */
    public static final class EulerTour {
        public final int[] tin, tout, order;
        public EulerTour(List<List<Integer>> tree, int root) {
            int n = tree.size();
            tin = new int[n]; tout = new int[n]; order = new int[n];
            Arrays.fill(tin, -1);
            ArrayDeque<int[]> stack = new ArrayDeque<>();
            stack.push(new int[] {root, -1, 0});
            int timer = 0;
            while (!stack.isEmpty()) {
                int[] frame = stack.peek();
                int v = frame[0], parent = frame[1];
                if (frame[2] == 0) { tin[v] = timer; order[timer++] = v; }
                if (frame[2] == tree.get(v).size()) { tout[v] = timer; stack.pop(); continue; }
                int to = tree.get(v).get(frame[2]++);
                if (to != parent && tin[to] == -1) stack.push(new int[] {to, v, 0});
            }
            if (timer != n) throw new IllegalArgumentException("disconnected tree");
        }
    }

    /** Point updates and vertex-sum path queries using heavy-light decomposition. O(log^2 n). */
    public static final class HeavyLight {
        private final List<List<Integer>> tree;
        private final int[] parent, depth, size, heavy, head, position;
        private final DataStructures.SegmentTree sums;
        private int clock;

        public HeavyLight(List<List<Integer>> tree, long[] values, int root) {
            if (tree.size() != values.length) throw new IllegalArgumentException();
            this.tree = tree;
            int n = tree.size();
            parent = new int[n]; depth = new int[n]; size = new int[n];
            heavy = new int[n]; head = new int[n]; position = new int[n];
            Arrays.fill(heavy, -1);
            Arrays.fill(parent, -1);
            buildSizes(root, -1);
            if (size[root] != n) throw new IllegalArgumentException("disconnected tree");
            decompose(root, root);
            long[] ordered = new long[n];
            for (int v = 0; v < n; v++) ordered[position[v]] = values[v];
            sums = new DataStructures.SegmentTree(ordered);
        }

        private void buildSizes(int v, int p) {
            parent[v] = p; size[v] = 1;
            int largest = 0;
            for (int to : tree.get(v)) if (to != p) {
                depth[to] = depth[v] + 1;
                buildSizes(to, v);
                size[v] += size[to];
                if (size[to] > largest) { largest = size[to]; heavy[v] = to; }
            }
        }

        private void decompose(int v, int chainHead) {
            head[v] = chainHead; position[v] = clock++;
            if (heavy[v] != -1) decompose(heavy[v], chainHead);
            for (int to : tree.get(v)) if (to != parent[v] && to != heavy[v]) decompose(to, to);
        }

        public void set(int vertex, long value) { sums.set(position[vertex], value); }

        public long pathSum(int a, int b) {
            long result = 0;
            while (head[a] != head[b]) {
                if (depth[head[a]] < depth[head[b]]) { int tmp = a; a = b; b = tmp; }
                result += sums.sum(position[head[a]], position[a] + 1);
                a = parent[head[a]];
            }
            if (depth[a] > depth[b]) { int tmp = a; a = b; b = tmp; }
            return result + sums.sum(position[a], position[b] + 1);
        }
    }
}
