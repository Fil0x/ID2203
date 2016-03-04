package components;

import domain.ReplicationGroup;
import client.events.GetRequest;
import client.events.PutRequest;
import network.TAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pp2p.event.Pp2pDeliver;
import pp2p.event.Pp2pMessage;
import pp2p.port.PerfectPointToPointLink;
import riwcm.event.ArReadRequest;
import riwcm.event.ArReadResponse;
import riwcm.event.ArWriteRequest;
import riwcm.event.ArWriteResponse;
import riwcm.port.AtomicRegister;
import se.sics.kompics.*;
import staticdata.Grid;

import java.util.*;

public class Node extends ComponentDefinition {

    private static final Logger LOG = LoggerFactory.getLogger(Node.class);

    // View data
    private TAddress leader;
    private TAddress self;
    private boolean isLeader;
    private List<TAddress> allNodes;

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

    Handler<GetRequest> getRequest = new Handler<GetRequest>() {
        @Override
        public void handle(GetRequest event) {
            ReplicationGroup r = Grid.getReplicaGroupByKey(event.getKey());
            if(r.contains(self)) {
                // we belong to the group that can handle the key
                trigger(new ArReadRequest(event.getKey()), nnar);
            }
            else {
                // pick a random node of that group and send it
                List<TAddress> group = r.getGroup();
                TAddress dest = group.get((new Random()).nextInt(group.size()));
                trigger(new Pp2pMessage(self, dest, event), pp2p);
            }
        }
    };

    Handler<PutRequest> putRequest = new Handler<PutRequest>() {
        @Override
        public void handle(PutRequest event) {
            LOG.info("Received PUT request");
            ReplicationGroup r = Grid.getReplicaGroupByKey(event.getKey());
            if(r.contains(self)) {
                // we belong to the group that can handle the key
                trigger(new ArWriteRequest(event.getKey(), event.getValue()), nnar);
            }
            else {
                // pick a random node of that group and send it
                List<TAddress> group = r.getGroup();
                TAddress dest = group.get((new Random()).nextInt(group.size()));
                trigger(new Pp2pMessage(self, dest, event), pp2p);
            }
        }
    };

    {
        subscribe(startHandler, control);
        subscribe(readResponse, nnar);
        subscribe(writeResponse, nnar);
        subscribe(pp2pDeliver, pp2p);
        subscribe(getRequest, pp2p);
        subscribe(putRequest, pp2p);
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
