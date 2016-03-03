package beb.event;

import se.sics.kompics.KompicsEvent;

public class BEBroadcast implements KompicsEvent {
    private final BEBDeliver deliverEvent;

    public BEBroadcast(BEBDeliver deliverEvent) {
        this.deliverEvent = deliverEvent;
    }

    public BEBDeliver getDeliverEvent() {
        return deliverEvent;
    }
}
