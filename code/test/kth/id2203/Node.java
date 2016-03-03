package kth.id2203;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kth.id2203.beb.event.BEBDeliver;
import kth.id2203.beb.event.BEBroadcast;
import kth.id2203.beb.port.BroadcastPort;
import kth.id2203.pp2p.event.P2PDeliver;
import kth.id2203.pp2p.event.P2PSend;
import kth.id2203.pp2p.port.Pp2pLinkPort;
import network.TAddress;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;

public class Node extends ComponentDefinition {
	
	private static final Logger LOG = LoggerFactory.getLogger(Node.class);
	
	private Positive<BroadcastPort> broadcastPort = requires(BroadcastPort.class);
		
	private TAddress self;
	
	public Node(Init init) {
		LOG.info("Initialize node with self address: " + init.self.getIp() + ":" + init.self.getPort());
        this.self = init.self;
    }
	
//	Handler<Start> startHandler = new Handler<Start>() {
//		public void handle(Start event) {
//			if (isLeader) {
//				System.out.println("Triger Test Broadcast message");
//				trigger(new BEBroadcast(), broadcastPort);
//			}
//		}
//	};
	
	Handler<BEBDeliver> bebDeliverHandler = new Handler<BEBDeliver>() {
		public void handle(BEBDeliver event) {
			System.out.println("Got a Deliver Event at:" + self.getIp() + ":" + self.getPort() + " and sent ACK to: " + event.getFrom().getIp() + ":" + event.getFrom().getPort());
				
		}
	};
	
	
	{
//		subscribe(startHandler, control);
		subscribe(bebDeliverHandler, broadcastPort);
	}
	
	public static class Init extends se.sics.kompics.Init<Node> {
        public final TAddress self;
        
        public Init(TAddress self) {
            this.self = self;
        }
    }
}
