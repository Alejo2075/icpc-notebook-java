package notebook;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Hopcroft-Karp matching and Konig minimum vertex cover in bipartite graphs. */
public final class BipartiteMatching {
    public record Cover(int[] left, int[] right) {}
    private final List<List<Integer>> edges;
    private final int[] leftMatch, rightMatch, distance;
    private int endDistance;

    public BipartiteMatching(int leftSize, int rightSize) {
        if (leftSize < 0 || rightSize < 0) throw new IllegalArgumentException();
        edges = new ArrayList<>();
        for (int i = 0; i < leftSize; i++) edges.add(new ArrayList<>());
        leftMatch = new int[leftSize]; rightMatch = new int[rightSize]; distance = new int[leftSize];
        Arrays.fill(leftMatch, -1); Arrays.fill(rightMatch, -1);
    }

    public void addEdge(int left, int right) {
        if (right < 0 || right >= rightMatch.length) throw new IndexOutOfBoundsException();
        edges.get(left).add(right);
    }

    /** Maximum matching size. O(E*sqrt(V)); DFS is recursive. */
    public int maximumMatching() {
        Arrays.fill(leftMatch, -1); Arrays.fill(rightMatch, -1);
        int size = 0;
        while (layers())
            for (int left = 0; left < leftMatch.length; left++)
                if (leftMatch[left] == -1 && augment(left)) size++;
        return size;
    }

    private boolean layers() {
        Arrays.fill(distance, -1);
        ArrayDeque<Integer> queue = new ArrayDeque<>();
        for (int left = 0; left < leftMatch.length; left++) if (leftMatch[left] == -1) {
            distance[left] = 0; queue.add(left);
        }
        endDistance = Integer.MAX_VALUE;
        while (!queue.isEmpty()) {
            int left = queue.remove();
            if (distance[left] >= endDistance) continue;
            for (int right : edges.get(left)) {
                int partner = rightMatch[right];
                if (partner == -1) endDistance = distance[left] + 1;
                else if (distance[partner] == -1) {
                    distance[partner] = distance[left] + 1;
                    queue.add(partner);
                }
            }
        }
        return endDistance != Integer.MAX_VALUE;
    }

    private boolean augment(int left) {
        for (int right : edges.get(left)) {
            int partner = rightMatch[right];
            if (partner == -1 && distance[left] + 1 == endDistance
                    || partner != -1 && distance[partner] == distance[left] + 1 && augment(partner)) {
                leftMatch[left] = right; rightMatch[right] = left; return true;
            }
        }
        distance[left] = -1;
        return false;
    }

    /** Minimum vertex cover; calls maximumMatching first. O(E*sqrt(V)). */
    public Cover minimumVertexCover() {
        maximumMatching();
        boolean[] seenLeft = new boolean[leftMatch.length], seenRight = new boolean[rightMatch.length];
        ArrayDeque<Integer> queue = new ArrayDeque<>();
        for (int left = 0; left < leftMatch.length; left++) if (leftMatch[left] == -1) {
            seenLeft[left] = true; queue.add(left);
        }
        while (!queue.isEmpty()) {
            int left = queue.remove();
            for (int right : edges.get(left)) {
                if (leftMatch[left] == right || seenRight[right]) continue;
                seenRight[right] = true;
                int partner = rightMatch[right];
                if (partner != -1 && !seenLeft[partner]) { seenLeft[partner] = true; queue.add(partner); }
            }
        }
        int leftCount = 0, rightCount = 0;
        for (boolean seen : seenLeft) if (!seen) leftCount++;
        for (boolean seen : seenRight) if (seen) rightCount++;
        int[] coverLeft = new int[leftCount], coverRight = new int[rightCount];
        leftCount = rightCount = 0;
        for (int i = 0; i < seenLeft.length; i++) if (!seenLeft[i]) coverLeft[leftCount++] = i;
        for (int i = 0; i < seenRight.length; i++) if (seenRight[i]) coverRight[rightCount++] = i;
        return new Cover(coverLeft, coverRight);
    }

    public int[] leftMatches() { return leftMatch.clone(); }
}
