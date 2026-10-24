package notebook;

import java.util.ArrayList;
import java.util.List;

/** Bridges and articulation points in an undirected multigraph. Recursive DFS uses O(V) stack. */
public final class UndirectedCuts {
    private UndirectedCuts() {}

    public record Edge(int u, int v) {}
    public record Result(List<Integer> bridges, List<Integer> articulationPoints) {}

    /** Bridges are returned as indices into edges. O(V+E). */
    public static Result find(int n, List<Edge> edges) {
        List<List<int[]>> graph = new ArrayList<>();
        for (int i = 0; i < n; i++) graph.add(new ArrayList<>());
        for (int i = 0; i < edges.size(); i++) {
            Edge edge = edges.get(i);
            graph.get(edge.u).add(new int[] {edge.v, i});
            graph.get(edge.v).add(new int[] {edge.u, i});
        }
        int[] tin = new int[n], low = new int[n];
        boolean[] cut = new boolean[n];
        List<Integer> bridges = new ArrayList<>();
        int[] timer = {1};
        for (int v = 0; v < n; v++) if (tin[v] == 0)
            dfs(v, -1, graph, tin, low, cut, bridges, timer);
        List<Integer> cuts = new ArrayList<>();
        for (int i = 0; i < n; i++) if (cut[i]) cuts.add(i);
        bridges.sort(null);
        return new Result(bridges, cuts);
    }

    private static void dfs(int v, int parentEdge, List<List<int[]>> graph, int[] tin, int[] low,
                            boolean[] cut, List<Integer> bridges, int[] timer) {
        tin[v] = low[v] = timer[0]++;
        int children = 0;
        for (int[] edge : graph.get(v)) {
            int to = edge[0], id = edge[1];
            if (id == parentEdge) continue;
            if (tin[to] != 0) low[v] = Math.min(low[v], tin[to]);
            else {
                dfs(to, id, graph, tin, low, cut, bridges, timer);
                low[v] = Math.min(low[v], low[to]);
                if (low[to] > tin[v]) bridges.add(id);
                if (parentEdge != -1 && low[to] >= tin[v]) cut[v] = true;
                children++;
            }
        }
        if (parentEdge == -1 && children > 1) cut[v] = true;
    }
}
