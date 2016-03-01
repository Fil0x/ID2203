package pp2p.port;


import pp2p.event.Pp2pDeliver;
import pp2p.event.Pp2pSend;
import se.sics.kompics.PortType;

public final class PerfectPointToPointLink extends PortType {
    {
        indication(Pp2pDeliver.class);
        request(Pp2pSend.class);
    }
}
