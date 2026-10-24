package notebook;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** First graph routines using adjacency lists and an explicit queue. */
public final class BeginnerGraphs {
    private BeginnerGraphs() {}

    /** Build an undirected adjacency list from zero-indexed edges. O(V+E) time and space. */
    public static List<List<Integer>> undirectedGraph(int vertices, int[][] edges) {
        if (vertices < 0) throw new IllegalArgumentException("negative vertex count");
        List<List<Integer>> graph = new ArrayList<>();
        for (int i = 0; i < vertices; i++) graph.add(new ArrayList<>());
        for (int[] edge : edges) {
            if (edge.length != 2) throw new IllegalArgumentException("edge must contain two vertices");
            graph.get(edge[0]).add(edge[1]);
            graph.get(edge[1]).add(edge[0]);
        }
        return graph;
    }

    /** BFS distances from source; -1 means unreachable. O(V+E) time. */
    public static int[] distances(List<List<Integer>> graph, int source) {
        int[] distance = new int[graph.size()];
        Arrays.fill(distance, -1);
        ArrayDeque<Integer> queue = new ArrayDeque<>();
        distance[source] = 0;
        queue.add(source);
        while (!queue.isEmpty()) {
            int vertex = queue.remove();
            for (int next : graph.get(vertex)) if (distance[next] == -1) {
                distance[next] = distance[vertex] + 1;
                queue.add(next);
            }
        }
        return distance;
    }

    /** Count four-neighbor components of true cells. O(rows*columns) time and space. */
    public static int gridComponents(boolean[][] open) {
        int rows = open.length;
        if (rows == 0) return 0;
        int columns = open[0].length;
        for (boolean[] row : open) if (row.length != columns) throw new IllegalArgumentException("ragged grid");
        boolean[][] seen = new boolean[rows][columns];
        int[] dr = {-1, 1, 0, 0}, dc = {0, 0, -1, 1};
        ArrayDeque<int[]> queue = new ArrayDeque<>();
        int components = 0;
        for (int row = 0; row < rows; row++) for (int column = 0; column < columns; column++) {
            if (!open[row][column] || seen[row][column]) continue;
            components++;
            seen[row][column] = true;
            queue.add(new int[] {row, column});
            while (!queue.isEmpty()) {
                int[] cell = queue.remove();
                for (int direction = 0; direction < 4; direction++) {
                    int nextRow = cell[0] + dr[direction], nextColumn = cell[1] + dc[direction];
                    if (nextRow < 0 || nextRow >= rows || nextColumn < 0 || nextColumn >= columns
                            || !open[nextRow][nextColumn] || seen[nextRow][nextColumn]) continue;
                    seen[nextRow][nextColumn] = true;
                    queue.add(new int[] {nextRow, nextColumn});
                }
            }
        }
        return components;
    }
}
