package State;

public class Utils {

    public static boolean validateMove(State state, Action action) {

        Pair oldQueen = action.origin;
        Pair newQueen = action.destination;
        Pair arrow = action.arrow;
        // Validate that the positions are in bound
        if (!(oldQueen.isInBounds() && newQueen.isInBounds() && arrow.isInBounds())) {
            return false;
        }

        // Confirm that the old and new queen positions are different, as are the new position and the arrow position
        if (oldQueen.equals(newQueen) || newQueen.equals(arrow)) {
            return false;
        }

        Pair ray = calculateRay(oldQueen, newQueen);
        if (ray == null) {
            return false;
        }
        Pair testPos = oldQueen;
        do {
            testPos = testPos.add(ray);
            try {
                if (state.getPos(testPos) != 0) {
                    return false;
                }
            } catch (IndexOutOfBoundsException e) {
                System.out.printf("Error (queencheck): %s\n", e.getMessage());
                System.out.printf("Origin: %s\n", oldQueen);
                System.out.printf("Destination: %s\n", newQueen);
                System.out.printf("Ray: %s\n", ray);
                System.out.printf("TestPos: %s\n", testPos);
                throw e;
            }
        } while (!testPos.equals(newQueen));

        // Confirm that the queen can shoot the arrow to a valid spot

        if (oldQueen.equals(arrow)) {
            return true; // We know we could move from that spot, so the arrow can land there
        }

        ray = calculateRay(newQueen, arrow);
        if (ray == null) {
            return false;
        }

        testPos = newQueen;
        do {
            testPos = testPos.add(ray);
            try {
                if ((state.getPos(testPos) != 0) && !testPos.equals(oldQueen)) {
                    return false;
                }
            } catch (IndexOutOfBoundsException e) {
                System.out.printf("Error (arrowcheck): %s\n", e.getMessage());
                System.out.printf("Origin: %s\n", newQueen);
                System.out.printf("Destination: %s\n", arrow);
                System.out.printf("Ray: %s\n", ray);
                System.out.printf("TestPos: %s\n", testPos);
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
