package notebook;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** String algorithms operate on Java UTF-16 code units. */
public final class StringsAlgorithms {
    private StringsAlgorithms() {}

    /** KMP match starts, including all n+1 positions for an empty pattern. O(n+m). */
    public static List<Integer> kmp(String text, String pattern) {
        List<Integer> matches = new ArrayList<>();
        if (pattern.isEmpty()) {
            for (int i = 0; i <= text.length(); i++) matches.add(i);
            return matches;
        }
        int[] pi = prefixFunction(pattern);
        for (int i = 0, j = 0; i < text.length(); i++) {
            while (j > 0 && text.charAt(i) != pattern.charAt(j)) j = pi[j - 1];
            if (text.charAt(i) == pattern.charAt(j)) j++;
            if (j == pattern.length()) { matches.add(i - j + 1); j = pi[j - 1]; }
        }
        return matches;
    }

    /** Prefix function. O(n). */
    public static int[] prefixFunction(String s) {
        int[] pi = new int[s.length()];
        for (int i = 1; i < s.length(); i++) {
            int j = pi[i - 1];
            while (j > 0 && s.charAt(i) != s.charAt(j)) j = pi[j - 1];
            if (s.charAt(i) == s.charAt(j)) j++;
            pi[i] = j;
        }
        return pi;
    }

    /** Longest palindromic substring by Manacher, O(n). */
    public static String manacher(String s) {
        if (s.isEmpty()) return "";
        int n = s.length(), bestStart = 0, bestLen = 1;
        int[] odd = new int[n], even = new int[n];
        for (int i = 0, l = 0, r = -1; i < n; i++) {
            int k = i > r ? 1 : Math.min(odd[l + r - i], r - i + 1);
            while (i - k >= 0 && i + k < n && s.charAt(i - k) == s.charAt(i + k)) k++;
            odd[i] = k--;
            int len = 2 * odd[i] - 1;
            if (len > bestLen) { bestLen = len; bestStart = i - odd[i] + 1; }
            if (i + k > r) { l = i - k; r = i + k; }
        }
        for (int i = 0, l = 0, r = -1; i < n; i++) {
            int k = i > r ? 0 : Math.min(even[l + r - i + 1], r - i + 1);
            while (i - k - 1 >= 0 && i + k < n && s.charAt(i - k - 1) == s.charAt(i + k)) k++;
            even[i] = k--;
            int len = 2 * even[i];
            if (len > bestLen) { bestLen = len; bestStart = i - even[i]; }
            if (i + k > r) { l = i - k - 1; r = i + k; }
        }
        return s.substring(bestStart, bestStart + bestLen);
    }

    /** Boyer-Moore bad-character search. O(nm) worst case; UTF-16 aware. */
    public static List<Integer> boyerMoore(String text, String pattern) {
        List<Integer> matches = new ArrayList<>();
        int n = text.length(), m = pattern.length();
        if (m == 0) {
            for (int i = 0; i <= n; i++) matches.add(i);
            return matches;
        }
        int[] last = new int[Character.MAX_VALUE + 1];
        Arrays.fill(last, -1);
        for (int i = 0; i < m; i++) last[pattern.charAt(i)] = i;
        for (int shift = 0; shift <= n - m;) {
            int j = m - 1;
            while (j >= 0 && pattern.charAt(j) == text.charAt(shift + j)) j--;
            if (j < 0) {
                matches.add(shift);
                shift += shift + m < n ? m - last[text.charAt(shift + m)] : 1;
            } else shift += Math.max(1, j - last[text.charAt(shift + j)]);
        }
        return matches;
    }

    /** Z-function: z[i] is longest common prefix of s and s.substring(i). O(n). */
    public static int[] zFunction(String s) {
        int[] z = new int[s.length()];
        for (int i = 1, l = 0, r = 0; i < s.length(); i++) {
            if (i <= r) z[i] = Math.min(r - i + 1, z[i - l]);
            while (i + z[i] < s.length() && s.charAt(z[i]) == s.charAt(i + z[i])) z[i]++;
            if (i + z[i] - 1 > r) { l = i; r = i + z[i] - 1; }
        }
        return z;
    }
}
