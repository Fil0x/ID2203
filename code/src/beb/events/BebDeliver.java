package beb.events;

import network.TAddress;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;

public class BebDeliver implements Serializable, KompicsEvent {

    private static final long serialVersionUID = 4088333329204792579L;

    private TAddress source;

    public BebDeliver(TAddress source) {
        this.source = source;
    }

    public TAddress getSource() {
        return source;
    }
}
