package client.events;


import network.TAddress;
import pp2p.event.Pp2pDeliver;
import se.sics.kompics.KompicsEvent;
import staticdata.Grid;

public class PutRequest extends Pp2pDeliver {
    private static final long serialVersionUID = 7429988893396340783L;
    // PutRequest(key, value): contains the key and the value to be stored
    private int key, value;
    private byte type;

    public PutRequest(TAddress source, int key, int value) {
        super(source);

        this.key = key;
        this.value = value;
        this.type = Grid.PUTREQ;
    }

    public int getKey() {
        return key;
    }

    public int getValue() {
        return value;
    }

    @Override
    public byte getType() {
        return type;
    }
}
