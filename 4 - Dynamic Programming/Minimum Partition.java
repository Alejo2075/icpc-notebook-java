    Dividir un conjunto en dos subconjuntos de modo que la diferencia de las sumas de los subconjuntos sea mínima
    
    int minDifference(int arr[], int n) {
        int sum = 0;
        
        for (int i = 0; i < n; i++) {
            sum += arr[i];
        }
        
        int y = sum / 2 + 1;
        boolean dp[] = new boolean[y], dd[] = new boolean[y];

        for (int i = 0; i < y; i++) {
            dp[i] = dd[i] = false;
        }

        dd[0] = true;
        for (int i = 0; i < n; i++) {

            for (int j = 0; j + arr[i] < y; j++) {
                if (dp[j]) {
                    dd[j + arr[i]] = true;
                }
            }
            for (int j = 0; j < y; j++) {
                if (dd[j]) {
                    dp[j] = true;
                }
                dd[j] = false;
            }
        }

        for (int i = y - 1; i >= 0; i--) {
            if (dp[i]) {
                return (sum - 2 * i);
            }
        }
        return 0;
    }
