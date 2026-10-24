package notebook;

import java.util.Arrays;

/** Minimum-cost assignment of every row to a distinct column. */
public final class HungarianAlgorithm {
    private HungarianAlgorithm() {}
    public record Assignment(long cost, int[] columnForRow) {}

    /** Rectangular dense assignment, rows <= columns. O(rows^2 * columns). */
    public static Assignment minimize(long[][] costs) {
        int rows = costs.length;
        if (rows == 0) return new Assignment(0, new int[0]);
        int columns = costs[0].length;
        if (columns < rows) throw new IllegalArgumentException("rows must not exceed columns");
        for (long[] row : costs) if (row.length != columns) throw new IllegalArgumentException("ragged cost matrix");
        long[] rowPotential = new long[rows + 1], columnPotential = new long[columns + 1];
        int[] matchedRow = new int[columns + 1], previousColumn = new int[columns + 1];
        for (int row = 1; row <= rows; row++) {
            matchedRow[0] = row;
            long[] minimum = new long[columns + 1];
            Arrays.fill(minimum, Long.MAX_VALUE);
            boolean[] used = new boolean[columns + 1];
            int currentColumn = 0;
            do {
                used[currentColumn] = true;
                int currentRow = matchedRow[currentColumn], nextColumn = -1;
                long delta = Long.MAX_VALUE;
                for (int column = 1; column <= columns; column++) if (!used[column]) {
                    long reduced = Math.subtractExact(Math.subtractExact(costs[currentRow - 1][column - 1],
                            rowPotential[currentRow]), columnPotential[column]);
                    if (reduced < minimum[column]) {
                        minimum[column] = reduced;
                        previousColumn[column] = currentColumn;
                    }
                    if (minimum[column] < delta) { delta = minimum[column]; nextColumn = column; }
                }
                if (nextColumn < 0 || delta == Long.MAX_VALUE) throw new IllegalArgumentException("no assignment");
                for (int column = 0; column <= columns; column++) {
                    if (used[column]) {
                        rowPotential[matchedRow[column]] = Math.addExact(rowPotential[matchedRow[column]], delta);
                        columnPotential[column] = Math.subtractExact(columnPotential[column], delta);
                    } else if (minimum[column] != Long.MAX_VALUE)
                        minimum[column] = Math.subtractExact(minimum[column], delta);
                }
                currentColumn = nextColumn;
            } while (matchedRow[currentColumn] != 0);
            do {
                int previous = previousColumn[currentColumn];
                matchedRow[currentColumn] = matchedRow[previous];
                currentColumn = previous;
            } while (currentColumn != 0);
        }
        int[] assignment = new int[rows];
        for (int column = 1; column <= columns; column++)
            if (matchedRow[column] != 0) assignment[matchedRow[column] - 1] = column - 1;
        long total = 0;
        for (int row = 0; row < rows; row++) total = Math.addExact(total, costs[row][assignment[row]]);
        return new Assignment(total, assignment);
    }
}
