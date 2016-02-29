package components;

import network.TAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.sics.kompics.*;
import se.sics.kompics.network.Network;
import se.sics.kompics.network.netty.NettyInit;
import se.sics.kompics.network.netty.NettyNetwork;
import se.sics.kompics.timer.Timer;
import util.RandomNumGenerator;

import java.util.List;

public class NodeParent extends ComponentDefinition {

	private static final Logger LOG = LoggerFactory.getLogger(NodeParent.class);

    public NodeParent(Init init) {
    	LOG.info("Initiate NodeParent...");

        Component network = create(NettyNetwork.class, new NettyInit(init.self));
        Component node = create(Node.class, new Node.Init(init.self, init.allNodes, init.isLeader, init.leader));

        connect(node.getNegative(Network.class), network.getPositive(Network.class), Channel.TWO_WAY);
    }

    public static class Init extends se.sics.kompics.Init<NodeParent> {
        public final TAddress self, leader;
        public final List<TAddress> allNodes;
        public final boolean isLeader;

        public Init(TAddress self, List<TAddress> allNodes, boolean isLeader, TAddress leader) {
            this.self = self;
            this.allNodes = allNodes;
            this.isLeader = isLeader;
            this.leader = leader;
        }
    }
}