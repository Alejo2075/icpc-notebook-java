package notebook;

import java.util.List;

/** Boolean 2-SAT via strongly connected components. O(variables + clauses). */
public final class TwoSat {
    private final int variables;
    private final List<List<Integer>> implication;

    public TwoSat(int variables) {
        if (variables < 0) throw new IllegalArgumentException();
        this.variables = variables;
        implication = Graphs.newGraph(2 * variables);
    }

    private int literal(int variable, boolean value) {
        if (variable < 0 || variable >= variables) throw new IndexOutOfBoundsException();
        return 2 * variable + (value ? 1 : 0);
    }

    /** Add (a == aValue) OR (b == bValue). */
    public void addOr(int a, boolean aValue, int b, boolean bValue) {
        int x = literal(a, aValue), y = literal(b, bValue);
        implication.get(x ^ 1).add(y);
        implication.get(y ^ 1).add(x);
    }

    /** Return one satisfying assignment, or null if unsatisfiable. */
    public boolean[] solve() {
        int[] component = AdvancedGraphs.stronglyConnectedComponents(implication);
        boolean[] assignment = new boolean[variables];
        for (int i = 0; i < variables; i++) {
            if (component[2 * i] == component[2 * i + 1]) return null;
            assignment[i] = component[2 * i + 1] > component[2 * i];
        }
        return assignment;
    }
}
