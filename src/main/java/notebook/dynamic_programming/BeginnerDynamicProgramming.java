package notebook;

import java.util.Arrays;

/** First DP examples: identify a state, base case, transition, and answer. */
public final class BeginnerDynamicProgramming {
    private BeginnerDynamicProgramming() {}

    /** Fibonacci F(n), with F(0)=0 and F(1)=1. O(n) time; n must be at most 92. */
    public static long fibonacci(int n) {
        if (n < 0 || n > 92) throw new IllegalArgumentException("n must be in [0,92]");
        long previous = 0, current = 1;
        for (int i = 0; i < n; i++) { long next = previous + current; previous = current; current = next; }
        return previous;
    }

    /** Minimum coins for an amount, or -1 if impossible. O(amount * coins.length) time. */
    public static int minimumCoins(int[] coins, int amount) {
        if (amount < 0) throw new IllegalArgumentException("negative amount");
        int[] dp = new int[amount + 1];
        Arrays.fill(dp, amount + 1);
        dp[0] = 0;
        for (int value = 1; value <= amount; value++)
            for (int coin : coins) {
                if (coin <= 0) throw new IllegalArgumentException("coins must be positive");
                if (coin <= value) dp[value] = Math.min(dp[value], dp[value - coin] + 1);
            }
        return dp[amount] > amount ? -1 : dp[amount];
    }

    /** Number of paths in a grid, moving right or down around blocked cells. O(rows*columns). */
    public static long gridPaths(boolean[][] blocked) {
        int rows = blocked.length;
        if (rows == 0) return 0;
        int columns = blocked[0].length;
        for (boolean[] row : blocked) if (row.length != columns) throw new IllegalArgumentException("ragged grid");
        if (columns == 0) return 0;
        long[] dp = new long[columns];
        dp[0] = blocked[0][0] ? 0 : 1;
        for (int row = 0; row < rows; row++)
            for (int column = 0; column < columns; column++) {
                if (blocked[row][column]) dp[column] = 0;
                else if (column > 0) dp[column] = Math.addExact(dp[column], dp[column - 1]);
            }
        return dp[columns - 1];
    }
}
