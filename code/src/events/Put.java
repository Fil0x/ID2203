package events;


import se.sics.kompics.KompicsEvent;

public class Put implements KompicsEvent {
    // Put(key, value): contains the key and the value to be stored
    private int key, value;
    private boolean toForward;

    public Put(int key, int value) {
        this.key = key;
        this.value = value;
        this.toForward = true;
    }

    public int getKey() {
        return key;
    }

    public int getValue() {
        return value;
    }

    public boolean toForward() {
        return toForward;
    }

    public void setToForward(boolean toForward) {
        this.toForward = toForward;
    }
}
