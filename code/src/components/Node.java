package components;

import com.google.common.primitives.Ints;

import beb.BestEffortBroadcast;
import beb.events.BebDataMessage;
import events.*;
import network.TAddress;
import network.TMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.sics.kompics.*;
import se.sics.kompics.network.Network;
import se.sics.kompics.network.Transport;
import se.sics.kompics.timer.*;
import se.sics.kompics.timer.Timer;

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

//    private Positive<BestEffortBroadcast> beb = requires(BestEffortBroadcast.class);
    private Positive<Network> net = requires(Network.class);
    private Positive<Timer> timer = requires(Timer.class);

    public Node(Init init) {
        this.self = init.self;
        this.leader = init.leader;
        this.isLeader = init.isLeader;
        this.allNodes = init.allNodes;


        // subscribe(bebDataHandler, beb);
    }

    Handler<Start> startHandler = new Handler<Start>() {
        @Override
        public void handle(Start event) {
            if(!isLeader)
                LOG.info("Slave  :" + self.toString());
            else {
                LOG.info("Leader : " + self.toString());
                trigger(new BebDataMessage(self, allNodes.get(5), Transport.TCP), net);
            }
        }
    };

    Handler<BebDataMessage> bebDataHandler = new Handler<BebDataMessage>() {
        @Override
        public void handle(BebDataMessage msg) {
            LOG.info("HERE");
        }
    };

    {
        subscribe(startHandler, control);
        subscribe(bebDataHandler, net);
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
