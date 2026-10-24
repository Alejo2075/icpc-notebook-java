package notebook;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Additional graph routines. Vertices are zero-indexed. */
public final class AdvancedGraphs {
    private AdvancedGraphs() {}

    /** Kosaraju SCC labels for a directed graph. O(V+E), iterative DFS. */
    public static int[] stronglyConnectedComponents(List<List<Integer>> graph) {
        int n = graph.size();
        List<List<Integer>> reverse = Graphs.newGraph(n);
        for (int v = 0; v < n; v++) for (int to : graph.get(v)) reverse.get(to).add(v);
        boolean[] seen = new boolean[n];
        List<Integer> order = new ArrayList<>();
        for (int start = 0; start < n; start++) {
            if (seen[start]) continue;
            ArrayDeque<int[]> stack = new ArrayDeque<>();
            seen[start] = true; stack.push(new int[] {start, 0});
            while (!stack.isEmpty()) {
                int[] frame = stack.peek();
                List<Integer> edges = graph.get(frame[0]);
                if (frame[1] == edges.size()) { order.add(frame[0]); stack.pop(); continue; }
                int to = edges.get(frame[1]++);
                if (!seen[to]) { seen[to] = true; stack.push(new int[] {to, 0}); }
            }
        }
        int[] component = new int[n];
        Arrays.fill(component, -1);
        int count = 0;
        for (int i = order.size() - 1; i >= 0; i--) {
            int start = order.get(i);
            if (component[start] != -1) continue;
            ArrayDeque<Integer> stack = new ArrayDeque<>();
            stack.push(start); component[start] = count;
            while (!stack.isEmpty()) {
                for (int to : reverse.get(stack.pop())) if (component[to] == -1) {
                    component[to] = count; stack.push(to);
                }
            }
            count++;
        }
        return component;
    }

    /** Bellman-Ford; throws if a reachable negative cycle exists. O(VE). */
    public static long[] bellmanFord(int n, List<Graphs.Edge> edges, int source) {
        long[] dist = new long[n];
        Arrays.fill(dist, Long.MAX_VALUE);
        dist[source] = 0;
        for (int pass = 0; pass < n; pass++) {
            boolean changed = false;
            for (Graphs.Edge edge : edges) {
                if (dist[edge.from()] == Long.MAX_VALUE) continue;
                long candidate = Math.addExact(dist[edge.from()], edge.weight());
                if (candidate < dist[edge.to()]) {
                    if (pass == n - 1) throw new IllegalArgumentException("reachable negative cycle");
                    dist[edge.to()] = candidate;
                    changed = true;
                }
            }
            if (!changed) break;
        }
        return dist;
    }

    /** Binary-lifting LCA for a connected, undirected tree. O(n log n) build, O(log n) query. */
    public static final class LCA {
        private final int[][] up;
        private final int[] depth;

        public LCA(List<List<Integer>> tree, int root) {
            int n = tree.size(), levels = 1;
            while ((1L << levels) <= Math.max(1, n)) levels++;
            up = new int[levels][n]; depth = new int[n];
            Arrays.fill(depth, -1);
            ArrayDeque<Integer> queue = new ArrayDeque<>();
            depth[root] = 0; up[0][root] = root; queue.add(root);
            while (!queue.isEmpty()) {
                int v = queue.remove();
                for (int to : tree.get(v)) if (depth[to] == -1) {
                    depth[to] = depth[v] + 1; up[0][to] = v; queue.add(to);
                }
            }
            for (int d : depth) if (d < 0) throw new IllegalArgumentException("disconnected tree");
            for (int k = 1; k < levels; k++)
                for (int v = 0; v < n; v++) up[k][v] = up[k - 1][up[k - 1][v]];
        }

        public int query(int a, int b) {
            if (depth[a] < depth[b]) { int t = a; a = b; b = t; }
            int delta = depth[a] - depth[b];
            for (int k = 0; delta > 0; k++, delta >>= 1) if ((delta & 1) != 0) a = up[k][a];
            if (a == b) return a;
            for (int k = up.length - 1; k >= 0; k--) if (up[k][a] != up[k][b]) {
                a = up[k][a]; b = up[k][b];
            }
            return up[0][a];
        }
    }

    /** Dinic maximum flow with nonnegative long capacities. */
    public static final class Dinic {
        private static final class Arc {
            int to, reverse;
            long capacity;
            Arc(int to, int reverse, long capacity) {
                this.to = to; this.reverse = reverse; this.capacity = capacity;
            }
        }
        private final List<List<Arc>> graph = new ArrayList<>();
        private final int[] level, next;
        public Dinic(int n) {
            if (n < 0) throw new IllegalArgumentException();
            for (int i = 0; i < n; i++) graph.add(new ArrayList<>());
            level = new int[n]; next = new int[n];
        }
        public void addEdge(int from, int to, long capacity) {
            if (capacity < 0) throw new IllegalArgumentException();
            int forwardReverse = graph.get(to).size() + (from == to ? 1 : 0);
            Arc forward = new Arc(to, forwardReverse, capacity);
            Arc backward = new Arc(from, graph.get(from).size(), 0);
            graph.get(from).add(forward); graph.get(to).add(backward);
        }
        private boolean levels(int source, int sink) {
            Arrays.fill(level, -1);
            ArrayDeque<Integer> queue = new ArrayDeque<>();
            queue.add(source); level[source] = 0;
            while (!queue.isEmpty()) {
                int v = queue.remove();
                for (Arc arc : graph.get(v)) if (arc.capacity > 0 && level[arc.to] == -1) {
                    level[arc.to] = level[v] + 1; queue.add(arc.to);
                }
            }
            return level[sink] >= 0;
        }
        private long send(int v, int sink, long flow) {
            if (v == sink) return flow;
            List<Arc> arcs = graph.get(v);
            for (; next[v] < arcs.size(); next[v]++) {
                Arc arc = arcs.get(next[v]);
                if (arc.capacity == 0 || level[arc.to] != level[v] + 1) continue;
                long pushed = send(arc.to, sink, Math.min(flow, arc.capacity));
                if (pushed == 0) continue;
                arc.capacity -= pushed;
                Arc back = graph.get(arc.to).get(arc.reverse);
                back.capacity = Math.addExact(back.capacity, pushed);
                return pushed;
            }
            return 0;
        }
        public long maxFlow(int source, int sink) {
            if (source == sink) return 0;
            long total = 0;
            while (levels(source, sink)) {
                Arrays.fill(next, 0);
                long pushed;
                while ((pushed = send(source, sink, Long.MAX_VALUE)) > 0) total = Math.addExact(total, pushed);
            }
            return total;
        }
    }
}
