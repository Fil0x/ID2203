package beb.port;

import beb.event.BEBroadcast;
import beb.event.BEBDeliver;
import se.sics.kompics.PortType;

public class BroadcastPort extends PortType {
	{
		request(BEBroadcast.class);
		indication(BEBDeliver.class);
	}
}
