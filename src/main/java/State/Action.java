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
        origin = new Pair(fromPos.get(1) - 1, fromPos.get(0) - 1);
        destination = new Pair(toPos.get(1) - 1, toPos.get(0) - 1);
        arrow = new Pair(arrowPos.get(1) - 1, arrowPos.get(0) - 1);

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
        map.put(AmazonsGameMessage.QUEEN_POS_CURR, new ArrayList<>(Arrays.asList(fromRow + 1, fromCol + 1)));
        map.put(AmazonsGameMessage.QUEEN_POS_NEXT, new ArrayList<>(Arrays.asList(toRow + 1, toCol + 1)));
        map.put(AmazonsGameMessage.ARROW_POS, new ArrayList<>(Arrays.asList(arrowRow + 1, arrowCol + 1)));
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
}
