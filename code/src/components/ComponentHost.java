package components;


import beb.component.BroadcastComponent;
import beb.port.BroadcastPort;
import network.TAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pp2p.component.Pp2pLink;
import pp2p.port.PerfectPointToPointLink;
import riwcm.component.ReadImposeWriteConsultMajority;
import riwcm.port.AtomicRegister;
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
        Component nnar = create(ReadImposeWriteConsultMajority.class, new ReadImposeWriteConsultMajority.Init(init.self, allNodes));
        Component beb = create(BroadcastComponent.class, new BroadcastComponent.Init(init.self, allNodes));
        Component pp2p = create(Pp2pLink.class, new Pp2pLink.Init(init.self, allNodes));
        Component network = create(NettyNetwork.class, new NettyInit(init.self));

        if (init.self.equals(leader))
            node = create(Node.class, new Node.Init(init.self, allNodes, true, null));
        else
            node = create(Node.class, new Node.Init(init.self, allNodes, false, leader));

        connect(node.getNegative(AtomicRegister.class), nnar.getPositive(AtomicRegister.class), Channel.TWO_WAY);
        connect(node.getNegative(PerfectPointToPointLink.class), pp2p.getPositive(PerfectPointToPointLink.class), Channel.TWO_WAY);

        connect(nnar.getNegative(BroadcastPort.class), beb.getPositive(BroadcastPort.class), Channel.TWO_WAY);
        connect(nnar.getNegative(PerfectPointToPointLink.class), pp2p.getPositive(PerfectPointToPointLink.class), Channel.TWO_WAY);

        connect(beb.getNegative(Network.class), network.getPositive(Network.class), Channel.TWO_WAY);

        connect(pp2p.getNegative(Network.class), network.getPositive(Network.class), Channel.TWO_WAY);
    }

    public static class Init extends se.sics.kompics.Init<ComponentHost> {
        public final TAddress self;

        public Init(TAddress self) {
            this.self = self;
        }
    }

}
