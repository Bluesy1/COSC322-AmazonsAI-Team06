package State;

import java.util.Queue;

public interface ActionFactory {

    public Action[] getAction(State state, boolean black, int movesPlayed, int topN);

}
