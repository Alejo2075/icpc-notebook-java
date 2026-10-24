package notebook;

/** Recursion and exhaustive search when the input is small enough. */
public final class BeginnerBacktracking {
    private BeginnerBacktracking() {}

    /** Factorial using a smaller subproblem. O(n) time and stack; n must be in [0,20]. */
    public static long factorial(int n) {
        if (n < 0 || n > 20) throw new IllegalArgumentException("n must be in [0,20]");
        if (n <= 1) return 1;
        return n * factorial(n - 1);
    }

    /** Count subsets whose sum equals target. O(2^n) time, O(n) stack; n <= 25. */
    public static long countSubsetsWithSum(int[] values, long target) {
        if (values.length > 25) throw new IllegalArgumentException("too many subsets");
        return count(values, 0, 0, target);
    }

    private static long count(int[] values, int index, long sum, long target) {
        if (index == values.length) return sum == target ? 1 : 0;
        return count(values, index + 1, sum, target)
                + count(values, index + 1, Math.addExact(sum, values[index]), target);
    }
}
