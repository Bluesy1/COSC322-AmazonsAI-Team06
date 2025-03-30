package State;

public class ActionControlPair implements Comparable<ActionControlPair> {
    private Action action;
    private int Control;

    public ActionControlPair(Action action, int Control) {
        this.action = action;
        this.Control = Control;
    }

    @Override
    public int compareTo(ActionControlPair o) {
        return Integer.compare(o.Control, Control);
    }

    public Action getAction() {
        return action;
    }

    public int getControl() {
        return Control;
    }

    public void setControl(int Control) {
        this.Control = Control;
    }

    public void setAction(Action action) {
        this.action = action;
    }

}
