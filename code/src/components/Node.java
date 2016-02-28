package components;

import com.google.common.primitives.Ints;
import events.*;
import network.VAddress;
import network.VMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ports.BestEffortBroadcast;
import se.sics.kompics.*;

import java.util.*;

public class Node extends ComponentDefinition {

    private static final Logger LOG = LoggerFactory.getLogger(Node.class);
    private final int UPPERBOUND = Integer.MAX_VALUE;

    private int id;
    // View data
    private VAddress leader;
    private boolean isLeader;
    private List<VAddress> pi;
    // Key data
    private Map<Integer, Integer> keyData = new HashMap<>(); // It holds its data and that of the replica

    private Positive<BestEffortBroadcast> beb = requires(BestEffortBroadcast.class);

    private final VAddress self;

    public Node(Init init) {
        this.self = config().getValue("node", VAddress.class);
        this.id = Ints.fromByteArray(this.self.getId());

        this.leader = init.leader;
        this.isLeader = init.isLeader;
        this.pi = init.pi;

        subscribe(startHandler, control);
        subscribe(bebDataHandler, beb);
        
        LOG.info("Created Node: [id: " + id + ", Leader:" + isLeader + "]");
    }

    Handler<Start> startHandler = new Handler<Start>() {
        @Override
        public void handle(Start event) {
            if(!isLeader)
                LOG.info("Node " + id + " initialized(leader=" + Ints.fromByteArray(leader.getId()) + ")");
            else
                LOG.info("Node " + id + " is the leader");
        }
    };

    ClassMatchedHandler<Join, VMessage> joinHandler = new ClassMatchedHandler<Join, VMessage>() {
        @Override
        public void handle(Join content, VMessage context) {

        }
    };

    ClassMatchedHandler<View, VMessage> viewHandler = new ClassMatchedHandler<View, VMessage>() {
        @Override
        public void handle(View content, VMessage context) {

        }
    };

    ClassMatchedHandler<Get, VMessage> getHandler = new ClassMatchedHandler<Get, VMessage>() {
        @Override
        public void handle(Get get, VMessage vMessage) {

        }
    };

    ClassMatchedHandler<Put, VMessage> putHandler = new ClassMatchedHandler<Put, VMessage>() {
        @Override
        public void handle(Put put, VMessage vMessage) {

        }
    };

    Handler<BebMessage> bebDataHandler = new Handler<BebMessage>() {
        @Override
        public void handle(BebMessage msg) {
            LOG.info(id + ": Received message from " + Ints.fromByteArray(msg.getSource().getId()));
        }
    };

    public static class Init extends se.sics.kompics.Init<Node> {
        public final VAddress leader;
        public boolean isLeader;
        public List<VAddress> pi;

        public Init(VAddress leader, boolean isLeader, List<VAddress> allNodes) {
            this.leader = leader;
            this.isLeader = isLeader;
            this.pi = allNodes;
        }
    }
}
