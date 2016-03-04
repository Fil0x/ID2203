package riwcm.event;


import se.sics.kompics.KompicsEvent;

public class ArReadResponse implements KompicsEvent {

    private final int key;
    private final int value;

    public ArReadResponse(int key, int value) {
        super();

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
