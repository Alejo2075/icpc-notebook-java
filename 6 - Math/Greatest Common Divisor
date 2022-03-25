    Calcula el máximo común divisor entre a y b 

    double gcd(double a, double b) {
        if (a < b) {
            return gcd(b, a);
        }
        if (Math.abs(b) < 0.001) {
            return a;
        } else {
            return (gcd(b, a - Math.floor(a / b) * b));
        }
    }
