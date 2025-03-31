package State;

public class ActionControlPair implements Comparable<ActionControlPair> {
    private Action action;
    private int control;

    public ActionControlPair(Action action, int Control) {
        this.action = action;
        this.control = Control;
    }

    @Override
    public int compareTo(ActionControlPair o) {
        return Integer.compare(o.control, control);
    }

    public Action getAction() {
        return action;
    }

    public int getControl() {
        return control;
    }

    public void setControl(int Control) {
        this.control = Control;
    }

    public void setAction(Action action) {
        this.action = action;
    }

}
