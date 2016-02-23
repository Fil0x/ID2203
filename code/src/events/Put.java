package events;


import se.sics.kompics.KompicsEvent;

public class Put implements KompicsEvent {
    // Put(key, value): contains the key and the value to be stored
    private int key, value;

    public Put(int key, int value) {
        this.key = key;
        this.value = value;
    }

    public int getKey() {
        return key;
    }

    public int getValue() {
        return value;
    }
}
