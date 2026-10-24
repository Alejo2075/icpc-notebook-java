package notebook;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Multi-pattern search for lowercase ASCII strings. O(total pattern length + text length * alphabet). */
public final class AhoCorasick {
    private static final class Node {
        int[] next = new int[26];
        int fail, outputs;
        Node() { Arrays.fill(next, -1); }
    }
    private final List<Node> nodes = new ArrayList<>();
    private boolean built;

    public AhoCorasick() { nodes.add(new Node()); }

    /** Add a nonempty lowercase pattern before build(). */
    public void add(String pattern) {
        if (built || pattern.isEmpty()) throw new IllegalArgumentException();
        int state = 0;
        for (int i = 0; i < pattern.length(); i++) {
            int c = index(pattern.charAt(i));
            if (nodes.get(state).next[c] == -1) {
                nodes.get(state).next[c] = nodes.size();
                nodes.add(new Node());
            }
            state = nodes.get(state).next[c];
        }
        nodes.get(state).outputs++;
    }

    public void build() {
        if (built) return;
        ArrayDeque<Integer> queue = new ArrayDeque<>();
        for (int c = 0; c < 26; c++) {
            int to = nodes.get(0).next[c];
            if (to == -1) nodes.get(0).next[c] = 0;
            else queue.add(to);
        }
        while (!queue.isEmpty()) {
            int state = queue.remove();
            Node node = nodes.get(state);
            node.outputs += nodes.get(node.fail).outputs;
            for (int c = 0; c < 26; c++) {
                int to = node.next[c];
                if (to == -1) node.next[c] = nodes.get(node.fail).next[c];
                else { nodes.get(to).fail = nodes.get(node.fail).next[c]; queue.add(to); }
            }
        }
        built = true;
    }

    /** Count all occurrences, including overlaps and duplicate patterns. O(text length). */
    public long countMatches(String text) {
        if (!built) build();
        int state = 0;
        long count = 0;
        for (int i = 0; i < text.length(); i++) {
            state = nodes.get(state).next[index(text.charAt(i))];
            count += nodes.get(state).outputs;
        }
        return count;
    }

    private static int index(char c) {
        if (c < 'a' || c > 'z') throw new IllegalArgumentException("lowercase a-z only");
        return c - 'a';
    }
}
