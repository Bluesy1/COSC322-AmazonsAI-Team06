package State;

import java.util.Arrays;

public class AlphaBetaMinimax {

    public static Action getBestMove(ActionControlPair[] actions, int depth, boolean isBlack, int topN, ActionFactory actionFactory, State state) {
        if (actions == null || actions.length == 0) {
            return null;
        }

        int bestValue = Integer.MIN_VALUE;
        Action bestAction = null;

        for (ActionControlPair acp : actions) {
            State newState = new State(state, acp.getAction());
            int value = evaluateMove(actionFactory, depth - 1, topN, false, newState,
                    isBlack, Integer.MIN_VALUE, Integer.MAX_VALUE);
            if (value > bestValue) {
                bestValue = value;
                bestAction = acp.getAction();
            }
        }
        return bestAction;
    }

    private static int evaluateMove (ActionFactory actionFactory, int depth, int topN, boolean maximizingPlayer, State currentState, boolean maximizerIsBlack, int alpha, int beta) {
        if (depth == 0) {
            return currentState.evaluate(maximizerIsBlack);
        }

        ActionControlPair[] childPaths = actionFactory.getAction(currentState, maximizingPlayer ? maximizerIsBlack : !maximizerIsBlack, topN);

        if (childPaths == null || childPaths.length == 0) {
            return currentState.evaluate(maximizerIsBlack);
        }

        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;

            for (ActionControlPair child : childPaths) {
                State childState = new State(currentState, child.getAction());
                int eval = evaluateMove(actionFactory, depth-1, topN, false, childState, maximizerIsBlack, alpha, beta);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) {break;}
            }
            return maxEval;
        }

        else {
            int minEval = Integer.MAX_VALUE;

            for (ActionControlPair child : childPaths) {
                State childState = new State(currentState, child.getAction());
                int eval = evaluateMove(actionFactory, depth-1, topN, true, childState, maximizerIsBlack, alpha, beta);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) {break;}
            }
            return minEval;
        }
    }
}
