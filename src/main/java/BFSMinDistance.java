

import State.Pair;

import java.util.*;

public class BFSMinDistance {
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

    public static int minDistanceEvaluation(int[][] board, Pair[] playerAmazons, Pair[] opponentAmazons) {
        int rows = board.length, cols = board[0].length;
        int[][] playerReach = new int[rows][cols];
        int[][] opponentReach = new int[rows][cols];
        for (int[] row : playerReach) Arrays.fill(row, Integer.MAX_VALUE);
        for (int[] row : opponentReach) Arrays.fill(row, Integer.MAX_VALUE);

        for (int i = 0; i < playerAmazons.length; i++) {
            int[][] distances = bfsMinDistance(board, playerAmazons[i].col, playerAmazons[i].row);
            for (int r = 0; r < rows; r++)
                for (int c = 0; c < cols; c++)
                    playerReach[r][c] = Math.min(playerReach[r][c], distances[r][c]);
        }

        for (int i = 0; i < opponentAmazons.length; i++) {
            int[][] distances = bfsMinDistance(board, opponentAmazons[i].col, opponentAmazons[i].row);
            for (int r = 0; r < rows; r++)
                for (int c = 0; c < cols; c++)
                    opponentReach[r][c] = Math.min(opponentReach[r][c], distances[r][c]);
        }

        int playerControl = 0, opponentControl = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (playerReach[r][c] < opponentReach[r][c]) playerControl++;
                else if (opponentReach[r][c] < playerReach[r][c]) opponentControl++;
            }
        }

        return playerControl - opponentControl;
    }
}