   Calculo del mínimo común múltiplo usando el máximo común divisor. Agregar Greatest Common Divisor.
   
   double lcm(double a, double b) {
        return a * (b / gcd(a, b));
   }
