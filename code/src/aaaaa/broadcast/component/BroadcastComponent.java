package aaaaa.broadcast.component;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aaaaa.broadcast.event.Broadcast;
import aaaaa.broadcast.event.Deliver;
import aaaaa.broadcast.event.P2PMessage;
import aaaaa.broadcast.port.BroadcastPort;
import network.TAddress;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.network.Network;

public class BroadcastComponent extends ComponentDefinition {

	private static final Logger log = LoggerFactory.getLogger(BroadcastComponent.class);

	private TAddress self;

	private List<TAddress> all;

	private Negative<BroadcastPort> broadcastPort = provides(BroadcastPort.class);
	
	private Positive<Network> network = requires(Network.class);

	public BroadcastComponent(Init init) {
		this.self = init.self;
		this.all = init.all;
		log.info("Initiate broadcast component " + self.getIp() + ":" + self.getPort());
		
		subscribe(handleBroadcast, broadcastPort);
		subscribe(handleP2PMessage, network);
	}
	
	private Handler<Broadcast> handleBroadcast = new Handler<Broadcast>() {

		@Override
		public void handle(Broadcast event) {
			log.info("Broadcast P2PMessage from " + self.getIp() + ":" + self.getPort());
			for (TAddress dest : all) {
				trigger(new P2PMessage(self, dest), network);
				
			}
		}

	};

	private Handler<P2PMessage> handleP2PMessage = new Handler<P2PMessage>() {

		@Override
		public void handle(P2PMessage event) {
			log.info("Deliver broadcast message at " + self.getIp() + ":" + self.getPort());
			trigger(new Deliver(), broadcastPort);
		}

	};


	public static class Init extends se.sics.kompics.Init<BroadcastComponent> {
		public final TAddress self;
		public List<TAddress> all;

		public Init(TAddress self, List<TAddress> all) {
			this.self = self;
			this.all = all;
		}
	}
}
