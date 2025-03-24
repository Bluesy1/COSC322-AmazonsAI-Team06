package State;

import java.util.*;

public class KingDistanceActionFactory implements ActionFactory {

    private static final int[] DR = {-1, 1, 0, 0, -1, -1, 1, 1};
    private static final int[] DC = {0, 0, -1, 1, -1, 1, -1, 1};

    @Override
    public Action getAction(State state, boolean black) {
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

            int tempControl = kingDistanceEvaluation(board, ourQueens, theirQueens);
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

    public static int kingDistanceEvaluation(int[][] board, Pair[] playerAmazons, Pair[] opponentAmazons) {
        int rows = board.length, cols = board[0].length;
        int[][] playerKingReach = new int[rows][cols];
        int[][] opponentKingReach = new int[rows][cols];
        for (int[] row : playerKingReach) Arrays.fill(row, Integer.MAX_VALUE);
        for (int[] row : opponentKingReach) Arrays.fill(row, Integer.MAX_VALUE);

        // Compute minimum king distances for player amazons
        for (Pair playerAmazon : playerAmazons) {
            int[][] distances = bfsKingMinDistance(board, playerAmazon.col, playerAmazon.row);
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    playerKingReach[r][c] = Math.min(playerKingReach[r][c], distances[r][c]);
                }
            }
        }

        // Compute minimum king distances for opponent amazons
        for (Pair oppAmazon : opponentAmazons) {
            int[][] distances = bfsKingMinDistance(board, oppAmazon.col, oppAmazon.row);
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    opponentKingReach[r][c] = Math.min(opponentKingReach[r][c], distances[r][c]);
                }
            }
        }

        // Calculate control difference
        int playerControl = 0, opponentControl = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (playerKingReach[r][c] < opponentKingReach[r][c]) playerControl++;
                else if (opponentKingReach[r][c] < playerKingReach[r][c]) opponentControl++;
            }
        }

        return playerControl - opponentControl;
    }
}
