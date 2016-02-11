package components;

import com.google.common.primitives.Ints;
import network.VAddress;
import se.sics.kompics.Component;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.config.Config;
import se.sics.kompics.network.Network;
import se.sics.kompics.network.netty.NettyInit;
import se.sics.kompics.network.netty.NettyNetwork;
import se.sics.kompics.network.virtual.VirtualNetworkChannel;

import java.util.ArrayList;
import java.util.List;

public class NodeParent extends ComponentDefinition {
    public NodeParent() {
        VAddress baseSelf = config().getValue("network.node", VAddress.class);

        Component network = create(NettyNetwork.class, new NettyInit(baseSelf));
        VirtualNetworkChannel vnc = VirtualNetworkChannel.connect(network.getPositive(Network.class), proxy);
        int num = config().getValue("network.grid.num", Integer.class);

        List<Component> group = new ArrayList<>();
        // Bootstrap the system by first creating the leader and then the rest of the nodes
        VAddress leader = null;

        for (int i = 0; i < num; i++) {
            byte[] id = Ints.toByteArray(i);
            Config.Builder cbuild = config().modify(id());
            VAddress nodeAddress = baseSelf.withVirtual(id);
            cbuild.setValue("node", nodeAddress);
            Component node = null;
            if(i == 0) {
                // The leader
                node = create(Node.class, new Node.Init(null, true), cbuild.finalise());
                leader = nodeAddress;
            }
            else {
                // The slaves
                node = create(Node.class, new Node.Init(leader, false), cbuild.finalise());
            }

            vnc.addConnection(id, node.getNegative(Network.class));
            group.add(node);
        }
    }
}