package pp2p.component;


import beb.event.BEBMessage;
import network.TAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pp2p.event.Pp2pDeliver;
import pp2p.event.Pp2pMessage;
import pp2p.event.Pp2pSend;
import pp2p.port.PerfectPointToPointLink;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.network.Network;

import java.util.List;

public class Pp2pLink extends ComponentDefinition {

    private static final Logger LOG = LoggerFactory.getLogger(Pp2pLink.class);

    private TAddress self;
    private List<TAddress> allNodes;

    private Negative<PerfectPointToPointLink> pp2p = provides(PerfectPointToPointLink.class);
    private Positive<Network> network = requires(Network.class);

    public Pp2pLink(Init init) {
        this.self = init.self;
        this.allNodes = init.all;

        LOG.info("Initiate pp2p component " + self.getIp() + ":" + self.getPort());

        subscribe(handlePp2pSend, pp2p);
        subscribe(handlePp2pDeliver, network);
    }

    private Handler<Pp2pSend> handlePp2pSend = new Handler<Pp2pSend>() {
        @Override
        public void handle(Pp2pSend event) {
            // Send a message over the network
            trigger(new Pp2pMessage(self, event.getDestination(), event.getDeliverEvent()), network);
        }
    };

    private Handler<Pp2pMessage> handlePp2pDeliver = new Handler<Pp2pMessage>() {
        @Override
        public void handle(Pp2pMessage event) {
            trigger(event.getDeliverEvent(), pp2p);
        }
    };

    public static class Init extends se.sics.kompics.Init<Pp2pLink> {
        public final TAddress self;
        public List<TAddress> all;

        public Init(TAddress self, List<TAddress> all) {
            this.self = self;
            this.all = all;
        }
    }
}
