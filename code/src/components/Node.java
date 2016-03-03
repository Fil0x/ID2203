package components;

import beb.event.BEBDeliver;
import beb.event.BEBroadcast;
import beb.port.BroadcastPort;

import network.TAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pp2p.event.Pp2pDeliver;
import pp2p.event.Pp2pSend;
import pp2p.port.PerfectPointToPointLink;
import riwcm.event.ArReadResponse;
import riwcm.event.ArWriteResponse;
import riwcm.port.AtomicRegister;
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

    Positive<AtomicRegister> nnar = requires(AtomicRegister.class);
    Positive<PerfectPointToPointLink> pp2p = requires(PerfectPointToPointLink.class);

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
            }
        }
    };

    Handler<ArReadResponse> readResponse = new Handler<ArReadResponse>() {

        @Override
        public void handle(ArReadResponse msg) {
            LOG.info("Got ArRead msg:" + self.toString() + ". Read value: " + msg.getValue());
        }
    };

    Handler<ArWriteResponse> writeResponse = new Handler<ArWriteResponse>() {

        @Override
        public void handle(ArWriteResponse msg) {
            LOG.info("Got ArWrite msg:" + self.toString());
        }
    };

    Handler<Pp2pDeliver> pp2pDeliver = new Handler<Pp2pDeliver>() {
        @Override
        public void handle(Pp2pDeliver msg) {
            LOG.info("Got P2P msg:" + self.toString());
        }
    };

    {
        subscribe(startHandler, control);
        subscribe(readResponse, nnar);
        subscribe(writeResponse, nnar);
        subscribe(pp2pDeliver, pp2p);
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
