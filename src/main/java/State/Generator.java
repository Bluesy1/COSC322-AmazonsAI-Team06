package State;

import java.util.*;
import java.util.stream.Stream;

public class Generator {

    private static final Pair[] directions = {
            new Pair(0, -1),
            new Pair(1, 0),
            new Pair(0, 1),
            new Pair(-1, 0),
            new Pair(1, 1),
            new Pair(-1, -1),
            new Pair(1, -1),
            new Pair(-1, 1)
    };

    public static ArrayList<Action> availableMoves(State state, int color) {
        Pair[] queens = state.getQueens(color);

        return new ArrayList<>(
            Arrays.stream(queens).flatMap((queen) -> Arrays.stream(directions).flatMap((direction) -> {
                ArrayList<Pair> tiles = new ArrayList<>();
                Pair dest = queen.add(direction);
                while (dest.isInBounds() && state.getPos(dest) == 0) {
                    tiles.add(dest);
                    dest = dest.add(direction);
                }
                return tiles.stream().flatMap((to) -> generateActionsForQueenPos(queen, to, state));
            })).toList()
        );
    }

    private static Stream<Action> generateActionsForQueenPos(Pair old, Pair dest, State state) {
        return Arrays.stream(directions).flatMap((direction) -> {
            ArrayList<Action> moves = new ArrayList<>();
            Pair arrow = dest.add(direction);
            while (arrow.isInBounds() && (arrow.equals(old) || (state.getPos(arrow) == 0))) {
                moves.add(new Action(old, dest, arrow));
                arrow = arrow.add(direction);
            }
            return moves.stream();
        });
    }
}
