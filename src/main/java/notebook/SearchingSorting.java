package notebook;

/** Search and sorting routines. Input to searches must be sorted ascending. */
public final class SearchingSorting {
    private SearchingSorting() {}

    /** O(log n); returns any matching index, or -1. */
    public static int binarySearch(int[] a, int key) {
        int lo = 0, hi = a.length - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (a[mid] == key) return mid;
            if (a[mid] < key) lo = mid + 1;
            else hi = mid - 1;
        }
        return -1;
    }

    /** O(log n); returns any matching index, or -1. */
    public static int ternarySearch(int[] a, int key) {
        int lo = 0, hi = a.length - 1;
        while (lo <= hi) {
            int m1 = lo + (hi - lo) / 3;
            int m2 = hi - (hi - lo) / 3;
            if (a[m1] == key) return m1;
            if (a[m2] == key) return m2;
            if (key < a[m1]) hi = m1 - 1;
            else if (key > a[m2]) lo = m2 + 1;
            else { lo = m1 + 1; hi = m2 - 1; }
        }
        return -1;
    }

    /** O(n + 65536) counting sort for Java UTF-16 char units. */
    public static void countingSort(char[] a) {
        int[] count = new int[Character.MAX_VALUE + 1];
        for (char c : a) count[c]++;
        int i = 0;
        for (int c = 0; c < count.length; c++) {
            while (count[c]-- > 0) a[i++] = (char) c;
        }
    }

    /** Lower bound: first index i with a[i] >= key. O(log n). */
    public static int lowerBound(int[] a, int key) {
        int lo = 0, hi = a.length;
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (a[mid] < key) lo = mid + 1;
            else hi = mid;
        }
        return lo;
    }
}
