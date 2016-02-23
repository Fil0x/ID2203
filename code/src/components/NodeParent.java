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
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class NodeParent extends ComponentDefinition {

    private final int UPPERBOUND;//Integer.MAX_VALUE;

    private long nextLong(long n) {
        // error checking and 2^x checking removed for simplicity.
        long bits, val;
        do {
            bits = ((new Random()).nextLong() << 1) >>> 1;
            val = bits % n;
        } while (bits-val+(n-1) < 0L);
        return val;
    }

    private int nextInt(int max) {
        return (new Random()).nextInt(max);
    }

    public NodeParent() {
        VAddress baseSelf = config().getValue("network.node", VAddress.class);
        UPPERBOUND = config().getValue("network.grid.upperbound", Integer.class);

        Component network = create(NettyNetwork.class, new NettyInit(baseSelf));
        VirtualNetworkChannel vnc = VirtualNetworkChannel.connect(network.getPositive(Network.class), proxy);
        int num = config().getValue("network.grid.num", Integer.class);

        List<Component> group = new ArrayList<>();
        // Bootstrap the system by first creating the leader and then the rest of the nodes
        VAddress leader = null;

        // Generate #num random ids and sort them
        List<Integer> ids = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            ids.add(this.nextInt(UPPERBOUND));
        }
        Collections.sort(ids);

        for (int i = 0; i < num; i++) {
            byte[] id = Ints.toByteArray(ids.get(i));
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