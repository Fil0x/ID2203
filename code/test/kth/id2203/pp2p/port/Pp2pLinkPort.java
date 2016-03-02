package kth.id2203.pp2p.port;

import kth.id2203.pp2p.event.P2PDeliver;
import kth.id2203.pp2p.event.P2PSend;
import se.sics.kompics.PortType;

public class Pp2pLinkPort extends PortType {
	{
		request(P2PSend.class);
		indication(P2PDeliver.class);
	}
}
