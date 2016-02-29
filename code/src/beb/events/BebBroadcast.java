package beb.events;


import se.sics.kompics.KompicsEvent;

public class BebBroadcast implements KompicsEvent {

    private final BebDeliver deliverEvent;

    public BebBroadcast(BebDeliver deliverEvent) {
        this.deliverEvent = deliverEvent;
    }

    public BebDeliver getDeliverEvent() {
        return deliverEvent;
    }
}
