package notebook;

import java.util.ArrayList;
import java.util.List;

/** Union by size without path compression, so earlier states can be restored. */
public final class RollbackDSU {
    private final int[] parent, size;
    private final List<int[]> history = new ArrayList<>();
    private int components;

    public RollbackDSU(int n) {
        if (n < 0) throw new IllegalArgumentException();
        parent = new int[n]; size = new int[n]; components = n;
        for (int i = 0; i < n; i++) { parent[i] = i; size[i] = 1; }
    }

    /** O(log n) worst case because union by size bounds tree height. */
    public int find(int vertex) {
        while (vertex != parent[vertex]) vertex = parent[vertex];
        return vertex;
    }

    /** Return whether two components merged. O(log n). */
    public boolean union(int a, int b) {
        a = find(a); b = find(b);
        if (a == b) return false;
        if (size[a] < size[b]) { int temp = a; a = b; b = temp; }
        history.add(new int[] {a, b, size[b]});
        parent[b] = a; size[a] += size[b]; components--;
        return true;
    }

    /** A token for a later rollback. O(1). */
    public int snapshot() { return history.size(); }

    /** Undo all successful unions after a snapshot. O(number of undone unions). */
    public void rollback(int snapshot) {
        if (snapshot < 0 || snapshot > history.size()) throw new IllegalArgumentException();
        while (history.size() > snapshot) {
            int[] change = history.remove(history.size() - 1);
            parent[change[1]] = change[1];
            size[change[0]] -= change[2];
            components++;
        }
    }

    public int components() { return components; }
    public int size(int vertex) { return size[find(vertex)]; }
}
