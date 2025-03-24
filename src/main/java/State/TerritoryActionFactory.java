package State;

// This class will combine the king and queen min distance with various weights to pick an ideal move.

import java.util.ArrayList;
import java.util.Collections;

public class TerritoryActionFactory implements ActionFactory {

    private static final int AVERAGE_MOVES = 30;
    private static final double STEEPNESS = 0.05;

    @Override
    public Action getAction(State state, boolean black, int movesPlayed) {
        int color = black ? State.BLACK : State.WHITE;
        ArrayList<Action> moves = Generator.availableMoves(state, color);

        Collections.shuffle(moves);
        if (moves.isEmpty()) {
            return null;
        }

        double currentControl = -Double.MAX_VALUE;
        Action bestAction = null;
        double[] fs = calculateWeights(movesPlayed);

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

    public double[] calculateWeights(int movesPlayed) {
        // Step 1: Calculate game progress using a logistic function
        double progress = 1.0 / (1.0 + Math.exp(-STEEPNESS * (movesPlayed - AVERAGE_MOVES)));

        // Step 2: Calculate f1 and f4 based on progress
        double a = 0.15; // Minimum value for f1 and f4
        double b = 0.5;  // Range of variation
        double f1 = a + b * progress;
        double f4 = a + b * (1.0 - progress);

        // Step 3: Calculate f2 and f3 by scaling with f1 and f4
        double k = 0.25; // Scaling factor to ensure f2 < f1, f3 < f4
        double f2 = k * f1;
        double f3 = k * f4;

        // Step 4: Return the weights
        return new double[] {f1, f2, f3, f4};
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
}
