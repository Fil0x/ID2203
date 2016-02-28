package components;

import com.google.common.primitives.Ints;
import network.VAddress;
import se.sics.kompics.Channel;
import se.sics.kompics.Component;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Init;
import se.sics.kompics.network.Network;
import se.sics.kompics.network.netty.NettyInit;
import se.sics.kompics.network.netty.NettyNetwork;
import staticdata.Grid;

import java.util.List;

public class NodeParentHost extends ComponentDefinition {
	
	public NodeParentHost() {
		VAddress baseSelf = config().getValue("network.node", VAddress.class);
		List<VAddress> pi = Grid.getAllNodes(baseSelf);

		Component network = create(NettyNetwork.class, new NettyInit(baseSelf));
		Component nodeParent = create(NodeParent.class, Init.NONE);
		Component beb = create(BasicBroadcast.class, new BasicBroadcast.Init(baseSelf.withVirtual(Ints.toByteArray(1)), pi));

		connect(nodeParent.getNegative(Network.class), network.getPositive(Network.class), Channel.TWO_WAY);
		connect(beb.getNegative(Network.class), network.getPositive(Network.class), Channel.TWO_WAY);
	}

}
