package State;

import java.util.*;

public class State implements Cloneable {

    private Pair[] whiteQueens;
    private Pair[] blackQueens;
    private int[][] board;

    public static final int BOARD_SIZE = 10;
    public static final int BLACK = 1;
    public static final int WHITE = 2;
    public static final int ARROW = 3;

    public State(ArrayList<Integer> gameState){
        board = new int[BOARD_SIZE][BOARD_SIZE];
        whiteQueens = new Pair[4];
        blackQueens = new Pair[4];
        int numWhiteFound = 0;
        int numBlackFound = 0;
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                int type = gameState.get((j + 1)* 11 + i + 1);
                switch (gameState.get((j + 1)* 11 + i + 1)) {
                    case WHITE -> {whiteQueens[numWhiteFound++] = new Pair(i, j);}
                    case BLACK -> {blackQueens[numBlackFound++] = new Pair(i, j);}
                }
                board[i][j] = type;
            }
        }
    }

    public State(State state, Action action) {
        this.whiteQueens = state.whiteQueens.clone();
        this.blackQueens = state.blackQueens.clone();
        this.board = state.board.clone();

        int movingPiece = getPiece(action.fromCol, action.fromRow);
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
    }

    public Pair[] getQueens(int color) {
        switch (color) {
            case WHITE -> {return whiteQueens;}
            case BLACK -> {return blackQueens;}
            default -> {throw new IllegalArgumentException(color + " is an invalid color");}
        }
    }

    public int getPiece(int x, int y) {
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

    public String boardToString(){
        StringBuilder sb = new StringBuilder();
        for (int row = BOARD_SIZE-1; row >= 0; row--) {
            sb.append(String.format("%2d ", row + 1));
            for (int col = BOARD_SIZE-1; col >= 0; col--) {
                char piece;
                switch (getPiece(col, row)) {
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

}
