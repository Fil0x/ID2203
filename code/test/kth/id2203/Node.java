package kth.id2203;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kth.id2203.beb.event.BEBDeliver;
import kth.id2203.beb.event.BEBroadcast;
import kth.id2203.beb.port.BroadcastPort;
import kth.id2203.message.MessagePayload;
import kth.id2203.network.TAddress;
import kth.id2203.pp2p.event.P2PAckDeliver;
import kth.id2203.pp2p.event.P2PAckSend;
import kth.id2203.pp2p.port.Pp2pLinkPort;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;

public class Node extends ComponentDefinition {
	
	private static final Logger LOG = LoggerFactory.getLogger(Node.class);
	
	private Positive<BroadcastPort> broadcastPort = requires(BroadcastPort.class);
	private Positive<Pp2pLinkPort> p2pLinkPort = requires(Pp2pLinkPort.class);
		
	private TAddress self;
	
	private boolean isLeader;
	
	public Node(Init init) {
		LOG.info("Initialize node with self address: " + init.self.getIp() + ":" + init.self.getPort());
        this.self = init.self;
        this.isLeader = init.isLeader;
    }
	
	Handler<Start> startHandler = new Handler<Start>() {
		public void handle(Start event) {
			if (isLeader) {
				LOG.info("Triger Test Broadcast message from : " + self.getIp() + ":" + self.getPort());
				trigger(new BEBroadcast(new MessagePayload("TEST Message")), broadcastPort);
			}
		}
	};
	
	Handler<BEBDeliver> bebDeliverHandler = new Handler<BEBDeliver>() {
		public void handle(BEBDeliver event) {
			LOG.info("Got a Deliver Event at:" + self.getIp() + ":" + self.getPort() + " and sent ACK to: " + event.getFrom().getIp() + ":" + event.getFrom().getPort());
			if(event.getFrom().getPort() != self.getPort()) {
				trigger(new P2PAckSend(event.getFrom(), new MessagePayload("ACK")), p2pLinkPort);	
			}
		}
	};

	private Handler<P2PAckDeliver> handleP2PAckMessage = new Handler<P2PAckDeliver>() {

		@Override
		public void handle(P2PAckDeliver event) {
			LOG.info("Got Ack from " + event.getFrom().getIp() + ":" + event.getFrom().getPort());

		}

	};
	
	
	{
		subscribe(startHandler, control);
		subscribe(bebDeliverHandler, broadcastPort);
		subscribe(handleP2PAckMessage, p2pLinkPort);
	}
	
	public static class Init extends se.sics.kompics.Init<Node> {
        public final TAddress self;
        public final boolean isLeader;
        public Init(TAddress self, boolean isLeader) {
            this.self = self;
            this.isLeader = isLeader;
        }
    }
}
