package riwcm.component;


import beb.event.BEBDeliver;
import network.TAddress;

public class WriteArMessage extends BEBDeliver {

    private static final long serialVersionUID = 7119335186848859132L;

    private final Integer rid, ts, wr, val;

    public WriteArMessage(TAddress source, Integer rid, Integer ts, Integer wr, Integer val) {
        super(source);
        this.rid = rid;
        this.ts = ts;
        this.wr = wr;
        this.val = val;
    }

    public Integer getRid() {
        return rid;
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
