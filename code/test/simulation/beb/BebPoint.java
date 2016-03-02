package simulation.beb;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import beb.event.BEBDeliver;
import beb.event.BEBroadcast;
import beb.port.BroadcastPort;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;
import se.sics.kompics.simulator.util.GlobalView;
import simulation.pp2p.LinkPoint;

public class BebPoint extends ComponentDefinition {

	private static final Logger log = LoggerFactory.getLogger(BebPoint.class);
	
	Positive<BroadcastPort> broadcastPort = requires(BroadcastPort.class);

	boolean isBroadcaster;

	public BebPoint(Init init) {
		log.info("Init BebPoint : " + init.isBroadcaster);
		this.isBroadcaster = init.isBroadcaster;
	}

	Handler<Start> startHandler = new Handler<Start>() {
		public void handle(Start event) {
			if (isBroadcaster) {
				log.info("Triger Broadcast message");
				trigger(new BEBroadcast(), broadcastPort);
			}
		}
	};

	Handler<BEBDeliver> deliverHandler = new Handler<BEBDeliver>() {
		public void handle(BEBDeliver event) {
			log.info("Got a Deliver Event ");
			
			GlobalView gv = config().getValue("simulation.globalview", GlobalView.class);
	        gv.setValue("simulation.beb.numreceived", gv.getValue("simulation.beb.numreceived", Integer.class) + 1);
		}
	};


	{
		subscribe(startHandler, control);
		subscribe(deliverHandler, broadcastPort);
	}

	public static class Init extends se.sics.kompics.Init<BebPoint> {
		public final boolean isBroadcaster;

		public Init(boolean isBroadcaster) {
			this.isBroadcaster = isBroadcaster;
		}
	}
}