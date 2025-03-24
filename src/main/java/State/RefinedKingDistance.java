package State;

public class RefinedKingDistance {
    public static double calculateRefinedKingDistance (int[][] playerReach, int[][] opponentReach, int rows, int cols, int[][] board) {
        double refinedDistance = 0.0;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (!(board[i][j] == 0)) {continue;}
                double playerD2 = playerReach[i][j];
                double opponentD2 = opponentReach[i][j];
                refinedDistance += Math.min(1, Math.max(-1, (playerD2-opponentD2))/6);
            }
        }

        return refinedDistance;
    }
}
