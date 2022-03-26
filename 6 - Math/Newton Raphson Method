    Encontrar raiz de una funcion con EPSILON error aceptado
    
    final double EPSILON = 0.001;

    static double func(double x) {
        return x * x * x - x * x + 2;
    }

    static double derivFunc(double x) {
        return 3 * x * x - 2 * x;
    }

    double newtonRaphson(double x) {
        double h = func(x) / derivFunc(x);
        while (Math.abs(h) >= EPSILON) {
            h = func(x) / derivFunc(x);
            x = x - h;
        }

        return Math.round(x * 100.0) / 100.0;
    }
