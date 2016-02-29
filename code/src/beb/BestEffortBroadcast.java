package beb;


import beb.events.BebBroadcast;
import beb.events.BebDeliver;
import se.sics.kompics.PortType;

public class BestEffortBroadcast extends PortType {
    {
        indication(BebDeliver.class);
        request(BebBroadcast.class);
    }
}
