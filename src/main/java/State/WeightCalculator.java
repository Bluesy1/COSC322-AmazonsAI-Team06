package State;

import java.util.Arrays;

public class WeightCalculator {
    public static double calculateWeight(State s, boolean black) {
        int[][] board = s.getBoard();

        int color = black ? State.BLACK : State.WHITE
        int rows = board.length, cols = board[0].length;

        int[][] playerReach = new int[rows][cols];
        int[][] opponentReach = new int[rows][cols];
        for (int[] row : playerReach) Arrays.fill(row, Integer.MAX_VALUE);
        for (int[] row : opponentReach) Arrays.fill(row, Integer.MAX_VALUE);

        Pair[] playerAmazons = s.getQueens(color);
        Pair[] opponentAmazons = s.getQueens(black ? State.WHITE : State.BLACK);

        // This can definitely be optimized because its redoing code from MinDistanceActionFactory

        for (int i = 0; i < playerAmazons.length; i++) {
            int[][] distances = MinDistanceActionFactory.bfsMinDistance(board, playerAmazons[i].col, playerAmazons[i].row);
            for (int r = 0; r < rows; r++)
                for (int c = 0; c < cols; c++)
                    playerReach[r][c] = Math.min(playerReach[r][c], distances[r][c]);
        }

        for (int i = 0; i < opponentAmazons.length; i++) {
            int[][] distances = MinDistanceActionFactory.bfsMinDistance(board, opponentAmazons[i].col, opponentAmazons[i].row);
            for (int r = 0; r < rows; r++)
                for (int c = 0; c < cols; c++)
                    opponentReach[r][c] = Math.min(opponentReach[r][c], distances[r][c]);
        }

        double w = 0.0;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double playerD1 = playerReach[i][j];
                double opponentD1 = opponentReach[i][j];
                w += Math.pow(2, -Math.abs(playerD1 - opponentD1));
            }
        }

        return w;
    }
}
