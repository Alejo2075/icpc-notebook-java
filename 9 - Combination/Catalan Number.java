    Genera los numeros de Catalan hasta n

    int[] catalan(int n) {
        int cat = 1;
        int[] cN = new int[n];

        System.out.print(cat + " "); // C(0)

        for (int i = 1; i < n; i++) {
            cat *= (4 * i - 2);
            cat /= (i + 1);
            cN[i] = cat;
        }
        return cN;
    }
