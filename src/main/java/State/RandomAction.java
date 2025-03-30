/*
package State;

import java.util.ArrayList;
import java.util.Random;

public class RandomAction implements ActionFactory {

    private final Random random;

    public RandomAction() {
        random = new Random();
    };

    @Override
    public Action[] getAction(State state, boolean black, int movesPlayed, int topN) {
        int color = black ? State.BLACK : State.WHITE;
        ArrayList<Action> moves = Generator.availableMoves(state, color);
        Action[] actions = new Action[1];
        actions[0] = moves.get(random.nextInt(moves.size()));
        return actions;
    }
}
*/
