package State;

public class AlphaBetaMinimax {

    public static final long maxTimeMillis = 28L * 1000;
    private static long startTime;

    public static Action getBestMove(ActionControlPair[] actions, int depth, boolean isBlack, int topN, ActionFactory actionFactory, State state, int moveCounter) {
        if (actions == null || actions.length == 0) {
            return null;
        }

        int bestValue = Integer.MIN_VALUE;
        Action bestAction = null;

        startTime = System.currentTimeMillis();
        do {
            for (ActionControlPair acp : actions) {
                boolean outOfTime = startTime + maxTimeMillis < System.currentTimeMillis();
                if (outOfTime) {
                    System.out.println("!!!!!STOP TIME EXCEEDED, BAILING OUT!!!!!");
                    break;
                }
                State newState = new State(state, acp.getAction());
                int value = evaluateMove(actionFactory, depth - 1, topN, false, newState, isBlack, Integer.MIN_VALUE, Integer.MAX_VALUE);
                if (value > bestValue) {
                    bestValue = value;
                    bestAction = acp.getAction();
                }
            }
            depth++;
        } while (moveCounter + depth < 92 && startTime + maxTimeMillis < System.currentTimeMillis());
        return bestAction;
    }

    private static int evaluateMove(ActionFactory actionFactory, int depth, int topN, boolean maximizingPlayer, State currentState, boolean maximizerIsBlack, int alpha, int beta) {
        if (depth == 0) {
            return currentState.evaluate(maximizerIsBlack);
        }

        ActionControlPair[] childPaths = actionFactory.getAction(currentState, maximizingPlayer == maximizerIsBlack, topN);

        if (childPaths == null || childPaths.length == 0) {
            return currentState.evaluate(maximizerIsBlack);
        }

        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;

            for (ActionControlPair child : childPaths) {
                if (startTime + maxTimeMillis < System.currentTimeMillis()) {
                    System.out.println("!!!!!STOP TIME EXCEEDED, BAILING OUT!!!!!");
                    return maxEval;
                }
                State childState = new State(currentState, child.getAction());
                int eval = evaluateMove(actionFactory, depth - 1, topN, false, childState, maximizerIsBlack, alpha, beta);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) {
                    break;
                }
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;

            for (ActionControlPair child : childPaths) {
                if (startTime + maxTimeMillis < System.currentTimeMillis()) {
                    System.out.println("!!!!!STOP TIME EXCEEDED, BAILING OUT!!!!!");
                    return minEval;
                }
                State childState = new State(currentState, child.getAction());
                int eval = evaluateMove(actionFactory, depth - 1, topN, true, childState, maximizerIsBlack, alpha, beta);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) {
                    break;
                }
            }
            return minEval;
        }
    }
}
