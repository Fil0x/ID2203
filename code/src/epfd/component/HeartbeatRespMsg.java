package epfd.component;


import network.TAddress;
import pp2p.event.Pp2pDeliver;

public class HeartbeatRespMsg extends Pp2pDeliver {

    private static final long serialVersionUID = -306649641819166808L;
    private final Integer sequenceNumber;

    protected HeartbeatRespMsg(TAddress source, Integer sequenceNumber) {
        super(source);
        this.sequenceNumber = sequenceNumber;
    }

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }
}
