package notebook;

import java.util.HashMap;
import java.util.Map;

/** Trie and double rolling hash over Java UTF-16 code units. */
public final class AdvancedStrings {
    private AdvancedStrings() {}

    public static final class Trie {
        private static final class Node {
            Map<Character, Node> children = new HashMap<>();
            int terminalCount;
        }
        private final Node root = new Node();

        /** Insert a word, including the empty word. O(length). */
        public void add(String word) {
            Node node = root;
            for (int i = 0; i < word.length(); i++)
                node = node.children.computeIfAbsent(word.charAt(i), key -> new Node());
            node.terminalCount++;
        }

        public int count(String word) {
            Node node = root;
            for (int i = 0; i < word.length(); i++) {
                node = node.children.get(word.charAt(i));
                if (node == null) return 0;
            }
            return node.terminalCount;
        }

        public boolean hasPrefix(String prefix) {
            Node node = root;
            for (int i = 0; i < prefix.length(); i++) {
                node = node.children.get(prefix.charAt(i));
                if (node == null) return false;
            }
            return true;
        }
    }

    /** O(n) build, O(1) half-open substring hash. Hash equality is probabilistic. */
    public static final class RollingHash {
        private static final long MOD1 = 1_000_000_007L, MOD2 = 1_000_000_009L;
        private static final long BASE = 911_382_323L;
        private final long[] h1, h2, p1, p2;
        public RollingHash(String s) {
            int n = s.length();
            h1 = new long[n + 1]; h2 = new long[n + 1];
            p1 = new long[n + 1]; p2 = new long[n + 1];
            p1[0] = p2[0] = 1;
            for (int i = 0; i < n; i++) {
                long c = s.charAt(i) + 1L;
                h1[i + 1] = (h1[i] * BASE + c) % MOD1;
                h2[i + 1] = (h2[i] * BASE + c) % MOD2;
                p1[i + 1] = p1[i] * BASE % MOD1;
                p2[i + 1] = p2[i] * BASE % MOD2;
            }
        }
        public long hash(int left, int rightExclusive) {
            if (left < 0 || left > rightExclusive || rightExclusive >= h1.length)
                throw new IndexOutOfBoundsException();
            int len = rightExclusive - left;
            long a = (h1[rightExclusive] - h1[left] * p1[len] % MOD1 + MOD1) % MOD1;
            long b = (h2[rightExclusive] - h2[left] * p2[len] % MOD2 + MOD2) % MOD2;
            return (a << 32) | b;
        }
    }
}
