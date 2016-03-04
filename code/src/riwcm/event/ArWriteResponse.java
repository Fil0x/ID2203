package riwcm.event;


import se.sics.kompics.KompicsEvent;

public class ArWriteResponse implements KompicsEvent {
    private final int key;

    public ArWriteResponse(int key) {
        this.key = key;
    }

    public int getKey() {
        return key;
    }
}
