package beb.event;

import network.TAddress;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;

public class BEBDeliver implements KompicsEvent, Serializable {

    private static final long serialVersionUID = -5956547359117254696L;

    private TAddress source;

    public BEBDeliver() {}

    public BEBDeliver(TAddress source) {
        this.source = source;
    }

    public TAddress getSource() {
        return source;
    }
}

