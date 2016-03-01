package pp2p.event;

import network.TAddress;
import network.THeader;
import network.TMessage;
import se.sics.kompics.network.Transport;

public class Pp2pMessage extends TMessage {

	public Pp2pMessage(TAddress src, TAddress dst) {
		super(src, dst, Transport.TCP);
	}

	public Pp2pMessage(THeader header) {
		super(header);
	}
}
