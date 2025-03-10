package State;

import ygraph.ai.smartfox.games.amazons.AmazonsGameMessage;

import java.util.*;

public class Action {
    protected int fromRow, fromCol, toRow, toCol, arrowRow, arrowCol;
    protected Pair origin, destination, arrow;
    private final String[] cols = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j"};
    /**
     * Converts a map to an action.
     *
     * @param actionMap The map returned by the server
     */

    @SuppressWarnings("unchecked")
    public Action(Map<String, Object> actionMap) {
        ArrayList<Integer> fromPos = (ArrayList<Integer>) actionMap.get(AmazonsGameMessage.QUEEN_POS_CURR);
        ArrayList<Integer> toPos = (ArrayList<Integer>) actionMap.get(AmazonsGameMessage.QUEEN_POS_NEXT);
        ArrayList<Integer> arrowPos = (ArrayList<Integer>) actionMap.get(AmazonsGameMessage.ARROW_POS);
        origin = new Pair(fromPos).add(Pair.neg);
        destination = new Pair(toPos).add(Pair.neg);
        arrow = new Pair(arrowPos).add(Pair.neg);

        fromRow = origin.y;
        fromCol = origin.x;
        toRow = destination.y;
        toCol = destination.x;
        arrowRow = arrow.y;
        arrowCol = arrow.x;
    }

    public Action(Pair origin, Pair destination, Pair arrow) {
        this.origin = origin;
        this.destination = destination;
        this.arrow = arrow;

        fromRow = origin.x;
        fromCol = origin.y;
        toRow = destination.x;
        toCol = destination.y;
        arrowRow = arrow.x;
        arrowCol = arrow.y;
    }

    /**
     * Represents the action in the conventional notation of [oldPos]-[newPos]/[arrowPos]
     *
     * @return The action in the conventional notation
     */
    @Override
    public String toString() {
        return String.format("%s%d-%s%d/%s%d",
            cols[fromCol], (fromRow + 1), cols[toCol], (toRow + 1), cols[arrowCol], (arrowRow + 1)
        );
    }

    /**
     * Converts the action to a map that can be sent to the server
     *
     * @return A map that can be sent to the server
     */
    public Map<String, Object> toServerResponse() {
        Map<String, Object> map = new HashMap<>();
        map.put(AmazonsGameMessage.QUEEN_POS_CURR, origin.add(Pair.pos).toIntArr());
        map.put(AmazonsGameMessage.QUEEN_POS_NEXT, destination.add(Pair.pos).toIntArr());
        map.put(AmazonsGameMessage.ARROW_POS, arrow.add(Pair.pos).toIntArr());
        return map;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Action action = (Action) o;
        return fromRow == action.fromRow
                && fromCol == action.fromCol
                && toRow == action.toRow
                && toCol == action.toCol
                && arrowRow == action.arrowRow
                && arrowCol == action.arrowCol;
    }

    public Pair getOrigin() {
        return origin;
    }

    public Pair getDestination() {
        return destination;
    }

    public Pair getArrow() {
        return arrow;
    }
}
