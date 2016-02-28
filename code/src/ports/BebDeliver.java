package ports;

import network.VAddress;
import se.sics.kompics.Event;

import java.io.Serializable;

public class BebDeliver extends Event implements Serializable {

    private static final long serialVersionUID = 4088333329204792579L;

    private VAddress source;

    public BebDeliver(VAddress source) {
        this.source = source;
    }

    public VAddress getSource() {
        return source;
    }
}
