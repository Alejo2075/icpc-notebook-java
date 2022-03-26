    Toma de decisiones para encontrar el movimiento óptimo para un jugador

    int minimax(int depth, int nodeIndex, boolean isMax,
            int scores[], int h) {
        if (depth == h) {
            return scores[nodeIndex];
        }

        if (isMax) {
            return Math.max(minimax(depth + 1, nodeIndex * 2, false, scores, h),
                    minimax(depth + 1, nodeIndex * 2 + 1, false, scores, h));
        } else {
            return Math.min(minimax(depth + 1, nodeIndex * 2, true, scores, h),
                    minimax(depth + 1, nodeIndex * 2 + 1, true, scores, h));
        }
    }

    int log2(int n) {
        return (n == 1) ? 0 : 1 + log2(n / 2);
    }
