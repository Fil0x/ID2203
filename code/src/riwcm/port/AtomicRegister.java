package riwcm.port;


import riwcm.event.ArReadRequest;
import riwcm.event.ArReadResponse;
import riwcm.event.ArWriteRequest;
import riwcm.event.ArWriteResponse;
import se.sics.kompics.PortType;

public class AtomicRegister extends PortType {
    {
        indication(ArReadResponse.class);
        indication(ArWriteResponse.class);
        request(ArReadRequest.class);
        request(ArWriteRequest.class);
    }
}
