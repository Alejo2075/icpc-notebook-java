package notebook;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/** Everyday Java collections: map, set, and stack. */
public final class BeginnerCollections {
    private BeginnerCollections() {}

    /** Count each integer with a hash map. Expected O(n) time and O(distinct) space. */
    public static Map<Integer, Integer> frequencies(int[] values) {
        Map<Integer, Integer> result = new HashMap<>();
        for (int value : values) result.merge(value, 1, Integer::sum);
        return result;
    }

    /** Count distinct integers with a hash set. Expected O(n) time and space. */
    public static int distinctCount(int[] values) {
        Set<Integer> seen = new HashSet<>();
        for (int value : values) seen.add(value);
        return seen.size();
    }

    /** Check balanced (), [], and {} brackets. O(n) time, O(n) stack space. */
    public static boolean balancedBrackets(String text) {
        ArrayDeque<Character> stack = new ArrayDeque<>();
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (ch == '(' || ch == '[' || ch == '{') stack.push(ch);
            else if (ch == ')' || ch == ']' || ch == '}') {
                if (stack.isEmpty()) return false;
                char open = stack.pop();
                if (ch == ')' && open != '(' || ch == ']' && open != '[' || ch == '}' && open != '{')
                    return false;
            } else throw new IllegalArgumentException("expected brackets only");
        }
        return stack.isEmpty();
    }
}
