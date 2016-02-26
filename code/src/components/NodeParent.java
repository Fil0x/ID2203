package components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.primitives.Ints;

import network.VAddress;
import se.sics.kompics.Component;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Positive;
import se.sics.kompics.config.Config;
import se.sics.kompics.network.Network;
import se.sics.kompics.network.virtual.VirtualNetworkChannel;
import se.sics.kompics.timer.Timer;
import util.RandomNumGenerator;

public class NodeParent extends ComponentDefinition {

	private static final Logger log = LoggerFactory.getLogger(NodeParent.class);
	
	Positive<Timer> timer = requires(Timer.class);
	Positive<Network> network = requires(Network.class);
	
    private final int UPPERBOUND = Integer.MAX_VALUE;

    private int nextInt(int max) {
    	return RandomNumGenerator.getInstance().next(max);
    }

    public NodeParent() {
    	log.info("Initiate NodeParent...");
    	
        VAddress baseSelf = config().getValue("network.node", VAddress.class);

        VirtualNetworkChannel vnc = VirtualNetworkChannel.connect(network, proxy);
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
            	log.info("Create Leader Node: " + i);
                node = create(Node.class, new Node.Init(null, true), cbuild.finalise());
                leader = nodeAddress;
            }
            else {
                // The slaves
            	log.info("Create Slave Node: " + i);
                node = create(Node.class, new Node.Init(leader, false), cbuild.finalise());
            }

            vnc.addConnection(id, node.getNegative(Network.class));
            group.add(node);
        }
    }
}