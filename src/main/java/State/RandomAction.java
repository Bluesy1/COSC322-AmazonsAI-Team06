package State;

import java.util.ArrayList;
import java.util.Random;

public class RandomAction implements ActionFactory {

    private final Random random;

    public RandomAction() {
        random = new Random();
    };

    @Override
    public ActionControlPair[] getAction(State state, boolean black, int topN) {
        int color = black ? State.BLACK : State.WHITE;
        ArrayList<Action> moves = Generator.availableMoves(state, color);
        ActionControlPair[] actions = new ActionControlPair[1];
        Action action = moves.get(random.nextInt(moves.size()));
        actions[0] = new ActionControlPair(action, 0);
        return actions;
    }
}

