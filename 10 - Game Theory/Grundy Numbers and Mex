    Encuentra el numero de Grundy

    int calculateMex(Set<Integer> Set) {
        int Mex = 0;

        while (Set.contains(Mex)) {
            Mex++;
        }

        return (Mex);
    }

    int calculateGrundy(int n) {
        if (n == 0) {
            return (0);
        }

        Set<Integer> Set = new HashSet<Integer>();

        for (int i = 0; i <= n - 1; i++) {
            Set.add(calculateGrundy(i));
        }

        return (calculateMex(Set));
    }
