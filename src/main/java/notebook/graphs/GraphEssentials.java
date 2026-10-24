package notebook;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** Graph routines that complement BFS, DFS, Dijkstra, and Kruskal. */
public final class GraphEssentials {
    private GraphEssentials() {}

    /** Component labels for an undirected adjacency list. O(V+E). */
    public static int[] components(List<List<Integer>> graph) {
        int[] label = new int[graph.size()];
        Arrays.fill(label, -1);
        int count = 0;
        ArrayDeque<Integer> queue = new ArrayDeque<>();
        for (int start = 0; start < graph.size(); start++) {
            if (label[start] != -1) continue;
            label[start] = count; queue.add(start);
            while (!queue.isEmpty()) {
                int v = queue.remove();
                for (int to : graph.get(v)) if (label[to] == -1) {
                    label[to] = count; queue.add(to);
                }
            }
            count++;
        }
        return label;
    }

    /** Two-color an undirected graph; return null if it is not bipartite. O(V+E). */
    public static int[] bipartiteColors(List<List<Integer>> graph) {
        int[] color = new int[graph.size()];
        Arrays.fill(color, -1);
        ArrayDeque<Integer> queue = new ArrayDeque<>();
        for (int start = 0; start < graph.size(); start++) {
            if (color[start] != -1) continue;
            color[start] = 0; queue.add(start);
            while (!queue.isEmpty()) {
                int v = queue.remove();
                for (int to : graph.get(v)) {
                    if (color[to] == color[v]) return null;
                    if (color[to] == -1) { color[to] = color[v] ^ 1; queue.add(to); }
                }
            }
        }
        return color;
    }

    public record BinaryEdge(int to, int weight) {}

    /** Shortest paths with edge weights exactly 0 or 1. O(V+E). */
    public static int[] zeroOneBfs(List<List<BinaryEdge>> graph, int source) {
        int[] dist = new int[graph.size()];
        Arrays.fill(dist, Integer.MAX_VALUE);
        ArrayDeque<Integer> deque = new ArrayDeque<>();
        dist[source] = 0; deque.add(source);
        while (!deque.isEmpty()) {
            int v = deque.removeFirst();
            for (BinaryEdge edge : graph.get(v)) {
                if (edge.weight != 0 && edge.weight != 1) throw new IllegalArgumentException("weight must be 0 or 1");
                int candidate = dist[v] + edge.weight;
                if (candidate < dist[edge.to]) {
                    dist[edge.to] = candidate;
                    if (edge.weight == 0) deque.addFirst(edge.to);
                    else deque.addLast(edge.to);
                }
            }
        }
        return dist;
    }

    /** All-pairs shortest paths; Long.MAX_VALUE means unreachable. O(V^3). */
    public static long[][] floydWarshall(long[][] weights) {
        int n = weights.length;
        long[][] dist = new long[n][n];
        for (int i = 0; i < n; i++) {
            if (weights[i].length != n) throw new IllegalArgumentException("matrix must be square");
            dist[i] = weights[i].clone();
            dist[i][i] = Math.min(dist[i][i], 0);
        }
        for (int k = 0; k < n; k++)
            for (int i = 0; i < n; i++)
                for (int j = 0; j < n; j++) {
                    if (dist[i][k] == Long.MAX_VALUE || dist[k][j] == Long.MAX_VALUE) continue;
                    long candidate = Math.addExact(dist[i][k], dist[k][j]);
                    if (candidate < dist[i][j]) dist[i][j] = candidate;
                }
        for (int i = 0; i < n; i++) if (dist[i][i] < 0) throw new IllegalArgumentException("negative cycle");
        return dist;
    }

    /** Eulerian trail in a directed multigraph, or empty list if none exists. O(V+E). */
    public static List<Integer> directedEulerTrail(int n, int[][] edges) {
        if (n == 0) return List.of();
        List<List<Integer>> graph = Graphs.newGraph(n);
        int[] in = new int[n], out = new int[n], next = new int[n];
        for (int[] edge : edges) {
            graph.get(edge[0]).add(edge[1]); out[edge[0]]++; in[edge[1]]++;
        }
        int start = -1, end = -1;
        for (int v = 0; v < n; v++) {
            int delta = out[v] - in[v];
            if (delta == 1) { if (start != -1) return List.of(); start = v; }
            else if (delta == -1) { if (end != -1) return List.of(); end = v; }
            else if (delta != 0) return List.of();
        }
        if ((start == -1) != (end == -1)) return List.of();
        if (start == -1) for (int v = 0; v < n; v++) if (out[v] > 0) { start = v; break; }
        if (start == -1) return List.of(0);
        ArrayDeque<Integer> stack = new ArrayDeque<>();
        List<Integer> trail = new ArrayList<>();
        stack.push(start);
        while (!stack.isEmpty()) {
            int v = stack.peek();
            if (next[v] < graph.get(v).size()) stack.push(graph.get(v).get(next[v]++));
            else trail.add(stack.pop());
        }
        if (trail.size() != edges.length + 1) return List.of();
        Collections.reverse(trail);
        return trail;
    }
}
