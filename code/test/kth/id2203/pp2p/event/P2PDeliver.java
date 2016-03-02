package kth.id2203.pp2p.event;

import network.TAddress;
import network.THeader;
import network.TMessage;
import se.sics.kompics.KompicsEvent;
import se.sics.kompics.network.Transport;

public class P2PDeliver implements KompicsEvent {
	private final TAddress from;
	
	public P2PDeliver(TAddress from) {
		this.from = from;
	}

	public TAddress getFrom() {
		return from;
	}
}
