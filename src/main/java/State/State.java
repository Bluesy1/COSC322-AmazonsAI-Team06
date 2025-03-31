package State;

import java.util.ArrayList;
import java.util.Random;

public class State implements Cloneable {

    public static final int BOARD_SIZE = 10;
    public static final int BLACK = 1;
    public static final int WHITE = 2;
    public static final int ARROW = 3;
    private static final long[][][] zobristTable;
    private static final long zobristSide;

    static {
        Random rand = new Random(123456789); // Seed for consistency
        zobristTable = new long[BOARD_SIZE][BOARD_SIZE][3]; // 3 for Black and White queens as well as arrows
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                for (int k = 0; k < 3; k++) {
                    zobristTable[i][j][k] = rand.nextLong();
                }
            }
        }
        zobristSide = rand.nextLong();
    }

    private Pair[] whiteQueens;
    private Pair[] blackQueens;
    private int[][] board;
    private boolean isBlackTurn;

    public State(ArrayList<Integer> gameState) {
        board = new int[BOARD_SIZE][BOARD_SIZE];
        whiteQueens = new Pair[4];
        blackQueens = new Pair[4];
        int numWhiteFound = 0;
        int numBlackFound = 0;
        for (int col = 0; col < BOARD_SIZE; col++) {
            for (int row = 0; row < BOARD_SIZE; row++) {
                int type = gameState.get((row + 1) * 11 + col + 1);
                switch (type) {
                    case WHITE -> whiteQueens[numWhiteFound++] = new Pair(col, row);
                    case BLACK -> blackQueens[numBlackFound++] = new Pair(col, row);
                }
                board[col][row] = type;
            }
        }
        isBlackTurn = true;
    }

    public State(State state, Action action) {
        this.whiteQueens = state.whiteQueens.clone();
        this.blackQueens = state.blackQueens.clone();
        this.board = new int[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++) {
            System.arraycopy(state.board[i], 0, this.board[i], 0, BOARD_SIZE);
        }
//        this.board = state.board.clone();

        int movingPiece = getPos(action.fromCol, action.fromRow);
        setPiece(action.toCol, action.toRow, movingPiece);
        clearPiece(action.fromCol, action.fromRow);
        setPiece(action.arrowCol, action.arrowRow, ARROW);

        if (movingPiece == WHITE) {
            for (int i = 0; i < whiteQueens.length; i++) {
                if (action.origin.equals(whiteQueens[i])) {
                    whiteQueens[i] = action.destination;
                    break;
                }
            }
        } else {
            for (int i = 0; i < blackQueens.length; i++) {
                if (action.origin.equals(blackQueens[i])) {
                    blackQueens[i] = action.destination;
                    break;
                }
            }
        }
        isBlackTurn = !isBlackTurn;
    }

    public Pair[] getQueens(int color) {
        switch (color) {
            case WHITE -> {
                return whiteQueens;
            }
            case BLACK -> {
                return blackQueens;
            }
            default -> throw new IllegalArgumentException(color + " is an invalid color");
        }
    }

    public int getPos(Pair pos) {
        return getPos(pos.col, pos.row);
    }

    public int getPos(int x, int y) {
        return board[x][y];
    }

    public void setPiece(int x, int y, int piece) {
        board[x][y] = piece;
    }

    public void clearPiece(int x, int y) {
        board[x][y] = 0;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        State clone = (State) super.clone();
        clone.board = this.board.clone();
        clone.whiteQueens = this.whiteQueens.clone();
        clone.blackQueens = this.blackQueens.clone();
        return clone;
    }

    public String boardToString() {
        StringBuilder sb = new StringBuilder();
        for (int row = BOARD_SIZE - 1; row >= 0; row--) {
            sb.append(String.format("%2d ", row + 1));
            for (int col = 0; col < BOARD_SIZE; col++) {
                char piece;
                switch (getPos(col, row)) {
                    case WHITE -> piece = 'W';
                    case BLACK -> piece = 'B';
                    case ARROW -> piece = 'X';
                    default -> piece = '-';
                }
                sb.append(String.format("%c ", piece));
            }
            sb.append("\n");
        }
        sb.append("   a b c d e f g h i j");
        return sb.toString();
    }

    public String boardToStringNumbers() {
        StringBuilder sb = new StringBuilder();
        for (int row = BOARD_SIZE - 1; row >= 0; row--) {
            sb.append(String.format("%2d ", row + 1));
            for (int col = 0; col < BOARD_SIZE; col++) {
                char piece;
                switch (getPos(col, row)) {
                    case WHITE -> piece = '2';
                    case BLACK -> piece = '1';
                    case ARROW -> piece = 'X';
                    default -> piece = '-';
                }
                sb.append(String.format("%c ", piece));
            }
            sb.append("\n");
        }
        sb.append("   a b c d e f g h i j");
        return sb.toString();
    }

    public int evaluate(boolean forBlack) {
        int color = forBlack ? State.BLACK : State.WHITE;
        Pair[] ourQueens = getQueens(color);
        Pair[] theirQueens = getQueens(forBlack ? State.WHITE : State.BLACK);
        int[][] board = getBoard();

        ArrayList<int[][]> reaches = MinDistanceActionFactory.minDistanceEvaluation(board, ourQueens, theirQueens);
        int playerControl = 0, opponentControl = 0;
        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[0].length; c++) {
                if (reaches.get(0)[r][c] < reaches.get(1)[r][c]) {
                    playerControl++;
                } else if (reaches.get(1)[r][c] < reaches.get(0)[r][c]) {
                    opponentControl++;
                }
            }
        }
        return playerControl - opponentControl;
    }

    public int[][] getBoard() {
        return board;
    }

    public long getZobristHash() {
        long hash = 0;
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                int piece = board[i][j];
                if (piece != 0) { // 0 = empty, 1 = Black Amazon, 2 = White Amazon
                    hash ^= zobristTable[i][j][piece - 1];
                }
            }
        }
        if (isBlackTurn) {
            hash ^= zobristSide;
        }
        return hash;
    }
}
