package components;


import beb.component.BroadcastComponent;
import beb.port.BroadcastPort;
import network.TAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.sics.kompics.Channel;
import se.sics.kompics.Component;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.network.Network;
import se.sics.kompics.network.netty.NettyInit;
import se.sics.kompics.network.netty.NettyNetwork;
import staticdata.Grid;

import java.util.List;

public class ComponentHost extends ComponentDefinition {
    private static final Logger LOG = LoggerFactory.getLogger(ComponentHost.class);

    public ComponentHost(Init init) {
        List<TAddress> allNodes = Grid.getAllNodes();
        TAddress leader = allNodes.get(0);

        LOG.info("Initializing: " + init.toString());

        Component node;
        Component beb = create(BroadcastComponent.class, new BroadcastComponent.Init(init.self, allNodes));
        Component network = create(NettyNetwork.class, new NettyInit(init.self));

        if (init.self.equals(leader))
            node = create(Node.class, new Node.Init(init.self, allNodes, true, null));
        else
            node = create(Node.class, new Node.Init(init.self, allNodes, false, leader));

        connect(node.getNegative(BroadcastPort.class), beb.getPositive(BroadcastPort.class), Channel.TWO_WAY);
        connect(beb.getNegative(Network.class), network.getPositive(Network.class), Channel.TWO_WAY);
    }

    public static class Init extends se.sics.kompics.Init<ComponentHost> {
        public final TAddress self;

        public Init(TAddress self) {
            this.self = self;
        }
    }

}
