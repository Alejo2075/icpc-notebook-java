    Calcula los coeficientes enteros X y Y que satisfacen la ecuación: a*X + b*Y = gcd(a, b).

    int euclid(int a, int b) {
        int x,y;
        if (b == 0) {
            x = 1;
            y = 0;
            return a;
        }
        int d = euclid(b, a % b);
        int aux = x;
        x = y;
        y = aux - ((a / b) * y);
        return d;
    }
