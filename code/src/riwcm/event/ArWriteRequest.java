package riwcm.event;


import se.sics.kompics.KompicsEvent;

public class ArWriteRequest implements KompicsEvent {

    private final int key;
    private final int value;

    public ArWriteRequest(int key, int value) {
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
