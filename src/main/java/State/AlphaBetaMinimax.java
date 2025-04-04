package State;

import java.util.HashMap;

public class AlphaBetaMinimax {

    public static final long maxTimeMillis = 29L * 1000;
    private static final HashMap<Long, TranspositionEntry> transpositionTable = new HashMap<>();
    private static long stopTime;

    public static Action getBestMove(ActionControlPair[] actions, int depth, boolean isBlack, int topN, ActionFactory actionFactory, State state, int moveCounter) {
        if (actions == null || actions.length == 0) {
            return null;
        }

        int bestValue = Integer.MIN_VALUE;
        Action bestAction = null;
        int initialDepth = depth;
        stopTime = System.currentTimeMillis() + maxTimeMillis;
        do {
            if (stopTime < System.currentTimeMillis()) {
                System.out.println("!!!!!STOP TIME EXCEEDED, BAILING OUT!!!!!");
                break;
            }
            for (ActionControlPair acp : actions) {
                if (stopTime < System.currentTimeMillis()) {
                    System.out.println("!!!!!STOP TIME EXCEEDED, BAILING OUT!!!!!");
                    break;
                }
                State newState = new State(state, acp.getAction());
                long stateHash = newState.getZobristHash();
                int value = evaluateMove(actionFactory, depth - 1, topN, false, newState, isBlack, Integer.MIN_VALUE, Integer.MAX_VALUE, stateHash);
                if (value > bestValue) {
                    bestValue = value;
                    bestAction = acp.getAction();
                }
            }
            System.out.printf("Initial Depth: %d, Searched Depth: %d, Out of Time: %b.%n", initialDepth, depth++, stopTime < System.currentTimeMillis());
        } while (stopTime > System.currentTimeMillis() && moveCounter + depth < 92 && depth - initialDepth + 1 < 25 // At max do 25 iterations
        );
        return bestAction;
    }

    private static int evaluateMove(ActionFactory actionFactory, int depth, int topN, boolean maximizingPlayer, State currentState, boolean maximizerIsBlack, int alpha, int beta, long stateHash) {
        if (depth == 0) {
            return currentState.evaluate(maximizerIsBlack);
        }

        if (transpositionTable.containsKey(stateHash)) {
            TranspositionEntry entry = transpositionTable.get(stateHash);
            if (entry.depth >= depth) {
                if (entry.flag == TranspositionFlag.EXACT) return entry.value;
                if (entry.flag == TranspositionFlag.LOWER_BOUND && entry.value > alpha) alpha = entry.value;
                if (entry.flag == TranspositionFlag.UPPER_BOUND && entry.value < beta) beta = entry.value;
                if (alpha >= beta) return entry.value;
            }
        }

        ActionControlPair[] childPaths = actionFactory.getAction(currentState, maximizingPlayer == maximizerIsBlack, topN);
        if (childPaths == null || childPaths.length == 0) {
            return currentState.evaluate(maximizerIsBlack);
        }

        int originalAlpha = alpha;
        int bestEval = maximizingPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (ActionControlPair child : childPaths) {
            if (stopTime < System.currentTimeMillis()) {
                return bestEval;
            }
            State childState = new State(currentState, child.getAction());
            long childHash = childState.getZobristHash();
            int eval = evaluateMove(actionFactory, depth - 1, topN, !maximizingPlayer, childState, maximizerIsBlack, alpha, beta, childHash);

            if (maximizingPlayer) {
                bestEval = Math.max(bestEval, eval);
                alpha = Math.max(alpha, eval);
            } else {
                bestEval = Math.min(bestEval, eval);
                beta = Math.min(beta, eval);
            }

            if (beta <= alpha) break;
        }

        TranspositionFlag flag;
        if (bestEval <= originalAlpha) flag = TranspositionFlag.UPPER_BOUND;
        else if (bestEval >= beta) flag = TranspositionFlag.LOWER_BOUND;
        else flag = TranspositionFlag.EXACT;

        transpositionTable.put(stateHash, new TranspositionEntry(bestEval, depth, flag));

        return bestEval;
    }

    private enum TranspositionFlag {
        EXACT, LOWER_BOUND, UPPER_BOUND
    }

    private static class TranspositionEntry {
        int value, depth;
        TranspositionFlag flag;

        TranspositionEntry(int value, int depth, TranspositionFlag flag) {
            this.value = value;
            this.depth = depth;
            this.flag = flag;
        }
    }
}
