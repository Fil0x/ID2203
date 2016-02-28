package components;


import network.VAddress;
import ports.BebDeliver;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;

public class BebDataMessage implements Serializable, KompicsEvent {
    private static final long serialVersionUID = 9183185042302932366L;

    private VAddress source;
    private BebDeliver data;

    protected BebDataMessage(VAddress source, BebDeliver data) {
        this.data = data;
    }

    public BebDeliver getData() {
        return data;
    }

    public VAddress getSource() {
        return source;
    }
}
