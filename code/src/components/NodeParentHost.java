package components;

import network.VAddress;
import se.sics.kompics.Channel;
import se.sics.kompics.Component;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Init;
import se.sics.kompics.network.Network;
import se.sics.kompics.network.netty.NettyInit;
import se.sics.kompics.network.netty.NettyNetwork;

public class NodeParentHost extends ComponentDefinition {
	
	public NodeParentHost() {
		VAddress baseSelf = config().getValue("network.node", VAddress.class);
		Component network = create(NettyNetwork.class, new NettyInit(baseSelf));
		Component nodeParent = create(NodeParent.class, Init.NONE);
		
		connect(nodeParent.getNegative(Network.class), network.getPositive(Network.class), Channel.TWO_WAY);
	}

}
