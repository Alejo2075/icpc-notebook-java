package notebook;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/** Planar geometry with double coordinates; use exact arithmetic for integer-critical predicates. */
public final class Geometry {
    private Geometry() {}

    public record Point(double x, double y) {}

    public static double distance(Point a, Point b) {
        return Math.hypot(a.x - b.x, a.y - b.y);
    }

    /** Shoelace area; input vertices are not repeated at the end. O(n). */
    public static double polygonArea(List<Point> p) {
        double twice = 0;
        for (int i = 0; i < p.size(); i++) {
            Point a = p.get(i), b = p.get((i + 1) % p.size());
            twice += a.x * b.y - a.y * b.x;
        }
        return Math.abs(twice) / 2;
    }

    /** Closed polygon perimeter; O(n). */
    public static double polygonPerimeter(List<Point> p) {
        double result = 0;
        for (int i = 0; i < p.size(); i++) result += distance(p.get(i), p.get((i + 1) % p.size()));
        return result;
    }

    private static double cross(Point a, Point b, Point c) {
        return (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x);
    }

    /** Monotone-chain hull in counterclockwise order; removes collinear interior points. O(n log n). */
    public static List<Point> convexHull(Point[] points) {
        Point[] sorted = points.clone();
        Arrays.sort(sorted, Comparator.comparingDouble(Point::x).thenComparingDouble(Point::y));
        List<Point> unique = new ArrayList<>();
        for (Point p : sorted) if (unique.isEmpty() || !unique.get(unique.size() - 1).equals(p)) unique.add(p);
        if (unique.size() <= 1) return unique;
        List<Point> lower = new ArrayList<>(), upper = new ArrayList<>();
        for (Point p : unique) {
            while (lower.size() >= 2 && cross(lower.get(lower.size() - 2), lower.get(lower.size() - 1), p) <= 0)
                lower.remove(lower.size() - 1);
            lower.add(p);
        }
        for (int i = unique.size() - 1; i >= 0; i--) {
            Point p = unique.get(i);
            while (upper.size() >= 2 && cross(upper.get(upper.size() - 2), upper.get(upper.size() - 1), p) <= 0)
                upper.remove(upper.size() - 1);
            upper.add(p);
        }
        lower.remove(lower.size() - 1);
        upper.remove(upper.size() - 1);
        lower.addAll(upper);
        return lower;
    }

    /** Closest pair distance. O(n log^2 n); requires at least two points. */
    public static double closestPair(Point[] points) {
        if (points.length < 2) throw new IllegalArgumentException("at least two points required");
        Point[] sorted = points.clone();
        Arrays.sort(sorted, Comparator.comparingDouble(Point::x).thenComparingDouble(Point::y));
        return closest(sorted, 0, sorted.length);
    }

    private static double closest(Point[] p, int lo, int hi) {
        if (hi - lo <= 3) {
            double best = Double.POSITIVE_INFINITY;
            for (int i = lo; i < hi; i++) for (int j = i + 1; j < hi; j++) best = Math.min(best, distance(p[i], p[j]));
            return best;
        }
        int mid = (lo + hi) >>> 1;
        double splitX = p[mid].x;
        double best = Math.min(closest(p, lo, mid), closest(p, mid, hi));
        List<Point> strip = new ArrayList<>();
        for (int i = lo; i < hi; i++) if (Math.abs(p[i].x - splitX) < best) strip.add(p[i]);
        strip.sort(Comparator.comparingDouble(Point::y));
        for (int i = 0; i < strip.size(); i++)
            for (int j = i + 1; j < strip.size() && strip.get(j).y - strip.get(i).y < best; j++)
                best = Math.min(best, distance(strip.get(i), strip.get(j)));
        return best;
    }

    /** Diameter via convex hull and rotating calipers. O(n log n). */
    public static double farthestPair(Point[] points) {
        List<Point> hull = convexHull(points);
        int n = hull.size();
        if (n < 2) return 0;
        if (n == 2) return distance(hull.get(0), hull.get(1));
        int j = 1;
        double best = 0;
        for (int i = 0; i < n; i++) {
            int next = (i + 1) % n;
            while (Math.abs(cross(hull.get(i), hull.get(next), hull.get((j + 1) % n)))
                    > Math.abs(cross(hull.get(i), hull.get(next), hull.get(j)))) j = (j + 1) % n;
            best = Math.max(best, Math.max(distance(hull.get(i), hull.get(j)), distance(hull.get(next), hull.get(j))));
        }
        return best;
    }
}
