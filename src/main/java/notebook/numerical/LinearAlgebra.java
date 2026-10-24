package notebook;

/** Gaussian elimination for small dense real-valued systems. */
public final class LinearAlgebra {
    private LinearAlgebra() {}
    private static final double EPS = 1e-10;
    public record Solution(boolean consistent, int rank, double[] values) {}

    /** Solve A*x=b; free variables are set to zero. O(rows*columns*min(rows,columns)). */
    public static Solution solve(double[][] coefficients, double[] rightSide) {
        int rows = coefficients.length;
        if (rightSide.length != rows) throw new IllegalArgumentException("dimension mismatch");
        int columns = rows == 0 ? 0 : coefficients[0].length;
        double[][] matrix = new double[rows][columns + 1];
        for (int row = 0; row < rows; row++) {
            if (coefficients[row].length != columns) throw new IllegalArgumentException("ragged matrix");
            System.arraycopy(coefficients[row], 0, matrix[row], 0, columns);
            matrix[row][columns] = rightSide[row];
        }
        int[] pivotColumn = new int[Math.min(rows, columns)];
        int rank = 0;
        for (int column = 0; column < columns && rank < rows; column++) {
            int pivot = rank;
            for (int row = rank + 1; row < rows; row++)
                if (Math.abs(matrix[row][column]) > Math.abs(matrix[pivot][column])) pivot = row;
            if (Math.abs(matrix[pivot][column]) < EPS) continue;
            double[] swap = matrix[rank]; matrix[rank] = matrix[pivot]; matrix[pivot] = swap;
            double divisor = matrix[rank][column];
            for (int c = column; c <= columns; c++) matrix[rank][c] /= divisor;
            for (int row = 0; row < rows; row++) if (row != rank) {
                double factor = matrix[row][column];
                for (int c = column; c <= columns; c++) matrix[row][c] -= factor * matrix[rank][c];
            }
            pivotColumn[rank] = column;
            rank++;
        }
        for (int row = rank; row < rows; row++) if (Math.abs(matrix[row][columns]) > EPS)
            return new Solution(false, rank, new double[0]);
        double[] answer = new double[columns];
        for (int row = 0; row < rank; row++) answer[pivotColumn[row]] = matrix[row][columns];
        return new Solution(true, rank, answer);
    }

    /** Determinant of a square matrix by elimination. O(n^3). */
    public static double determinant(double[][] input) {
        int n = input.length;
        double[][] matrix = new double[n][n];
        for (int row = 0; row < n; row++) {
            if (input[row].length != n) throw new IllegalArgumentException("matrix must be square");
            matrix[row] = input[row].clone();
        }
        double determinant = 1;
        for (int column = 0; column < n; column++) {
            int pivot = column;
            for (int row = column + 1; row < n; row++)
                if (Math.abs(matrix[row][column]) > Math.abs(matrix[pivot][column])) pivot = row;
            if (Math.abs(matrix[pivot][column]) < EPS) return 0;
            if (pivot != column) { double[] swap = matrix[column]; matrix[column] = matrix[pivot]; matrix[pivot] = swap; determinant = -determinant; }
            double value = matrix[column][column];
            determinant *= value;
            for (int row = column + 1; row < n; row++) {
                double factor = matrix[row][column] / value;
                for (int c = column + 1; c < n; c++) matrix[row][c] -= factor * matrix[column][c];
            }
        }
        return determinant;
    }
}
