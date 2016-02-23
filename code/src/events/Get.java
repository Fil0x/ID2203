package events;

import se.sics.kompics.KompicsEvent;

public class Get implements KompicsEvent {
    // Get(key): send a request to retrieve a value based on a key
    private int key;

    public Get(int key) {
        this.key = key;
    }

    public int getKey() {
        return key;
    }
}
