package notebook;

import java.util.HashSet;
import java.util.Set;

public final class GameTheory {
    private GameTheory() {}

    /** Normal-play Nim: first player wins iff pile xor is nonzero. */
    public static boolean firstWinsNim(int[] piles) {
        int xor = 0;
        for (int pile : piles) {
            if (pile < 0) throw new IllegalArgumentException();
            xor ^= pile;
        }
        return xor != 0;
    }

    /** Minimum excluded nonnegative integer. */
    public static int mex(Set<Integer> values) {
        int result = 0;
        while (values.contains(result)) result++;
        return result;
    }

    /** Grundy values for a DAG whose edges only lead to lower-numbered states. */
    public static int[] grundy(int[][] moves) {
        int[] result = new int[moves.length];
        for (int i = 0; i < moves.length; i++) {
            Set<Integer> reachable = new HashSet<>();
            for (int to : moves[i]) {
                if (to < 0 || to >= i) throw new IllegalArgumentException("moves must lead to lower states");
                reachable.add(result[to]);
            }
            result[i] = mex(reachable);
        }
        return result;
    }

    /** Minimax of a complete binary game tree; leaves length must be a power of two. */
    public static int minimax(int[] leaves) {
        if (leaves.length == 0 || (leaves.length & (leaves.length - 1)) != 0)
            throw new IllegalArgumentException("leaf count must be a power of two");
        int[] level = leaves.clone();
        boolean maximize = (Integer.numberOfTrailingZeros(leaves.length) & 1) == 1;
        while (level.length > 1) {
            int[] next = new int[level.length / 2];
            for (int i = 0; i < next.length; i++)
                next[i] = maximize ? Math.max(level[2 * i], level[2 * i + 1])
                    : Math.min(level[2 * i], level[2 * i + 1]);
            level = next;
            maximize = !maximize;
        }
        return level[0];
    }
}
