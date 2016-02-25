package events;

import se.sics.kompics.KompicsEvent;

public class Get implements KompicsEvent {
    // Get(key): send a request to retrieve a value based on a key
    private int key;
    private boolean toForward;

    public Get(int key) {
        this.key = key;
        this.toForward = true;
    }

    public int getKey() {
        return key;
    }

    public boolean toForward() {
        return toForward;
    }

    public void setToForward(boolean toForward) {
        this.toForward = toForward;
    }
}
