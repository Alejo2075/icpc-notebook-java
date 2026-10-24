package notebook;

/** Lexicographically minimum cyclic rotation by Booth's linear-time algorithm. */
public final class StringExtensions {
    private StringExtensions() {}

    /** Index of the smallest rotation; zero for empty text. O(n) time, O(1) extra space. */
    public static int minimumRotationIndex(String text) {
        int n = text.length();
        if (n == 0) return 0;
        int first = 0, second = 1, offset = 0;
        while (first < n && second < n && offset < n) {
            char a = text.charAt((first + offset) % n);
            char b = text.charAt((second + offset) % n);
            if (a == b) { offset++; continue; }
            if (a > b) first += offset + 1;
            else second += offset + 1;
            if (first == second) second++;
            offset = 0;
        }
        return Math.min(first, second);
    }

    /** Lexicographically smallest cyclic rotation. O(n). */
    public static String minimumRotation(String text) {
        int index = minimumRotationIndex(text);
        return text.substring(index) + text.substring(0, index);
    }
}
