package State;

import java.util.Arrays;

public class AlphaBetaMinimax {

    public static Action getBestMove(ActionControlPair[] actions, int depth, boolean isBlack, int topN, ActionFactory actionFactory, State state) {
        for (int i = 0; i < actions.length; i++) {
            actions[i].setControl(evaluateMove(actionFactory, depth, topN, true, actions[i].getControl(), state, isBlack));
        }

        int mostControl = Integer.MIN_VALUE;
        int index = 0;
        for (int i = 0; i < actions.length; i++) {
            if (actions[i].getControl() > mostControl) {
                mostControl = actions[i].getControl();
                index = i;
            }
        }

        return actions[index].getAction();
    }

    private static int evaluateMove (ActionFactory actionFactory, int depth, int topN, boolean maximizingPlayer, int currentEval, State currentState, boolean maximizerIsBlack) {
        if (depth == 0) {
            return currentEval;
        }

        if (maximizingPlayer) {
            ActionControlPair[] childPaths = actionFactory.getAction(currentState, maximizerIsBlack, topN);
            if (childPaths == null) { return currentEval;}
            int index = 0;
            int[] eval = new int[childPaths.length];

            for (ActionControlPair child : childPaths) {
                State childState = new State(currentState, child.getAction());
                eval[index] = evaluateMove(actionFactory, depth-1, topN, false, child.getControl(), childState, maximizerIsBlack);
                index++;
            }
            return Arrays.stream(eval).max().getAsInt();
        }

        else {
            ActionControlPair[] childPaths = actionFactory.getAction(currentState, !maximizerIsBlack, topN);
            if (childPaths == null) { return currentEval;}
            int index = 0;
            int[] eval = new int[childPaths.length];

            for (ActionControlPair child : childPaths) {
                State childState = new State(currentState, child.getAction());
                eval[index] = evaluateMove(actionFactory, depth-1, topN, true, child.getControl(), childState, maximizerIsBlack);
                index++;
            }
            return Arrays.stream(eval).min().getAsInt();
        }
    }
}
