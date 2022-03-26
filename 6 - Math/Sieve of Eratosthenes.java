    Genera todos los numeros primos menores que n

    ArrayList sieveOfEratosthenes(int n) {
        boolean prime[] = new boolean[n + 1];
        for (int i = 0; i <= n; i++) {
            prime[i] = true;
        }

        for (int p = 2; p * p <= n; p++) {
            if (prime[p] == true) {
                for (int i = p * p; i <= n; i += p) {
                    prime[i] = false;
                }
            }
        }

        ArrayList<Integer> primes = new ArrayList<Integer>();
        for (int i = 2; i <= n; i++) {
            if (prime[i] == true) {
                primes.add(i);
            }
        }
        return primes;
    }
