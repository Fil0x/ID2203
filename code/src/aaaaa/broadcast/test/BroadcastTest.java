package aaaaa.broadcast.test;

import aaaaa.broadcast.event.Broadcast;
import aaaaa.broadcast.event.Deliver;
import aaaaa.broadcast.port.BroadcastPort;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;

public class BroadcastTest extends ComponentDefinition {

	Positive<BroadcastPort> broadcastPort = requires(BroadcastPort.class);

	boolean triggerTestMessage;

	public BroadcastTest(Init init) {
		System.out.println("Trigger Test Broad Cast message: " + init.triggerTestMessage);
		this.triggerTestMessage = init.triggerTestMessage;
	}

	Handler<Start> startHandler = new Handler<Start>() {
		public void handle(Start event) {
			if (triggerTestMessage) {
				System.out.println("Triger Test Broadcast message");
				trigger(new Broadcast(), broadcastPort);
			}
		}
	};

	Handler<Deliver> deliverHandler = new Handler<Deliver>() {
		public void handle(Deliver event) {
			System.out.println("Got a Deliver Event!");
		}
	};


	{
		subscribe(startHandler, control);
		subscribe(deliverHandler, broadcastPort);
	}

	public static class Init extends se.sics.kompics.Init<BroadcastTest> {
		public final boolean triggerTestMessage;

		public Init(boolean triggerTestMessage) {
			this.triggerTestMessage = triggerTestMessage;
		}
	}
}
