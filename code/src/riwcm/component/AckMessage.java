package riwcm.component;


import network.TAddress;
import pp2p.event.Pp2pDeliver;

public class AckMessage extends Pp2pDeliver {

    private static final long serialVersionUID = -2082066281232086167L;

    private final Integer key, r;

    public AckMessage(TAddress source, Integer key, Integer r) {
        super(source);

        this.key = key;
        this.r = r;
    }

    public Integer getKey() {
        return key;
    }

    public Integer getR() {
        return r;
    }
}
