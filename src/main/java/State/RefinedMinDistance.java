package State;

public class RefinedMinDistance {
    public static double calculateRefinedMinDistance (int[][] playerReach, int[][] opponentReach, int rows, int cols, int[][] board) {
        double refinedDistance = 0.0;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (!(board[i][j] == 0)) {continue;}
                double playerD1 = playerReach[i][j];
                double opponentD1 = opponentReach[i][j];
                refinedDistance += Math.pow(2, -(playerD1)) - Math.pow(2, -(opponentD1));
            }
        }

        return refinedDistance*2;
    }
}
