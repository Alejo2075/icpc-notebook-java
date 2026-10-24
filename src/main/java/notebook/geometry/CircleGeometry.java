package notebook;

import java.util.List;

/** Floating-point line, segment, and circle primitives with an absolute epsilon. */
public final class CircleGeometry {
    private CircleGeometry() {}
    private static final double EPS = 1e-10;
    public record Point(double x, double y) {}

    /** Intersection of two infinite lines, or null when parallel/coincident. O(1). */
    public static Point lineIntersection(Point a, Point b, Point c, Point d) {
        double ux = b.x - a.x, uy = b.y - a.y;
        double vx = d.x - c.x, vy = d.y - c.y;
        double determinant = ux * vy - uy * vx;
        if (Math.abs(determinant) < EPS) return null;
        double t = ((c.x - a.x) * vy - (c.y - a.y) * vx) / determinant;
        return new Point(a.x + t * ux, a.y + t * uy);
    }

    /** Euclidean distance from a point to a closed segment. O(1). */
    public static double pointSegmentDistance(Point point, Point a, Point b) {
        double dx = b.x - a.x, dy = b.y - a.y;
        double squared = dx * dx + dy * dy;
        if (squared == 0) return Math.hypot(point.x - a.x, point.y - a.y);
        double t = Math.max(0, Math.min(1, ((point.x - a.x) * dx + (point.y - a.y) * dy) / squared));
        return Math.hypot(point.x - (a.x + t * dx), point.y - (a.y + t * dy));
    }

    /** Intersections of an infinite line with a circle. Returns zero, one, or two points. */
    public static List<Point> circleLine(Point center, double radius, Point a, Point b) {
        if (radius < 0) throw new IllegalArgumentException("negative radius");
        double dx = b.x - a.x, dy = b.y - a.y;
        double lengthSquared = dx * dx + dy * dy;
        if (lengthSquared == 0) throw new IllegalArgumentException("line needs two distinct points");
        double t = ((center.x - a.x) * dx + (center.y - a.y) * dy) / lengthSquared;
        Point foot = new Point(a.x + t * dx, a.y + t * dy);
        double offsetSquared = radius * radius - Math.pow(foot.x - center.x, 2) - Math.pow(foot.y - center.y, 2);
        if (offsetSquared < -EPS) return List.of();
        if (Math.abs(offsetSquared) <= EPS) return List.of(foot);
        double offset = Math.sqrt(offsetSquared / lengthSquared);
        return List.of(new Point(foot.x + offset * dx, foot.y + offset * dy),
                new Point(foot.x - offset * dx, foot.y - offset * dy));
    }

    /** Intersections of two circles; coincident circles are rejected. */
    public static List<Point> circleCircle(Point first, double firstRadius, Point second, double secondRadius) {
        if (firstRadius < 0 || secondRadius < 0) throw new IllegalArgumentException("negative radius");
        double dx = second.x - first.x, dy = second.y - first.y;
        double distance = Math.hypot(dx, dy);
        if (distance < EPS) {
            if (Math.abs(firstRadius - secondRadius) < EPS)
                throw new IllegalArgumentException("infinitely many intersections");
            return List.of();
        }
        if (distance > firstRadius + secondRadius + EPS
                || distance < Math.abs(firstRadius - secondRadius) - EPS) return List.of();
        double along = (firstRadius * firstRadius - secondRadius * secondRadius + distance * distance)
                / (2 * distance);
        double heightSquared = firstRadius * firstRadius - along * along;
        if (heightSquared < -EPS) return List.of();
        double x = first.x + along * dx / distance, y = first.y + along * dy / distance;
        if (Math.abs(heightSquared) <= EPS) return List.of(new Point(x, y));
        double height = Math.sqrt(heightSquared);
        return List.of(new Point(x - height * dy / distance, y + height * dx / distance),
                new Point(x + height * dy / distance, y - height * dx / distance));
    }
}
