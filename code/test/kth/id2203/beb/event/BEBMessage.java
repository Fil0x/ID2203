package kth.id2203.beb.event;

import network.TAddress;
import network.THeader;
import network.TMessage;
import se.sics.kompics.network.Transport;

public class BEBMessage extends TMessage {
	
	public BEBMessage(TAddress src, TAddress dst) {
		super(src, dst, Transport.TCP);
	}

	public BEBMessage(THeader header) {
		super(header);
	}
}
