package State;

public class Utils {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_BOLD = "\u001B[1m";

    public static boolean validateMove(State state, Action action, int color, boolean verbose) {

        Pair oldQueen = action.origin;
        Pair newQueen = action.destination;
        Pair arrow = action.arrow;

        if (color != State.BLACK && color != State.WHITE) {
            throw new IllegalArgumentException(color + " is an invalid color");
        }
        int oldQueenTileData = state.getPos(oldQueen);
        if (oldQueenTileData != color) {
            if (verbose) {
                System.out.println(
                        ANSI_RESET + ANSI_RED + ANSI_BOLD
                                + "Error: Move Invalid (No Queen or wrong queen at move origin "
                                + oldQueen + "), got "
                                + oldQueenTileData + ", expected " + color + ":" + ANSI_RESET
                );
                System.out.println(state.boardToStringNumbers());
            }
            return false;
        }

        if (state.getPos(newQueen) != 0) {
            if (verbose) {
                System.out.println(ANSI_RESET + ANSI_RED + ANSI_BOLD + "Error: Move Invalid (Queen Destination Occupied)." + ANSI_RESET);
                System.out.println(state.boardToString());
            }
            return false;
        }

        if (state.getPos(arrow) != 0 && !oldQueen.equals(arrow)) {
            if (verbose) {
                System.out.println(ANSI_RESET + ANSI_RED + ANSI_BOLD + "Error: Move Invalid (Arrow Destination Occupied)." + ANSI_RESET);
                System.out.println(state.boardToString());
            }
            return false;
        }

        // Validate that the positions are in bound
        if (!(oldQueen.isInBounds() && newQueen.isInBounds() && arrow.isInBounds())) {
            if (verbose)
                System.out.printf("%sError: Move Invalid (Position OOB):%nOld Queen: %s%nNew Queen: %s%nArrow: %s%n%s%n",
                    ANSI_RESET + ANSI_RED + ANSI_BOLD,
                    oldQueen,
                    newQueen,
                    arrow,
                    ANSI_RESET);
            return false;
        }

        // Confirm that the old and new queen positions are different, as are the new position and the arrow position
        if (oldQueen.equals(newQueen) || newQueen.equals(arrow)) {
            if (verbose)
                System.out.printf("%sError: Move Invalid (Queen Stationary or shooting self):%nOld Queen: %s%nNew Queen: %s%nArrow: %s%n%s%n",
                    ANSI_RESET + ANSI_RED + ANSI_BOLD,
                    oldQueen,
                    newQueen,
                    arrow,
                    ANSI_RESET);
            return false;
        }

        Pair ray = calculateRay(oldQueen, newQueen);
        if (ray == null) {
            if (verbose)
                System.out.printf("%sError: Move Invalid (Queen Move Ray cast Failed):%nOld Queen: %s%nNew Queen: %s%s%n",
                    ANSI_RESET + ANSI_RED + ANSI_BOLD,
                    oldQueen,
                    newQueen,
                    ANSI_RESET);
            return false;
        }
        Pair testPos = oldQueen;
        do {
            testPos = testPos.add(ray);
            try {
                if (state.getPos(testPos) != 0) {
                    if (verbose)
                        System.out.printf("%sError: Move Invalid (Queen path check Failed):%nOld Queen: %s%nNew Queen: %s%nFailed At: %s%s%n",
                            ANSI_RESET + ANSI_RED + ANSI_BOLD,
                            oldQueen,
                            newQueen,
                            testPos,
                            ANSI_RESET);
                    return false;
                }
            } catch (IndexOutOfBoundsException e) {
                System.out.print(ANSI_RESET + ANSI_RED + ANSI_BOLD);
                System.out.printf("Error (queen check): %s\n", e.getMessage());
                System.out.printf("Origin: %s\n", oldQueen);
                System.out.printf("Destination: %s\n", newQueen);
                System.out.printf("Ray: %s\n", ray);
                System.out.printf("TestPos: %s\n", testPos);
                System.out.print(ANSI_RESET);
                throw e;
            }
        } while (!testPos.equals(newQueen));

        // Confirm that the queen can shoot the arrow to a valid spot

        if (oldQueen.equals(arrow)) {
            return true; // We know we could move from that spot, so the arrow can land there
        }

        ray = calculateRay(newQueen, arrow);
        if (ray == null) {
            if (verbose)
                System.out.printf("%sError: Move Invalid (Arrow Ray cast Failed):%nOld Queen: %s%nNew Queen: %s%s%n",
                    ANSI_RESET + ANSI_RED + ANSI_BOLD,
                    newQueen,
                    arrow,
                    ANSI_RESET);
            return false;
        }

        testPos = newQueen;
        do {
            testPos = testPos.add(ray);
            try {
                if ((state.getPos(testPos) != 0) && !testPos.equals(oldQueen)) {
                    if (verbose)
                        System.out.printf("%sError: Move Invalid (Arrow path check Failed):%nNew Queen: %s%nArrow: %s%nFailed At: %s%s%n",
                            ANSI_RESET + ANSI_RED + ANSI_BOLD,
                            newQueen,
                            arrow,
                            testPos,
                            ANSI_RESET);
                    return false;
                }
            } catch (IndexOutOfBoundsException e) {
                System.out.print(ANSI_RESET + ANSI_RED + ANSI_BOLD);
                System.out.printf("Error (arrow check): %s\n", e.getMessage());
                System.out.printf("Origin: %s\n", newQueen);
                System.out.printf("Destination: %s\n", arrow);
                System.out.printf("Ray: %s\n", ray);
                System.out.printf("TestPos: %s\n", testPos);
                System.out.print(ANSI_RESET);
                throw e;
            }
        } while (!testPos.equals(arrow));

        return true;
    }

    private static Pair calculateRay(Pair origin, Pair destination) {
        int dx = origin.x - destination.x;
        int dy = origin.y - destination.y;
        if (dx == 0) {
            dy = dy > 0 ? -1 : 1;
        } else if (dy == 0) {
            dx = dx > 0 ? -1 : 1;
        } else {
            if (Math.abs(dx) != Math.abs(dy)) {
                return null; // Not a valid direction
            }
            dx = dx < 0 ? 1 : -1;
            dy = dy < 0 ? 1 : -1;
        }

        return new Pair(dx, dy);
    }

}
