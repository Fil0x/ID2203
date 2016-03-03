package pp2p.event;

import network.TAddress;
import network.THeader;
import network.TMessage;
import se.sics.kompics.network.Transport;

public class Pp2pMessage extends TMessage {

    private final Pp2pDeliver deliverEvent;

	public Pp2pMessage(TAddress src, TAddress dst, Pp2pDeliver event) {
		super(src, dst, Transport.TCP);
        this.deliverEvent = event;
	}

    public Pp2pDeliver getDeliverEvent() {
        return deliverEvent;
    }
}
