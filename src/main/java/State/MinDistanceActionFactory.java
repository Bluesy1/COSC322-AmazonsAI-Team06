package State;

import java.util.*;

public class MinDistanceActionFactory implements ActionFactory {

    private static final int[] DR = {-1, 1, 0, 0, -1, -1, 1, 1};
    private static final int[] DC = {0, 0, -1, 1, -1, 1, -1, 1};

    public Action getAction(State state, boolean black) {
        int color = black ? State.BLACK : State.WHITE;
        ArrayList<Action> moves = Generator.availableMoves(state, color);

        Collections.shuffle(moves);
        if (moves.isEmpty()) {
            return null;
        }

        int currentControl = Integer.MIN_VALUE;
        Action bfsAction = null;
        for (Action action : moves) {
            if (!Utils.validateMove(state, action, color, false)) {continue;}
            State actionOutcome = new State(state, action);
            Pair[] ourQueens = actionOutcome.getQueens(color);
            Pair[] theirQueens = actionOutcome.getQueens(black ? State.WHITE : State.BLACK);
            int[][] board = actionOutcome.getBoard();

            int tempControl = minDistanceEvaluation(board, ourQueens, theirQueens);
            if (tempControl > currentControl) {
                bfsAction = action;
                currentControl = tempControl;
            }
        }

        return bfsAction;
    }

    public static int[][] bfsMinDistance(int[][] board, int startRow, int startCol) {
        int rows = board.length, cols = board[0].length;
        int[][] distances = new int[rows][cols];
        for (int[] row : distances) Arrays.fill(row, Integer.MAX_VALUE);
        distances[startRow][startCol] = 0;

        Queue<int[]> queue = new LinkedList<>();
        queue.offer(new int[]{startRow, startCol, 0});

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int r = current[0], c = current[1], d = current[2];

            for (int i = 0; i < 8; i++) {
                int nr = r + DR[i], nc = c + DC[i];
                while (nr >= 0 && nr < rows && nc >= 0 && nc < cols && board[nr][nc] == 0) {
                    if (distances[nr][nc] > d + 1) {
                        distances[nr][nc] = d + 1;
                        queue.offer(new int[]{nr, nc, d + 1});
                    }
                    nr += DR[i];
                    nc += DC[i];
                }
            }
        }
        return distances;
    }

    public static int minDistanceEvaluation(int[][] board, Pair[] playerAmazons, Pair[] opponentAmazons) {
        int rows = board.length, cols = board[0].length;
        int[][] playerReach = new int[rows][cols];
        int[][] opponentReach = new int[rows][cols];
        for (int[] row : playerReach) Arrays.fill(row, Integer.MAX_VALUE);
        for (int[] row : opponentReach) Arrays.fill(row, Integer.MAX_VALUE);

        reachCalculate(playerReach, board, playerAmazons);

        reachCalculate(opponentReach, board, opponentAmazons);

        int playerControl = 0, opponentControl = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (playerReach[r][c] < opponentReach[r][c]) playerControl++;
                else if (opponentReach[r][c] < playerReach[r][c]) opponentControl++;
            }
        }

        return playerControl - opponentControl;
    }

    public static void reachCalculate (int[][] reach, int[][] board, Pair[] amazons ) {
        for (int i = 0; i < amazons.length; i++) {
            int[][] distances = bfsMinDistance(board, amazons[i].col, amazons[i].row);
            for (int r = 0; r < board.length; r++)
                for (int c = 0; c < board[0].length; c++)
                    reach[r][c] = Math.min(reach[r][c], distances[r][c]);
        }
    }
}
