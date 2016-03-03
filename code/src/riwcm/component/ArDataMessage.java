package riwcm.component;


import network.TAddress;
import pp2p.event.Pp2pDeliver;

public class ArDataMessage extends Pp2pDeliver {

    private static final long serialVersionUID = -5706661489560207948L;

    private final Integer r, ts, wr, val;

    public ArDataMessage(TAddress source, Integer r, Integer ts, Integer wr, Integer val) {
        super(source);
        this.r = r;
        this.ts = ts;
        this.wr = wr;
        this.val = val;
    }

    public Integer getR() {
        return r;
    }

    public Integer getTs() {
        return ts;
    }

    public Integer getWr() {
        return wr;
    }

    public Integer getVal() {
        return val;
    }
}
