package epfd.port;


import epfd.event.Restore;
import epfd.event.Suspect;
import se.sics.kompics.PortType;

public class EventuallyPerfectFailureDetector extends PortType {
    {
        indication(Suspect.class);
        indication(Restore.class);
    }
}
