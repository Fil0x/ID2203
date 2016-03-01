package riwcm.event;


import se.sics.kompics.KompicsEvent;

public class ArReadResponse implements KompicsEvent {

    private final int value;

    public ArReadResponse(int value) {
        super();
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
