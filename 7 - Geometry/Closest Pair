    Par de puntos mas cercanos. Agregar Distance
    
    double distanceBruteForce(Point P[], int n) {
        double min = Double.MAX_VALUE;
        for (int i = 0; i < n; ++i) {
            for (int j = i + 1; j < n; ++j) {
                if (distance(P[i], P[j]) < min) {
                    min = distance(P[i], P[j]);
                }
            }
        }
        return min;
    }

    double stripClosest(Point strip[], int size, double d) {
        double min = d;
        Point aux = null;
        for (int i = 0; i < size; i++) {
            for (int j = 1; j < size; j++) {
                if (strip[i].y > strip[j].y) {
                    aux = strip[j];

                    strip[j] = strip[i];
                    strip[i] = aux;
                }
            }
        }

        for (int i = 0; i < size; ++i) {
            for (int j = i + 1; j < size && (strip[j].y - strip[i].y) < min; ++j) {
                if (distance(strip[i], strip[j]) < min) {
                    min = distance(strip[i], strip[j]);
                }
            }
        }
        return min;
    }

    double closesUtil(Point[] P, int n) {
        if (n <= 3) {
            return distanceBruteForce(P, n);
        }

        int mid = n / 2;
        Point midP = P[mid];

        Point[] Pl = new Point[mid];
        for (int i = 0; i < mid; i++) {
            Pl[i] = P[i];
        }

        Point[] Pr = new Point[n - mid];
        for (int i = 0; i < n - mid; i++) {
            Pr[i] = P[mid + i];
        }

        double dl = closesUtil(Pl, mid);
        double dr = closesUtil(Pr, n - mid);
        double d = Math.min(dl, dr);

        Point[] strip = new Point[n];
        int j = 0;
        for (int i = 0; i < n; i++) {
            if (Math.abs(P[i].x - midP.x) < d) {
                strip[j] = P[i];
                j++;
            }
        }
        return Math.min(d, stripClosest(strip, j, d));
    }

    double closest(Point[] P, int n) {
        Arrays.sort(P);
        return closesUtil(P, n);
    }
    
