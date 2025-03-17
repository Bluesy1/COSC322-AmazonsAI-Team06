package State;

import java.util.ArrayList;
import java.util.Arrays;

public final class Pair {
    public final int col, row;
    public static final Pair neg = new Pair(-1, -1);
    public static final Pair pos = new Pair(1, 1);

    public Pair(int col, int row) {
        this.col = col;
        this.row = row;
    }

    public Pair(ArrayList<Integer> intArr) {
        this.col = intArr.get(1);
        this.row = intArr.get(0);
    }

    public ArrayList<Integer> toIntArr() {
        return new ArrayList<>(Arrays.asList(row, col));
    }

    public Pair add(Pair p) {
        return new Pair(col + p.col, row + p.row);
    }

    public boolean isInBounds() {
        return col >= 0 && col < State.BOARD_SIZE
                && row >= 0 && row < State.BOARD_SIZE;
    }

    @Override
    public String toString() {
        return String.format("(%d,%d)", col, row);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Pair pair) {
            return pair.col == this.col && pair.row == this.row;
        }
        return false;
    }
}
