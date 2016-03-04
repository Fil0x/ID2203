package pp2p.event;


import network.TAddress;
import se.sics.kompics.KompicsEvent;
import java.io.Serializable;

public class Pp2pDeliver implements Serializable, KompicsEvent {

    private static final long serialVersionUID = -1565611742901069512L;

    private TAddress source;

    public Pp2pDeliver(TAddress source) {
        this.source = source;
    }

    public TAddress getSource() {
        return source;
    }

    public byte getType() { return (byte) 0;}
}