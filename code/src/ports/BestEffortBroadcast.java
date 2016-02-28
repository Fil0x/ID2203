package ports;


import se.sics.kompics.PortType;

public class BestEffortBroadcast extends PortType {
    {
        indication(BebDeliver.class);
        request(BebBroadcast.class);
    }
}
