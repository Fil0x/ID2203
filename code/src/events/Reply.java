package events;


import se.sics.kompics.KompicsEvent;

public class Reply implements KompicsEvent{
    private int key, value;

    public Reply(int key, int value) {
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
