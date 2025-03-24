package State;

public interface ActionFactory {

    public Action getAction(State state, boolean black, int movesPlayed);

}
