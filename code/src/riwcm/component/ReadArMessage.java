package riwcm.component;


import beb.event.BEBDeliver;
import network.TAddress;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;

public class ReadArMessage extends BEBDeliver {

    private static final long serialVersionUID = 1948787315093788827L;

    private final int rid;

    public ReadArMessage(TAddress source, int rid) {
        super(source);
        this.rid = rid;
    }

    public int getRid() {
        return rid;
    }
}
