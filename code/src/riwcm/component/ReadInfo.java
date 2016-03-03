package riwcm.component;


public class ReadInfo {
    private final Integer ts, wr, val, nodeid;

    public ReadInfo(Integer ts, Integer wr, Integer val, Integer nodeId) {
        this.ts = ts;
        this.wr = wr;
        this.val = val;
        this.nodeid = nodeId;
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

    public Integer getNodeid() {
        return nodeid;
    }
}
