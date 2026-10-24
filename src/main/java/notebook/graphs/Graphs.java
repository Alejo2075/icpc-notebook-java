package notebook;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/** Zero-indexed graph algorithms. */
public final class Graphs {
    private Graphs() {}

    /** Directed adjacency list. For undirected graphs add both directions. */
    public static List<List<Integer>> newGraph(int n) {
        if (n < 0) throw new IllegalArgumentException();
        List<List<Integer>> graph = new ArrayList<>();
        for (int i = 0; i < n; i++) graph.add(new ArrayList<>());
        return graph;
    }

    /** Unweighted shortest-path distances; unreachable vertices have -1. O(V+E). */
    public static int[] bfs(List<List<Integer>> graph, int start) {
        int[] dist = new int[graph.size()];
        Arrays.fill(dist, -1);
        ArrayDeque<Integer> queue = new ArrayDeque<>();
        dist[start] = 0; queue.add(start);
        while (!queue.isEmpty()) {
            int v = queue.remove();
            for (int to : graph.get(v)) if (dist[to] == -1) {
                dist[to] = dist[v] + 1; queue.add(to);
            }
        }
        return dist;
    }

    /** Iterative DFS visitation order. O(V+E). */
    public static List<Integer> dfs(List<List<Integer>> graph, int start) {
        boolean[] seen = new boolean[graph.size()];
        ArrayDeque<Integer> stack = new ArrayDeque<>();
        List<Integer> order = new ArrayList<>();
        stack.push(start);
        while (!stack.isEmpty()) {
            int v = stack.pop();
            if (seen[v]) continue;
            seen[v] = true; order.add(v);
            List<Integer> neighbors = graph.get(v);
            for (int i = neighbors.size() - 1; i >= 0; i--) if (!seen[neighbors.get(i)]) stack.push(neighbors.get(i));
        }
        return order;
    }

    /** Kahn topological order; throws if the directed graph contains a cycle. */
    public static List<Integer> topologicalSort(List<List<Integer>> graph) {
        int[] indegree = new int[graph.size()];
        for (List<Integer> edges : graph) for (int to : edges) indegree[to]++;
        ArrayDeque<Integer> queue = new ArrayDeque<>();
        for (int i = 0; i < indegree.length; i++) if (indegree[i] == 0) queue.add(i);
        List<Integer> result = new ArrayList<>();
        while (!queue.isEmpty()) {
            int v = queue.remove(); result.add(v);
            for (int to : graph.get(v)) if (--indegree[to] == 0) queue.add(to);
        }
        if (result.size() != graph.size()) throw new IllegalArgumentException("graph has a cycle");
        return result;
    }

    public record WeightedEdge(int to, long weight) {}

    /** Dijkstra; requires nonnegative weights. Unreachable vertices have Long.MAX_VALUE. */
    public static long[] dijkstra(List<List<WeightedEdge>> graph, int start) {
        long[] dist = new long[graph.size()];
        Arrays.fill(dist, Long.MAX_VALUE);
        PriorityQueue<long[]> queue = new PriorityQueue<>(Comparator.comparingLong(a -> a[0]));
        dist[start] = 0; queue.add(new long[] {0, start});
        while (!queue.isEmpty()) {
            long[] current = queue.remove();
            long d = current[0]; int v = (int) current[1];
            if (d != dist[v]) continue;
            for (WeightedEdge edge : graph.get(v)) {
                if (edge.weight < 0) throw new IllegalArgumentException("negative edge");
                if (d > Long.MAX_VALUE - edge.weight) continue;
                long candidate = d + edge.weight;
                if (candidate < dist[edge.to]) {
                    dist[edge.to] = candidate;
                    queue.add(new long[] {candidate, edge.to});
                }
            }
        }
        return dist;
    }

    public record Edge(int from, int to, long weight) {}

    /** Kruskal MST; throws if disconnected. O(E log E). */
    public static long kruskal(int n, List<Edge> edges) {
        List<Edge> sorted = new ArrayList<>(edges);
        sorted.sort(Comparator.comparingLong(Edge::weight));
        DataStructures.DSU dsu = new DataStructures.DSU(n);
        long total = 0;
        for (Edge edge : sorted) if (dsu.union(edge.from, edge.to)) total = Math.addExact(total, edge.weight);
        if (n > 0 && dsu.components() != 1) throw new IllegalArgumentException("disconnected graph");
        return total;
    }
}
