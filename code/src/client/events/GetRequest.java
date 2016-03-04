package client.events;

import network.TAddress;
import pp2p.event.Pp2pDeliver;
import se.sics.kompics.KompicsEvent;
import staticdata.Grid;


public class GetRequest extends Pp2pDeliver {
    private static final long serialVersionUID = 8335028469218096522L;
    // GetRequest(key): send a request to retrieve a value based on a key
    private int key;
    private byte type;

    public GetRequest(TAddress source, int key) {
        super(source);

        this.key = key;
        this.type = Grid.GETREQ;
    }

    public int getKey() {
        return key;
    }

    @Override
    public byte getType() {
        return type;
    }
}
