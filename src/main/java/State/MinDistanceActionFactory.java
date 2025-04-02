package State;

import java.util.*;

public class MinDistanceActionFactory implements ActionFactory {

    private static final int[] DR = {-1, 1, 0, 0, -1, -1, 1, 1};
    private static final int[] DC = {0, 0, -1, 1, -1, 1, -1, 1};

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

    public static ArrayList<int[][]> minDistanceEvaluation(int[][] board, Pair[] playerAmazons, Pair[] opponentAmazons) {
        int rows = board.length, cols = board[0].length;
        int[][] playerReach = new int[rows][cols];
        int[][] opponentReach = new int[rows][cols];
        for (int[] row : playerReach) Arrays.fill(row, Integer.MAX_VALUE);
        for (int[] row : opponentReach) Arrays.fill(row, Integer.MAX_VALUE);

        reachCalculate(playerReach, board, playerAmazons);

        reachCalculate(opponentReach, board, opponentAmazons);

        ArrayList<int[][]> reaches = new ArrayList<>();
        reaches.add(playerReach);
        reaches.add(opponentReach);

        return reaches;
    }

    public static void reachCalculate(int[][] reach, int[][] board, Pair[] amazons) {
        for (Pair amazon : amazons) {
            int[][] distances = bfsMinDistance(board, amazon.col, amazon.row);
            for (int r = 0; r < board.length; r++)
                for (int c = 0; c < board[0].length; c++)
                    reach[r][c] = Math.min(reach[r][c], distances[r][c]);
        }
    }

    @Override
    public ActionControlPair[] getAction(State state, boolean black, int topN) {
        int color = black ? State.BLACK : State.WHITE;
        ArrayList<Action> moves = Generator.availableMoves(state, color);

        if (moves.size() < topN) {
            topN = moves.size();
        }

        if (moves.isEmpty()) {
            return null;
        }

        ActionControlPair[] bfsActionArray = new ActionControlPair[topN];

        List<ActionControlPair> actions = (moves.size() <= 10 ? moves.stream() : moves.parallelStream())
                .map(action ->{
            State actionOutcome = new State(state, action);
            Pair[] ourQueens = actionOutcome.getQueens(color);
            Pair[] theirQueens = actionOutcome.getQueens(black ? State.WHITE : State.BLACK);
            int[][] board = actionOutcome.getBoard();

            ArrayList<int[][]> reaches = minDistanceEvaluation(board, ourQueens, theirQueens);

            int playerControl = 0, opponentControl = 0;
            for (int r = 0; r < board.length; r++) {
                for (int c = 0; c < board[0].length; c++) {
                    int cmp = Integer.compare(reaches.get(0)[r][c], reaches.get(1)[r][c]);
                    if (cmp < 0) {
                        playerControl++;
                    } else if (cmp > 0){
                        opponentControl++;
                    }
                }
            }

            return new ActionControlPair(action, playerControl - opponentControl);
        }).sorted().toList();

        for (int i = 0; i < topN; i++) {
            bfsActionArray[i] = actions.get(i);
        }

        return bfsActionArray;
    }
}
