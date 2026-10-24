package notebook;

import java.util.Arrays;

/** Suffix array and LCP over UTF-16 code units. */
public final class SuffixAlgorithms {
    private SuffixAlgorithms() {}

    /** Suffix starts in lexicographic order. O(n log^2 n) with comparison sorting. */
    public static int[] suffixArray(String s) {
        int n = s.length();
        Integer[] order = new Integer[n];
        int[] rank = new int[n], nextRank = new int[n];
        for (int i = 0; i < n; i++) { order[i] = i; rank[i] = s.charAt(i); }
        for (int width = 1; width < n; width <<= 1) {
            final int[] current = rank;
            final int step = width;
            Arrays.sort(order, (a, b) -> {
                if (current[a] != current[b]) return Integer.compare(current[a], current[b]);
                return Integer.compare(a + step < n ? current[a + step] : -1,
                                       b + step < n ? current[b + step] : -1);
            });
            nextRank[order[0]] = 0;
            for (int i = 1; i < n; i++) {
                int a = order[i - 1], b = order[i];
                boolean different = current[a] != current[b]
                    || (a + step < n ? current[a + step] : -1)
                    != (b + step < n ? current[b + step] : -1);
                nextRank[b] = nextRank[a] + (different ? 1 : 0);
            }
            int[] swap = rank; rank = nextRank; nextRank = swap;
            if (rank[order[n - 1]] == n - 1) break;
            if (width > n / 2) break;
        }
        int[] result = new int[n];
        for (int i = 0; i < n; i++) result[i] = order[i];
        return result;
    }

    /** lcp[i] = common prefix length of suffixes sa[i] and sa[i-1]; lcp[0]=0. O(n). */
    public static int[] lcp(String s, int[] sa) {
        int n = s.length();
        if (sa.length != n) throw new IllegalArgumentException();
        int[] rank = new int[n], lcp = new int[n];
        for (int i = 0; i < n; i++) rank[sa[i]] = i;
        for (int i = 0, common = 0; i < n; i++) {
            if (rank[i] == 0) { common = 0; continue; }
            int j = sa[rank[i] - 1];
            while (i + common < n && j + common < n
                    && s.charAt(i + common) == s.charAt(j + common)) common++;
            lcp[rank[i]] = common;
            if (common > 0) common--;
        }
        return lcp;
    }

    /** Number of distinct nonempty substrings. O(n log^2 n). */
    public static long distinctSubstrings(String s) {
        int[] sa = suffixArray(s), lcp = lcp(s, sa);
        long count = 0;
        for (int i = 0; i < sa.length; i++) count += s.length() - sa[i] - lcp[i];
        return count;
    }
}
