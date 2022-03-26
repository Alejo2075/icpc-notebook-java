    Area de un poligono de puntos P

    double area(ArrayList<Point> P) {
        double result = 0.0;
        for (int i = 0; i < P.size() - 1; i++) {
            result += ((P.get(i).x * P.get(i + 1).y) - (P.get(i + 1).x * P.get(i).y));
        }
        return Math.abs(result) / 2.0;
    }
