package epfd.event;

import network.TAddress;
import se.sics.kompics.KompicsEvent;


public class Restore implements KompicsEvent {
    private final TAddress source;

    public Restore(TAddress source) {
        this.source = source;
    }

    public final TAddress getSource() {
        return source;
    }
}

