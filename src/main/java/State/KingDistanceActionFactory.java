/*
package State;

import java.util.*;

public class KingDistanceActionFactory implements ActionFactory {

    private static final int[] DR = {-1, 1, 0, 0, -1, -1, 1, 1};
    private static final int[] DC = {0, 0, -1, 1, -1, 1, -1, 1};


    public Action getAction(State state, boolean black, int movesPlayed) {
        int color = black ? State.BLACK : State.WHITE;
        ArrayList<Action> moves = Generator.availableMoves(state, color);

        Collections.shuffle(moves);
        if (moves.isEmpty()) {
            return null;
        }

        int currentControl = Integer.MIN_VALUE;
        Action kingMinAction = null;

        for (Action action : moves) {
            if (!Utils.validateMove(state, action, color, false)) {continue;}
            State actionOutcome = new State(state, action);
            Pair[] ourQueens = actionOutcome.getQueens(color);
            Pair[] theirQueens = actionOutcome.getQueens(black ? State.WHITE : State.BLACK);
            int[][] board = actionOutcome.getBoard();

            ArrayList<int[][]> reaches = new ArrayList<>();
            reaches = kingDistanceEvaluation(board, ourQueens, theirQueens);

            int playerControl = 0, opponentControl = 0;
            for (int r = 0; r < board.length; r++) {
                for (int c = 0; c < board[0].length; c++) {
                    if (reaches.get(0)[r][c] < reaches.get(1)[r][c]) playerControl++;
                    else opponentControl++;
                }
            }

            int tempControl = playerControl - opponentControl;
            if (tempControl > currentControl) {
                kingMinAction = action;
                currentControl = tempControl;
            }
        }

        return kingMinAction;

    }

    public static int[][] bfsKingMinDistance(int[][] board, int startRow, int startCol) {
        int rows = board.length, cols = board[0].length;
        int[][] distances = new int[rows][cols];
        for (int[] row : distances) Arrays.fill(row, Integer.MAX_VALUE);
        distances[startRow][startCol] = 0;

        Queue<int[]> queue = new LinkedList<>();
        queue.offer(new int[]{startRow, startCol, 0});

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int r = current[0], c = current[1], d = current[2];

            // Explore all 8 adjacent squares
            for (int i = 0; i < 8; i++) {
                int nr = r + DR[i];
                int nc = c + DC[i];
                // Check bounds and if the square is empty
                if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && board[nr][nc] == 0) {
                    if (distances[nr][nc] > d + 1) {
                        distances[nr][nc] = d + 1;
                        queue.offer(new int[]{nr, nc, d + 1});
                    }
                }
            }
        }
        return distances;
    }

    public static ArrayList<int[][]> kingDistanceEvaluation(int[][] board, Pair[] playerAmazons, Pair[] opponentAmazons) {
        int rows = board.length, cols = board[0].length;
        int[][] playerReach = new int[rows][cols];
        int[][] opponentReach = new int[rows][cols];
        for (int[] row : playerReach) Arrays.fill(row, Integer.MAX_VALUE);
        for (int[] row : opponentReach) Arrays.fill(row, Integer.MAX_VALUE);

        // Compute minimum king distances for player amazons
        reachCalculate(playerReach, board, playerAmazons);

        // Compute minimum king distances for opponent amazons
        reachCalculate(opponentReach, board, opponentAmazons);

        ArrayList<int[][]> reaches = new ArrayList<>();
        reaches.add(playerReach);
        reaches.add(opponentReach);

        return reaches;
    }

    public static void reachCalculate (int[][] reach, int[][] board, Pair[] amazons ) {
        for (int i = 0; i < amazons.length; i++) {
            int[][] distances = bfsKingMinDistance(board, amazons[i].col, amazons[i].row);
            for (int r = 0; r < board.length; r++)
                for (int c = 0; c < board[0].length; c++)
                    reach[r][c] = Math.min(reach[r][c], distances[r][c]);
        }
    }

    @Override
    public Action[] getAction(State state, boolean black, int movesPlayed, int topN) {
        return new Action[0];
    }
}
*/
