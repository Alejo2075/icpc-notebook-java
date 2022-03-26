    Perimetro del poligono de puntos P

    double perimeter(ArrayList<Point> P) {
        double result = 0.0;
        for (int i = 0; i < P.size() - 1; i++) {
            result += distance(P.get(i), P.get(i + 1));
        }
        return result;
    }
