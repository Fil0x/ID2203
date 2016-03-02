package kth.id2203.pp2p;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kth.id2203.pp2p.event.P2PDeliver;
import kth.id2203.pp2p.event.P2PMessage;
import kth.id2203.pp2p.event.P2PSend;
import kth.id2203.pp2p.port.Pp2pLinkPort;
import network.TAddress;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.network.Network;
import se.sics.kompics.timer.Timer;

public class Pp2pLink extends ComponentDefinition {

	private static final Logger log = LoggerFactory.getLogger(Pp2pLink.class);
	
	private TAddress self;
	
	private Positive<Network> network = requires(Network.class);
	private Positive<Timer> timer = requires(Timer.class);
	private Negative<Pp2pLinkPort> p2plink = provides(Pp2pLinkPort.class);
	
	
	public Pp2pLink(Init init) {
		this.self = init.self;
		log.info("Initiate Pp2pLink component " + self.getIp() + ":" + self.getPort());
		
		subscribe(handleSend, p2plink);
		subscribe(handleDeliver, network);
	}
	
	
	private Handler<P2PSend> handleSend = new Handler<P2PSend>() {

		@Override
		public void handle(P2PSend event) {
			log.info("Trigger P2PSend from " + self.getIp() + ":" + self.getPort());
			trigger(new P2PMessage(self, event.getTo()), network);
		}
		
	};
	
	private Handler<P2PMessage> handleDeliver = new Handler<P2PMessage>() {
		@Override
		public void handle(P2PMessage event) {
			log.info("P2PMessage deliver at " + self.getIp() + ":" + self.getPort());
			trigger(new P2PDeliver(event.getSource()), p2plink);
		}
		
	};
	
	public static class Init extends se.sics.kompics.Init<Pp2pLink> {
		public final TAddress self;
		
		public Init(TAddress self) {
			this.self = self;
		}
	}
}
