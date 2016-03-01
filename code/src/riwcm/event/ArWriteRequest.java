package riwcm.event;


import se.sics.kompics.KompicsEvent;

public class ArWriteRequest implements KompicsEvent {

    private final int value;

    public ArWriteRequest(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
