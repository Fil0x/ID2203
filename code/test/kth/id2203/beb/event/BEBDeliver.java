package kth.id2203.beb.event;

import network.TAddress;
import se.sics.kompics.KompicsEvent;

public class BEBDeliver implements KompicsEvent {
	private final TAddress from;
	
	public BEBDeliver(TAddress from) {
		this.from = from;
	}

	public TAddress getFrom() {
		return from;
	}
}

