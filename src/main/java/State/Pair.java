package State;

import java.util.ArrayList;
import java.util.Arrays;

public final class Pair {
    public final int x,y;
    public static final Pair neg = new Pair(-1, -1);
    public static final Pair pos = new Pair(1, 1);

    public Pair(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Pair(ArrayList<Integer> intArr) {
        this.x = intArr.get(1);
        this.y = intArr.get(0);
    }

    public ArrayList<Integer> toIntArr() {
        return new ArrayList<>(Arrays.asList(x, y));
    }

    public Pair transpose() {
        //noinspection SuspiciousNameCombination
        return new Pair(y, x);
    }

    public Pair add(Pair p) {
        return new Pair(x + p.x, y + p.y);
    }

    public boolean isInBounds() {
        return x >= 0 && x < State.BOARD_SIZE
                && y >= 0 && y < State.BOARD_SIZE;
    }

    @Override
    public String toString() {
        return String.format("(%d,%d)", x, y);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Pair pair) {
            return pair.x == this.x && pair.y == this.y;
        }
        return false;
    }
}
