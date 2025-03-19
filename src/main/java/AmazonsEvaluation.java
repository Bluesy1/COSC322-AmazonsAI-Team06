import java.util.*;

public class AmazonsEvaluation {

    public static Map<int[], Integer> computeDistances(int[][] board, List<int[]> playerAmazons, boolean isQueenDistance) {
        Map<int[], Integer> distances = new HashMap<>();
        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[0].length; c++) {
                if (board[r][c] == 0) { // Empty square
                    int minDist = Integer.MAX_VALUE;
                    for (int[] amazon : playerAmazons) {
                        int dist = isQueenDistance ? queenDistance(amazon, new int[]{r, c}) : kingDistance(amazon, new int[]{r, c});
                        minDist = Math.min(minDist, dist);
                    }
                    distances.put(new int[]{r, c}, minDist);
                }
            }
        }
        return distances;
    }

    public static int calculateT1(Map<int[], Integer> D1Player, Map<int[], Integer> D1Opponent) {
        int score = 0;
        for (int[] pos : D1Player.keySet()) {
            if (D1Player.get(pos) < D1Opponent.get(pos)) score++;
            else if (D1Player.get(pos) > D1Opponent.get(pos)) score--;
        }
        return score;
    }

    public static double calculateC1(Map<int[], Integer> D1Player, Map<int[], Integer> D2Opponent) {
        double score = 0.0;
        for (int[] pos : D1Player.keySet()) {
            score += Math.pow(2, -D1Player.get(pos)) - Math.pow(2, -D2Opponent.get(pos));
        }
        return 2 * score;
    }

    public static double calculateAlpha(int[] amazon, int[][] board) {
        double alpha = 0.0;
        for (int[] b : getQueenNeighbors(amazon, board)) {
            int N = countEmptyKingNeighbors(b, board);
            alpha += N * Math.pow(2, -kingDistance(amazon, b));
        }
        return alpha;
    }

    public static double calculateM(int[][] board, List<int[]> playerAmazons, List<int[]> opponentAmazons, double w) {
        double mScore = 0.0;
        for (int[] amazon : playerAmazons) {
            double alpha = calculateAlpha(amazon, board);
            mScore -= f(w, alpha);
        }
        for (int[] amazon : opponentAmazons) {
            double alpha = calculateAlpha(amazon, board);
            mScore += f(w, alpha);
        }
        return mScore;
    }

    public static double evaluate(int[][] board, List<int[]> playerAmazons, List<int[]> opponentAmazons) {
        Map<int[], Integer> D1Player = computeDistances(board, playerAmazons, true);
        Map<int[], Integer> D2Player = computeDistances(board, playerAmazons, false);
        Map<int[], Integer> D1Opponent = computeDistances(board, opponentAmazons, true);
        Map<int[], Integer> D2Opponent = computeDistances(board, opponentAmazons, false);

        int t1 = calculateT1(D1Player, D1Opponent);
        int t2 = calculateT1(D2Player, D2Opponent);
        double c1 = calculateC1(D1Player, D2Opponent);
        double c2 = calculateC1(D2Player, D2Opponent);

        double w = 0.0;
        for (int[] a : D1Player.keySet()) {
            w += Math.pow(2, -Math.abs(D1Player.get(a) - D2Player.get(a)));
        }

        double f1 = 1 - w;
        double f2 = w;
        double f3 = w;
        double f4 = 1 - w;

        double t = f1 * t1 + f2 * c1 + f3 * c2 + f4 * t2;
        double m = calculateM(board, playerAmazons, opponentAmazons, w);

        return t + m;
    }

    // Helper functions (to be implemented)
    private static int queenDistance(int[] a, int[] b) {
        return Math.max(Math.abs(a[0] - b[0]), Math.abs(a[1] - b[1]));
    }

    private static int kingDistance(int[] a, int[] b) {
        return Math.max(Math.abs(a[0] - b[0]), Math.abs(a[1] - b[1])) == 1 ? 1 : 2;
    }

    private static List<int[]> getQueenNeighbors(int[] pos, int[][] board) {
        // Returns all queen-move reachable positions
        return new ArrayList<>();
    }

    private static int countEmptyKingNeighbors(int[] pos, int[][] board) {
        // Returns count of empty king-move neighbors
        return 0;
    }

    private static double f(double w, double alpha) {
        // Some function f(w, alpha) to be defined
        return w * alpha;
    }
}