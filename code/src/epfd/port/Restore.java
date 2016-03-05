package epfd.port;

import network.TAddress;
import se.sics.kompics.KompicsEvent;


public class Restore implements KompicsEvent {
    public class Suspect implements KompicsEvent {
        private final TAddress source;

        public Suspect(TAddress source) {
            this.source = source;
        }

        public final TAddress getSource() {
            return source;
        }
    }
}
