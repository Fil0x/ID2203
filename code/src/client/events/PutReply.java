package client.events;


import network.TAddress;
import pp2p.event.Pp2pDeliver;
import se.sics.kompics.KompicsEvent;
import staticdata.Grid;

public class PutReply extends Pp2pDeliver{
    private static final long serialVersionUID = 8777664112768146746L;
    private byte type;

    public PutReply(TAddress source) {
        super(source);

        this.type = Grid.PUTREPLY;
    }

    public byte getType() {
        return type;
    }
}
