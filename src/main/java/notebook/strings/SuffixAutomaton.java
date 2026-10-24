package notebook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Suffix automaton over Java UTF-16 code units. Expected O(n) build time. */
public final class SuffixAutomaton {
    private static final class State {
        int length, link = -1;
        Map<Character, Integer> next = new HashMap<>();
    }
    private final List<State> states = new ArrayList<>();
    private int last;

    public SuffixAutomaton(String text) {
        states.add(new State());
        for (int i = 0; i < text.length(); i++) extend(text.charAt(i));
    }

    private void extend(char symbol) {
        int current = states.size();
        State state = new State(); state.length = states.get(last).length + 1;
        states.add(state);
        int previous = last;
        while (previous != -1 && !states.get(previous).next.containsKey(symbol)) {
            states.get(previous).next.put(symbol, current);
            previous = states.get(previous).link;
        }
        if (previous == -1) state.link = 0;
        else {
            int target = states.get(previous).next.get(symbol);
            if (states.get(previous).length + 1 == states.get(target).length) state.link = target;
            else {
                int cloneIndex = states.size();
                State clone = new State();
                clone.length = states.get(previous).length + 1;
                clone.link = states.get(target).link;
                clone.next.putAll(states.get(target).next);
                states.add(clone);
                while (previous != -1 && Integer.valueOf(target).equals(states.get(previous).next.get(symbol))) {
                    states.get(previous).next.put(symbol, cloneIndex);
                    previous = states.get(previous).link;
                }
                states.get(target).link = cloneIndex;
                state.link = cloneIndex;
            }
        }
        last = current;
    }

    /** Whether pattern occurs as a substring. Expected O(pattern length). */
    public boolean contains(String pattern) {
        int state = 0;
        for (int i = 0; i < pattern.length(); i++) {
            Integer next = states.get(state).next.get(pattern.charAt(i));
            if (next == null) return false;
            state = next;
        }
        return true;
    }

    /** Number of distinct nonempty substrings. O(number of states). */
    public long distinctSubstringCount() {
        long count = 0;
        for (int i = 1; i < states.size(); i++)
            count += states.get(i).length - states.get(states.get(i).link).length;
        return count;
    }

    /** Length of the longest substring shared with other. Expected O(other length). */
    public int longestCommonSubstringLength(String other) {
        int state = 0, length = 0, best = 0;
        for (int i = 0; i < other.length(); i++) {
            char symbol = other.charAt(i);
            while (state != 0 && !states.get(state).next.containsKey(symbol)) {
                state = states.get(state).link;
                length = states.get(state).length;
            }
            Integer next = states.get(state).next.get(symbol);
            if (next != null) { state = next; length++; }
            else { state = 0; length = 0; }
            best = Math.max(best, length);
        }
        return best;
    }
}
