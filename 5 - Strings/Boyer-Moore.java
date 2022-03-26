    Coincidencia de cadena larga

    ArrayList<Integer> search(char txt[], char pat[]) {
        ArrayList<Integer> matches = new ArrayList<Integer>();
        int m = pat.length;
        int n = txt.length;
        int badchar[] = new int[256];

        badCharHeuristic(pat, m, badchar);

        int s = 0;
        while (s <= (n - m)) {
            int j = m - 1;
            while (j >= 0 && pat[j] == txt[s + j]) {
                j--;
            }
            if (j < 0) {
                matches.add(s);
                s += (s + m < n) ? m - badchar[txt[s + m]] : 1;
            } else {
                s += Math.max(1, j - badchar[txt[s + j]]);
            }
        }
        return matches;
    }
    
    void badCharHeuristic(char[] str, int size, int badchar[]) {

        for (int i = 0; i < 256; i++) {
            badchar[i] = -1;
        }

        for (int i = 0; i < size; i++) {
            badchar[(int) str[i]] = i;
        }
    }
