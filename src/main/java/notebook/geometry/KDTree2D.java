package notebook;

import java.util.Arrays;
import java.util.Comparator;

/** Static two-dimensional nearest-neighbor index using a bounding-box KD tree. */
public final class KDTree2D {
    public record Point(double x, double y) {}
    public record Neighbor(int index, double squaredDistance) {}
    private record Item(double x, double y, int index) {}
    private static final class Node {
        Item item;
        Node left, right;
        double minX, maxX, minY, maxY;
        Node(Item item) { this.item = item; minX=maxX=item.x; minY=maxY=item.y; }
    }
    private final Node root;

    /** Build in O(n log^2 n) time. Points are copied and must have finite coordinates. */
    public KDTree2D(Point[] points) {
        Item[] items = new Item[points.length];
        for (int i = 0; i < points.length; i++) {
            Point point = points[i];
            if (!Double.isFinite(point.x) || !Double.isFinite(point.y))
                throw new IllegalArgumentException("coordinates must be finite");
            items[i] = new Item(point.x, point.y, i);
        }
        root = build(items, 0, items.length, 0);
    }

    private static Node build(Item[] points, int left, int right, int depth) {
        if (left >= right) return null;
        Comparator<Item> order = depth % 2 == 0
                ? Comparator.comparingDouble(Item::x).thenComparingDouble(Item::y).thenComparingInt(Item::index)
                : Comparator.comparingDouble(Item::y).thenComparingDouble(Item::x).thenComparingInt(Item::index);
        Arrays.sort(points, left, right, order);
        int middle = (left + right) >>> 1;
        Node node = new Node(points[middle]);
        node.left = build(points, left, middle, depth + 1);
        node.right = build(points, middle + 1, right, depth + 1);
        include(node, node.left); include(node, node.right);
        return node;
    }

    private static void include(Node node, Node child) {
        if (child == null) return;
        node.minX=Math.min(node.minX,child.minX); node.maxX=Math.max(node.maxX,child.maxX);
        node.minY=Math.min(node.minY,child.minY); node.maxY=Math.max(node.maxY,child.maxY);
    }

    /** Return original input index and squared distance. Average O(log n), worst O(n). */
    public Neighbor nearest(Point query) {
        if (root == null) throw new IllegalStateException("empty point set");
        if (!Double.isFinite(query.x) || !Double.isFinite(query.y))
            throw new IllegalArgumentException("query must be finite");
        Best best = new Best();
        search(root, query, best);
        return new Neighbor(best.index, best.distance);
    }

    private static final class Best { int index = -1; double distance = Double.POSITIVE_INFINITY; }

    private static void search(Node node, Point query, Best best) {
        if (node == null || boxDistance(node, query) > best.distance) return;
        double dx = node.item.x-query.x, dy = node.item.y-query.y, distance = dx*dx+dy*dy;
        if (distance < best.distance || distance == best.distance && node.item.index < best.index) {
            best.distance=distance; best.index=node.item.index;
        }
        double leftDistance = node.left == null ? Double.POSITIVE_INFINITY : boxDistance(node.left,query);
        double rightDistance = node.right == null ? Double.POSITIVE_INFINITY : boxDistance(node.right,query);
        if (leftDistance <= rightDistance) {
            search(node.left,query,best); search(node.right,query,best);
        } else {
            search(node.right,query,best); search(node.left,query,best);
        }
    }

    private static double boxDistance(Node node, Point query) {
        double dx = query.x < node.minX ? node.minX-query.x : query.x > node.maxX ? query.x-node.maxX : 0;
        double dy = query.y < node.minY ? node.minY-query.y : query.y > node.maxY ? query.y-node.maxY : 0;
        return dx*dx+dy*dy;
    }
}
