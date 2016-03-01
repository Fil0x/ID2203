package beb.component;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import beb.event.BEBroadcast;
import beb.event.BEBDeliver;
import beb.event.BEBMessage;
import beb.port.BroadcastPort;
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

	private Negative<BroadcastPort> beb = provides(BroadcastPort.class);
	private Positive<Network> network = requires(Network.class);

	public BroadcastComponent(Init init) {
		this.self = init.self;
		this.all = init.all;
		log.info("Initiate broadcast component " + self.getIp() + ":" + self.getPort());
		
		subscribe(handleBroadcast, beb);
		subscribe(handleP2PMessage, network);
	}
	
	private Handler<BEBroadcast> handleBroadcast = new Handler<BEBroadcast>() {

		@Override
		public void handle(BEBroadcast event) {
			log.info("BEBroadcast BEBMessage from " + self.getIp() + ":" + self.getPort());
			for (TAddress dest : all) {
				trigger(new BEBMessage(self, dest), network);
			}
		}

	};

	private Handler<BEBMessage> handleP2PMessage = new Handler<BEBMessage>() {

		@Override
		public void handle(BEBMessage event) {
			log.info("BEBDeliver broadcast message at " + self.getIp() + ":" + self.getPort());
			trigger(new BEBDeliver(), beb);
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
