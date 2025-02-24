package State;

import java.util.ArrayList;
import java.util.Arrays;

public final class Pair {
    public final int x,y;

    public Pair(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Pair(ArrayList<Integer> intArr) {
        this.x = intArr.get(0);
        this.y = intArr.get(1);
    }

    public ArrayList<Integer> toIntArr() {
        return new ArrayList<>(Arrays.asList(x, y));
    }

    public Pair transpose() {
        //noinspection SuspiciousNameCombination
        return new Pair(y, x);
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
