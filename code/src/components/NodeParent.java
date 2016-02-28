package components;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.primitives.Ints;

import network.VAddress;
import ports.BebBroadcast;
import ports.BebDeliver;
import ports.BestEffortBroadcast;
import se.sics.kompics.*;
import se.sics.kompics.config.Config;
import se.sics.kompics.network.Network;
import se.sics.kompics.network.netty.NettyInit;
import se.sics.kompics.network.netty.NettyNetwork;
import se.sics.kompics.network.virtual.VirtualNetworkChannel;
import se.sics.kompics.timer.Timer;
import staticdata.Grid;
import sun.nio.ch.Net;
import util.RandomNumGenerator;

public class NodeParent extends ComponentDefinition {

	private static final Logger LOG = LoggerFactory.getLogger(NodeParent.class);
	
	Positive<Timer> timer = requires(Timer.class);
	Positive<Network> network = requires(Network.class);
	
    private final int UPPERBOUND = Integer.MAX_VALUE;

    private int nextInt(int max) {
    	return RandomNumGenerator.getInstance().next(max);
    }

    public NodeParent() {
    	LOG.info("Initiate NodeParent...");
    	
        VAddress baseSelf = config().getValue("network.node", VAddress.class);
        VirtualNetworkChannel vnc = VirtualNetworkChannel.connect(network, proxy);

        List<VAddress> pi = Grid.getAllNodes(baseSelf);

        Component beb = create(BasicBroadcast.class, new BasicBroadcast.Init(baseSelf.withVirtual(Ints.toByteArray(1)), pi));
        vnc.addConnection(Ints.toByteArray(1), beb.getNegative(Network.class));

        // Bootstrap the system by first creating the leader and then the rest of the nodes
        VAddress leader = null;
        for(VAddress nodeAddr: pi) {
            Config.Builder cbuild = config().modify(id());
            cbuild.setValue("node", nodeAddr);

            LOG.info("Creating node with id: " + Ints.fromByteArray(nodeAddr.getId()));
            Component newNode = null;
            if(Ints.fromByteArray(nodeAddr.getId()) == 100) {
                newNode = create(Node.class, new Node.Init(null, true, pi), cbuild.finalise());
                leader = nodeAddr;
            }
            else {
                newNode = create(Node.class, new Node.Init(leader, false, pi), cbuild.finalise());
            }
            connect(newNode.getNegative(BestEffortBroadcast.class), beb.getPositive(BestEffortBroadcast.class), Channel.TWO_WAY);
        }
    }

    @Override
    public Fault.ResolveAction handleFault(Fault fault) {
        return super.handleFault(fault);
    }
}