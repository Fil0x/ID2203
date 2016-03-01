package components;

import beb.event.Broadcast;
import beb.event.Deliver;
import beb.port.BroadcastPort;

import network.TAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.sics.kompics.*;

import java.util.*;

public class Node extends ComponentDefinition {

    private static final Logger LOG = LoggerFactory.getLogger(Node.class);
    private final int UPPERBOUND = Integer.MAX_VALUE;

    // View data
    private TAddress leader;
    private TAddress self;
    private boolean isLeader;
    private List<TAddress> allNodes;

    // Key data
    private Map<Integer, Integer> keyData = new HashMap<>(); // It holds its data and that of the replica

    Positive<BroadcastPort> beb = requires(BroadcastPort.class);

    public Node(Init init) {
        this.self = init.self;
        this.leader = init.leader;
        this.isLeader = init.isLeader;
        this.allNodes = init.allNodes;
    }

    Handler<Start> startHandler = new Handler<Start>() {
        @Override
        public void handle(Start event) {
            if(!isLeader)
                LOG.info("Slave  :" + self.toString());
            else {
                LOG.info("Leader :" + self.toString());
                trigger(new Broadcast(), beb);
            }
        }
    };

    Handler<Deliver> bebDeliver = new Handler<Deliver>() {
        @Override
        public void handle(Deliver msg) {
            LOG.info("HERE:" + self.toString());
        }
    };

    {
        subscribe(startHandler, control);
        subscribe(bebDeliver, beb);
    }

    public static class Init extends se.sics.kompics.Init<Node> {
        public final TAddress self, leader;
        public boolean isLeader;
        public List<TAddress> allNodes;

        public Init(TAddress self, List<TAddress> allNodes, boolean isLeader, TAddress leader) {
            this.self = self;
            this.allNodes = allNodes;
            this.isLeader = isLeader;
            this.leader = leader;
        }
    }
}
