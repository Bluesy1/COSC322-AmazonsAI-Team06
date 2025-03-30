package State;

import java.util.Queue;

public interface ActionFactory {

    public ActionControlPair[] getAction(State state, boolean black, int topN);

}
