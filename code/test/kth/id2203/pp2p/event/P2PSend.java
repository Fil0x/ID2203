package kth.id2203.pp2p.event;

import network.TAddress;
import se.sics.kompics.KompicsEvent;

public class P2PSend implements KompicsEvent {
	private final TAddress to;
	
	public P2PSend(TAddress to) {
		this.to = to;
	}

	public TAddress getTo() {
		return to;
	}
	
}
