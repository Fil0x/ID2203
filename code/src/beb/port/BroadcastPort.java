package beb.port;

import beb.event.Broadcast;
import beb.event.Deliver;
import se.sics.kompics.PortType;

public class BroadcastPort extends PortType {
	{
		request(Broadcast.class);
		indication(Deliver.class);
	}
}
