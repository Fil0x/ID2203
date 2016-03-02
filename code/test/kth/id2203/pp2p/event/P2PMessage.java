package kth.id2203.pp2p.event;

import network.TAddress;
import network.THeader;
import network.TMessage;
import se.sics.kompics.network.Transport;

public class P2PMessage extends TMessage {
	
	public P2PMessage(TAddress src, TAddress dst) {
		super(src, dst, Transport.TCP);
	}

	public P2PMessage(THeader header) {
		super(header);
	}
}
