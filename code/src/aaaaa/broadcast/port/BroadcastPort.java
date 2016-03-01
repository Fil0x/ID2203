package aaaaa.broadcast.port;

import aaaaa.broadcast.event.Broadcast;
import aaaaa.broadcast.event.Deliver;
import se.sics.kompics.PortType;

public class BroadcastPort extends PortType {
	{
		request(Broadcast.class);
		indication(Deliver.class);
	}
}
