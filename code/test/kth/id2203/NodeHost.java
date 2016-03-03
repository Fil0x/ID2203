package kth.id2203;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kth.id2203.beb.BroadcastComponent;
import kth.id2203.beb.port.BroadcastPort;
import kth.id2203.pp2p.Pp2pLink;
import kth.id2203.pp2p.port.Pp2pLinkPort;
import network.TAddress;
import se.sics.kompics.Channel;
import se.sics.kompics.Component;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.network.Network;
import se.sics.kompics.network.netty.NettyInit;
import se.sics.kompics.network.netty.NettyNetwork;
import staticdata.Grid;

public class NodeHost extends ComponentDefinition {

	private static final Logger LOG = LoggerFactory.getLogger(NodeHost.class);
	
	public NodeHost() {
		LOG.info("Initialize NodeHost");

		
		List<TAddress> all = Grid.getAllNodes();
		for(TAddress self : all) {
			Component network = create(NettyNetwork.class, new NettyInit(self));
			Component p2pLinkForBeb = create(Pp2pLink.class, new Pp2pLink.Init(self));
			connect(p2pLinkForBeb.getNegative(Network.class), network.getPositive(Network.class), Channel.TWO_WAY);
			
			Component boradcast = create(BroadcastComponent.class, new BroadcastComponent.Init(self, all));
			connect(boradcast.getNegative(Pp2pLinkPort.class), p2pLinkForBeb.getPositive(Pp2pLinkPort.class));
			
			Component node = create(Node.class, new Node.Init(self));
			connect(node.getNegative(BroadcastPort.class), boradcast.getPositive(BroadcastPort.class));


		}
	}
}
