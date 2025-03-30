/*
package State;

// This class will combine the king and queen min distance with various weights to pick an ideal move.

import java.util.ArrayList;
import java.util.Collections;

public class TerritoryActionFactory implements ActionFactory {

    private static final double EPSILON = 0.2;   // Ensures f1 and f4 never drop to zero
    private static final double C = 6.0;         // Controls speed of game progress ramp-up
    private static final double K = 0.35;         // Fraction for f2 relative to f1 (and f3 to f4)


    public Action getAction(State state, boolean black, int movesPlayed) {
        int color = black ? State.BLACK : State.WHITE;
        ArrayList<Action> moves = Generator.availableMoves(state, color);

        Collections.shuffle(moves);
        if (moves.isEmpty()) {
            return null;
        }

        double currentControl = -Double.MAX_VALUE;
        Action bestAction = null;
        double[] fs = computeValues(movesPlayed);

        for (Action action : moves) {
            State actionOutcome = new State(state, action);
            Pair[] ourQueens = actionOutcome.getQueens(color);
            Pair[] theirQueens = actionOutcome.getQueens(black ? State.WHITE : State.BLACK);
            int[][] board = actionOutcome.getBoard();

            ArrayList<int[][]> queenReaches, kingReaches;
            queenReaches = new ArrayList<>();
            kingReaches = new ArrayList<>();

            queenReaches = MinDistanceActionFactory.minDistanceEvaluation(board, ourQueens, theirQueens);
            kingReaches = KingDistanceActionFactory.kingDistanceEvaluation(board, ourQueens, theirQueens);

            double refinedQueen = RefinedMinDistance.calculateRefinedMinDistance(queenReaches.get(0), queenReaches.get(1), board);
            double refinedKing = RefinedKingDistance.calculateRefinedKingDistance(kingReaches.get(0), kingReaches.get(1), board);

            double w = WeightCalculator.calculateWeight(queenReaches.get(0), queenReaches.get(1), board);

            double queenControl = calculateControl(board, queenReaches);
            double kingControl = calculateControl(board, kingReaches);

            double tempControl = fs[0]*w*queenControl + fs[1]*w*refinedQueen + fs[2]*w*refinedKing + fs[3]*w*kingControl;

            if (tempControl > currentControl) {currentControl = tempControl; bestAction = action;}

        }

        return bestAction;
    }

    public static double[] computeValues(int moves) {
        // Compute the game progress using a function that increases fast at first, then slows down.
        // The progress value will be between ε and 1 - ε.
        double progress = EPSILON + (1 - 2 * EPSILON) * (moves / (moves + C));

        // Determine normalization constant so that the total weight sums to 1.
        double A = 1.0 / (1.0 + K);

        // f1 is proportional to progress and f4 to the inverse of progress.
        double f1 = A * progress;
        double f4 = A * (1 - progress);

        // f2 and f3 are set as a fraction K of f1 and f4 respectively.
        double f2 = K * f1;
        double f3 = K * f4;

        return new double[]{f1, f2, f3, f4};
    }

    public double calculateControl(int[][] board, ArrayList<int[][]> reaches) {
        double totalControl = 0.0;
        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[0].length; c++) {
                if (reaches.get(0)[r][c] == Integer.MAX_VALUE) totalControl -= 1;
                else if (reaches.get(1)[r][c] == Integer.MAX_VALUE) totalControl += 1;
                else {
                    double numOurMoves = reaches.get(0)[r][c];
                    double numTheirMoves = reaches.get(1)[r][c];
                    if (numOurMoves == numTheirMoves) {continue;}
                    else if (numOurMoves > numTheirMoves) {totalControl += 1 - numOurMoves/numTheirMoves;}
                    else {totalControl -= 1 - numTheirMoves/numOurMoves;}
                }
            }
        }

        return totalControl;
    }

    @Override
    public Action[] getAction(State state, boolean black, int movesPlayed, int topN) {
        return new Action[0];
    }
}
*/
