package notebook;

/** Stoer-Wagner global minimum cut in a nonnegative undirected weighted graph. */
public final class GlobalMinCut {
    private GlobalMinCut() {}

    /** Symmetric adjacency matrix, zero diagonal. O(n^3) time, O(n^2) space. */
    public static long weight(long[][] weights) {
        int n = weights.length;
        if (n < 2) throw new IllegalArgumentException("at least two vertices required");
        long[][] graph = new long[n][n];
        for (long[] row : weights) if (row.length != n) throw new IllegalArgumentException("invalid matrix");
        for (int i = 0; i < n; i++) {
            if (weights[i][i] != 0) throw new IllegalArgumentException("invalid matrix");
            for (int j = 0; j < n; j++) {
                if (weights[i][j] < 0 || weights[i][j] != weights[j][i])
                    throw new IllegalArgumentException("matrix must be symmetric and nonnegative");
            }
            graph[i] = weights[i].clone();
        }
        int[] vertices = new int[n];
        for (int i = 0; i < n; i++) vertices[i] = i;
        long best = Long.MAX_VALUE;
        for (int remaining = n; remaining > 1; remaining--) {
            long[] connection = new long[n];
            boolean[] used = new boolean[n];
            int previous = -1;
            for (int step = 0; step < remaining; step++) {
                int selected = -1;
                for (int i = 0; i < remaining; i++) {
                    int vertex = vertices[i];
                    if (!used[vertex] && (selected == -1 || connection[vertex] > connection[selected]))
                        selected = vertex;
                }
                used[selected] = true;
                if (step == remaining - 1) {
                    best = Math.min(best, connection[selected]);
                    for (int i = 0; i < remaining; i++) {
                        int vertex = vertices[i];
                        if (vertex == selected || vertex == previous) continue;
                        graph[previous][vertex] = Math.addExact(graph[previous][vertex], graph[selected][vertex]);
                        graph[vertex][previous] = graph[previous][vertex];
                    }
                    for (int i = 0; i < remaining; i++) if (vertices[i] == selected) {
                        vertices[i] = vertices[remaining - 1]; break;
                    }
                } else {
                    previous = selected;
                    for (int i = 0; i < remaining; i++) {
                        int vertex = vertices[i];
                        if (!used[vertex]) connection[vertex] = Math.addExact(connection[vertex], graph[selected][vertex]);
                    }
                }
            }
        }
        return best;
    }
}
