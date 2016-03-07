package epfd.component;


import network.TAddress;
import pp2p.event.Pp2pDeliver;

public class HeartbeatReqMsg extends Pp2pDeliver {

    private static final long serialVersionUID = 490215700829591158L;

    private final Integer sequenceNumber;

    public HeartbeatReqMsg(TAddress source, Integer sequenceNumber) {
        super(source);
        this.sequenceNumber = sequenceNumber;
    }

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }
}
