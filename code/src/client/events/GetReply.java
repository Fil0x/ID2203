package client.events;


import network.TAddress;
import pp2p.event.Pp2pDeliver;
import se.sics.kompics.KompicsEvent;
import staticdata.Grid;


public class GetReply extends Pp2pDeliver {
    private static final long serialVersionUID = -4817090990532012025L;
    private int key, value;
    private byte type;

    public GetReply(TAddress source, int key, int value) {
        super(source);

        this.key = key;
        this.value = value;
        this.type = Grid.GETREPLY;
    }

    public int getKey() {
        return key;
    }

    public int getValue() {
        return value;
    }

    public byte getType() {
        return type;
    }
}
