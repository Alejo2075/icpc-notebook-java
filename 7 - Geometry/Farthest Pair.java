    Par de puntos mas alejados en el plano

    double rotatingCaliper(Point points[]) {
        ArrayList<Point> convexHull = convexHull(points, points.length);
        int n = convexHull.size();
        Point hull[] = new Point[n];
        n = 0;

        while (n < convexHull.size()) {
            hull[n] = convexHull.get(n++);
        }

        int k = 1;

        while (area(hull[n - 1], hull[0], hull[(k + 1) % n])
                > area(hull[n - 1], hull[0], hull[k])) {
            k++;
        }

        double res = 0;

        for (int i = 0, j = k; i <= k && j < n; i++) {
            res = Math.max(res, Math.sqrt((double) dist(hull[i], hull[j])));

            while (j < n && area(hull[i], hull[(i + 1) % n], hull[(j + 1) % n])
                    > area(hull[i], hull[(i + 1) % n], hull[j])) {
                res = Math.max(res, Math.sqrt(dist(hull[i], hull[(j + 1) % n])));
                j++;
            }
        }

        return res;
    }
    
    int area(Point p, Point q, Point r) {
        return Math.abs((p.y - q.y) * (q.x - r.x) - (q.y - r.y) * (p.x - q.x));
    }
