package notebook;

import java.math.BigInteger;
import java.util.List;

/** Exact integer predicates for planar geometry. */
public final class GeometryAdvanced {
    private GeometryAdvanced() {}

    public record Point(long x, long y) {}

    /** Signed cross product (b-a) x (c-a), exact even near long limits. */
    public static BigInteger cross(Point a, Point b, Point c) {
        BigInteger dx1 = BigInteger.valueOf(b.x).subtract(BigInteger.valueOf(a.x));
        BigInteger dy1 = BigInteger.valueOf(b.y).subtract(BigInteger.valueOf(a.y));
        BigInteger dx2 = BigInteger.valueOf(c.x).subtract(BigInteger.valueOf(a.x));
        BigInteger dy2 = BigInteger.valueOf(c.y).subtract(BigInteger.valueOf(a.y));
        return dx1.multiply(dy2).subtract(dy1.multiply(dx2));
    }

    /** -1 clockwise, 0 collinear, +1 counterclockwise. */
    public static int orientation(Point a, Point b, Point c) {
        return cross(a, b, c).signum();
    }

    private static boolean onSegment(Point a, Point b, Point p) {
        return orientation(a, b, p) == 0 && Math.min(a.x, b.x) <= p.x && p.x <= Math.max(a.x, b.x)
            && Math.min(a.y, b.y) <= p.y && p.y <= Math.max(a.y, b.y);
    }

    /** Inclusive segment intersection; handles endpoints and collinearity. */
    public static boolean segmentsIntersect(Point a, Point b, Point c, Point d) {
        int abC = orientation(a, b, c), abD = orientation(a, b, d);
        int cdA = orientation(c, d, a), cdB = orientation(c, d, b);
        if (abC == 0 && onSegment(a, b, c)) return true;
        if (abD == 0 && onSegment(a, b, d)) return true;
        if (cdA == 0 && onSegment(c, d, a)) return true;
        if (cdB == 0 && onSegment(c, d, b)) return true;
        return abC != abD && cdA != cdB;
    }

    /** True inside or on the boundary of a simple polygon. O(n). */
    public static boolean pointInPolygon(List<Point> polygon, Point point) {
        boolean inside = false;
        for (int i = 0; i < polygon.size(); i++) {
            Point a = polygon.get(i), b = polygon.get((i + 1) % polygon.size());
            if (onSegment(a, b, point)) return true;
            boolean crosses = (a.y > point.y) != (b.y > point.y);
            if (crosses) {
                int sign = orientation(a, b, point);
                if ((b.y > a.y && sign > 0) || (b.y < a.y && sign < 0)) inside = !inside;
            }
        }
        return inside;
    }

    /** Twice the signed polygon area, exact. Input need not repeat the first point. */
    public static BigInteger signedDoubleArea(List<Point> polygon) {
        BigInteger result = BigInteger.ZERO;
        for (int i = 0; i < polygon.size(); i++) {
            Point a = polygon.get(i), b = polygon.get((i + 1) % polygon.size());
            result = result.add(BigInteger.valueOf(a.x).multiply(BigInteger.valueOf(b.y))
                .subtract(BigInteger.valueOf(a.y).multiply(BigInteger.valueOf(b.x))));
        }
        return result;
    }
}
