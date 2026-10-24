package notebook;

import java.math.BigInteger;

/** Exact number of spanning trees in an undirected multigraph. */
public final class MatrixTreeTheorem {
    private MatrixTreeTheorem() {}

    /** Kirchhoff cofactor with a fraction-free Bareiss determinant. O(n^3) BigInteger work. */
    public static BigInteger count(int vertices, int[][] edges) {
        if (vertices < 0) throw new IllegalArgumentException();
        if (vertices == 0) return BigInteger.ZERO;
        if (vertices == 1) return BigInteger.ONE;
        BigInteger[][] laplacian = new BigInteger[vertices][vertices];
        for (int row = 0; row < vertices; row++)
            for (int column = 0; column < vertices; column++) laplacian[row][column] = BigInteger.ZERO;
        for (int[] edge : edges) {
            if (edge.length != 2 || edge[0] < 0 || edge[0] >= vertices || edge[1] < 0 || edge[1] >= vertices)
                throw new IllegalArgumentException("invalid edge");
            int a = edge[0], b = edge[1];
            if (a == b) continue;
            laplacian[a][a] = laplacian[a][a].add(BigInteger.ONE);
            laplacian[b][b] = laplacian[b][b].add(BigInteger.ONE);
            laplacian[a][b] = laplacian[a][b].subtract(BigInteger.ONE);
            laplacian[b][a] = laplacian[b][a].subtract(BigInteger.ONE);
        }
        int n = vertices - 1;
        BigInteger[][] matrix = new BigInteger[n][n];
        for (int row = 0; row < n; row++)
            System.arraycopy(laplacian[row], 0, matrix[row], 0, n);
        BigInteger previous = BigInteger.ONE;
        int sign = 1;
        for (int pivot = 0; pivot < n - 1; pivot++) {
            if (matrix[pivot][pivot].signum() == 0) {
                int replacement = pivot + 1;
                while (replacement < n && matrix[replacement][pivot].signum() == 0) replacement++;
                if (replacement == n) return BigInteger.ZERO;
                BigInteger[] swap = matrix[pivot]; matrix[pivot] = matrix[replacement]; matrix[replacement] = swap;
                sign = -sign;
            }
            BigInteger diagonal = matrix[pivot][pivot];
            for (int row = pivot + 1; row < n; row++)
                for (int column = pivot + 1; column < n; column++)
                    matrix[row][column] = matrix[row][column].multiply(diagonal)
                            .subtract(matrix[row][pivot].multiply(matrix[pivot][column])).divide(previous);
            for (int row = pivot + 1; row < n; row++) matrix[row][pivot] = BigInteger.ZERO;
            previous = diagonal;
        }
        BigInteger determinant = matrix[n - 1][n - 1];
        return sign < 0 ? determinant.negate() : determinant;
    }
}
