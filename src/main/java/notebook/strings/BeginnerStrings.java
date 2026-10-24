package notebook;

/** Character and substring patterns for introductory contest problems. */
public final class BeginnerStrings {
    private BeginnerStrings() {}

    /** Compare characters from both ends. O(n) time, O(1) extra space. */
    public static boolean isPalindrome(String text) {
        for (int left = 0, right = text.length() - 1; left < right; left++, right--)
            if (text.charAt(left) != text.charAt(right)) return false;
        return true;
    }

    /** Count lowercase English letters. O(n) time, O(1) space; rejects other characters. */
    public static int[] lowercaseFrequencies(String text) {
        int[] frequency = new int[26];
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (ch < 'a' || ch > 'z') throw new IllegalArgumentException("expected a-z");
            frequency[ch - 'a']++;
        }
        return frequency;
    }

    /** Count exact occurrences, including overlaps. O(n*m) time. */
    public static int countOccurrences(String text, String pattern) {
        if (pattern.isEmpty()) throw new IllegalArgumentException("pattern must not be empty");
        int count = 0;
        for (int start = 0; start + pattern.length() <= text.length(); start++) {
            int matched = 0;
            while (matched < pattern.length() && text.charAt(start + matched) == pattern.charAt(matched))
                matched++;
            if (matched == pattern.length()) count++;
        }
        return count;
    }

    /** Longest shared prefix of two strings. O(min(n,m)) time. */
    public static String commonPrefix(String first, String second) {
        int length = 0;
        while (length < first.length() && length < second.length()
                && first.charAt(length) == second.charAt(length)) length++;
        return first.substring(0, length);
    }
}
