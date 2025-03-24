package State;

import java.util.Arrays;

public class WeightCalculator {
    public static double calculateWeight(int[][] playerReach, int[][] opponentReach, int[][] board) {
        double w = 0.0;

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (!(board[i][j] == 0)) {continue;}
                double playerD1 = playerReach[i][j];
                double opponentD1 = opponentReach[i][j];
                w += Math.pow(2, -Math.abs(playerD1 - opponentD1));
            }
        }

        return w;
    }
}
