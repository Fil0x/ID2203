package epfd.port;


import se.sics.kompics.PortType;

public class EventuallyPerfectFailureDetector extends PortType {
    {
        indication(Suspect.class);
        indication(Restore.class);
    }
}
