package notebook;

/** Point additions and rectangle sums on a fixed zero-indexed grid. */
public final class Fenwick2D {
    private final int rows, columns;
    private final long[][] bit;

    public Fenwick2D(int rows, int columns) {
        if (rows < 0 || columns < 0) throw new IllegalArgumentException();
        this.rows = rows; this.columns = columns;
        bit = new long[rows + 1][columns + 1];
    }

    /** Add delta to one cell. O(log rows * log columns). */
    public void add(int row, int column, long delta) {
        if (row < 0 || row >= rows || column < 0 || column >= columns) throw new IndexOutOfBoundsException();
        for (int r = row + 1; r <= rows; r += r & -r)
            for (int c = column + 1; c <= columns; c += c & -c)
                bit[r][c] = Math.addExact(bit[r][c], delta);
    }

    /** Sum [0,row) x [0,column). O(log rows * log columns). */
    public long prefix(int row, int column) {
        if (row < 0 || row > rows || column < 0 || column > columns) throw new IndexOutOfBoundsException();
        long result = 0;
        for (int r = row; r > 0; r -= r & -r)
            for (int c = column; c > 0; c -= c & -c)
                result = Math.addExact(result, bit[r][c]);
        return result;
    }

    /** Sum [top,bottom) x [left,right). O(log rows * log columns). */
    public long rectangle(int top, int left, int bottom, int right) {
        if (top > bottom || left > right) throw new IndexOutOfBoundsException();
        return Math.subtractExact(Math.subtractExact(prefix(bottom, right), prefix(top, right)),
                Math.subtractExact(prefix(bottom, left), prefix(top, left)));
    }
}
