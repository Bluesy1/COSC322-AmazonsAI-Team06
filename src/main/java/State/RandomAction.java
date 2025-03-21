package State;

import java.util.ArrayList;
import java.util.Random;

public class RandomAction implements ActionFactory {

    private final Random random;

    public RandomAction() {
        random = new Random();
    };

    public Action getAction(State state, boolean black) {
        int color = black ? State.BLACK : State.WHITE;
        ArrayList<Action> moves = Generator.availableMoves(state, color);
        return moves.get(random.nextInt(moves.size()));
    }
}
