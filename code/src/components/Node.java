package components;

import com.google.common.primitives.Ints;
import events.*;
import network.VAddress;
import network.VMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.sics.kompics.*;
import se.sics.kompics.network.Network;
import se.sics.kompics.network.Transport;

import java.util.*;

public class Node extends ComponentDefinition {

    private static final Logger LOG = LoggerFactory.getLogger(Node.class);
    private final int UPPERBOUND;

    private int id;
    // View data
    private VAddress leader;
    private boolean isLeader;
    private List<VAddress> view = new ArrayList<>();
    // Key data
    private Map<Integer, Integer> keyData = new HashMap<>(); // It holds its data and that of the replica

    Positive<Network> net = requires(Network.class);

    private final VAddress self;

    public Node(Init init) {
        this.self = config().getValue("node", VAddress.class);
        this.id = Ints.fromByteArray(this.self.getId());
        UPPERBOUND = config().getValue("network.grid.upperbound", Integer.class); // used for the key hashing

        this.leader = init.leader;
        this.isLeader = init.isLeader;
        
        LOG.info("Created Node: [id: " + id + ", Leader:" + isLeader + "]");
    }

    Handler<Start> startHandler = new Handler<Start>() {
        @Override
        public void handle(Start event) {
            // LOG.info(String.format("[%d]: Got START message", id));
            if(!isLeader) {
                // Send a JOIN message to the leader
            	LOG.info("Node " + id + " triggers Join Event on leader");
                trigger(new VMessage(self, leader, Transport.TCP, new Join()), net);
            }
            else {
                // The leader has to add himself to the view
                view.add(self);
            }
        }
    };

    ClassMatchedHandler<Join, VMessage> joinHandler = new ClassMatchedHandler<Join, VMessage>() {
        @Override
        public void handle(Join content, VMessage context) {
            int srcId = Ints.fromByteArray(context.getSource().getId());
            // LOG.info(String.format("[%d]: Got JOIN message from ID: [%d]", id, srcId));
            
            
            // We have a new node in the group
            view.add(context.getSource());
            // Sort the view based on the node id
            Collections.sort(view, new Comparator<VAddress>() {
                @Override
                public int compare(VAddress o1, VAddress o2) {
                    int o1Id = Ints.fromByteArray(o1.getId());
                    int o2Id = Ints.fromByteArray(o2.getId());
                    return o1Id - o2Id;
                }
            });
            
            // we must update the view and broadcast it, skip yourself
            for (VAddress v: view) {
            	if(!v.equals(self)) {
                	trigger(new VMessage(self, v, Transport.TCP, new View(new ArrayList<VAddress>(view))), net);
                }
            }
        }
    };

    ClassMatchedHandler<View, VMessage> viewHandler = new ClassMatchedHandler<View, VMessage>() {
        @Override
        public void handle(View content, VMessage context) {
            int srcId = Ints.fromByteArray(context.getSource().getId());
            //  save the view locally
            view = content.view;
            // Get these group IDs
            StringBuilder sb = new StringBuilder();
            for(VAddress v: view) {
                sb.append(Ints.fromByteArray(v.getId()));
                sb.append(" ");
            }
            
            LOG.info(String.format("[%d]: Got VIEW message from ID: [%d](%s)", id, srcId, sb.toString().trim()));
        }
    };

    ClassMatchedHandler<Get, VMessage> getHandler = new ClassMatchedHandler<Get, VMessage>() {
        @Override
        public void handle(Get get, VMessage vMessage) {
            // I received a GET request, now I have to find the owner and his replica
            VAddress owner = null, replica = null;
            for (VAddress v : view) {
                int id = Ints.fromByteArray(v.getId());
                if(get.getKey() <= id) {
                    owner = v;
                    replica = getNext(id);

                    break;
                }
            }

            // Am I the owner, the replica or none of them?
            if(self == owner) { // The request is addressed to me
                // Send the request to the requester and the replica
                // First send it to the replica, instead of our source address we spoof it with the clients'
                trigger(new VMessage(vMessage.getSource(), replica, Transport.TCP,get), net);
                // and then back to the requester
                trigger(new VMessage(self, vMessage.getSource(), Transport.TCP, new Reply(get.getKey(), keyData.get(get.getKey()))), net);
            }
            else if(self == replica) { // I am the replica
                // Same deal as the previous case
                // First to the owner
                trigger(new VMessage(vMessage.getSource(), owner, Transport.TCP,get), net);
                // then to the requester
                trigger(new VMessage(self, vMessage.getSource(), Transport.TCP, new Reply(get.getKey(), keyData.get(get.getKey()))), net);
            }
            else { // Send the messages to the responsible nodes
                trigger(new VMessage(vMessage.getSource(), owner, Transport.TCP,get), net);
                trigger(new VMessage(vMessage.getSource(), replica, Transport.TCP,get), net);
            }
        }
    };

    ClassMatchedHandler<Put, VMessage> putHandler = new ClassMatchedHandler<Put, VMessage>() {
        @Override
        public void handle(Put put, VMessage vMessage) {
            // Hash it to find the proper owner and replica
            int hashedKey = general.Utilities.hash(put.getKey(), UPPERBOUND);
            // I received a PUT request, now I have to find the owner and his replica
            VAddress owner = null, replica = null;
            for (VAddress v : view) {
                int id = Ints.fromByteArray(v.getId());
                if(hashedKey <= id) {
                    owner = v;
                    replica = getNext(id);

                    break;
                }
            }

            // Am I the owner, the replica or none of them?
            if(self == owner) { // The request is addressed to me
                // Store it
                keyData.put(hashedKey, put.getValue());
                // Send the request to the replica
                // First send it to the replica, instead of our source address we spoof it with the clients'
                trigger(new VMessage(vMessage.getSource(), replica, Transport.TCP, put), net);

                // and then back to the requester. No need for reply?
                // trigger(new VMessage(self, vMessage.getSource(), Transport.TCP, new Reply(put.getKey(), keyData.get(put.getKey()))), net);
            }
            else if(self == replica) { // I am the replica
                // Store it
                keyData.put(hashedKey, put.getValue());
                // Same deal as the previous case
                // First to the owner
                trigger(new VMessage(vMessage.getSource(), owner, Transport.TCP,put), net);
                // then to the requester. No need for reply?
                // trigger(new VMessage(self, vMessage.getSource(), Transport.TCP, new Reply(put.getKey(), keyData.get(put.getKey()))), net);
            }
            else { // Send the messages to the responsible nodes
                trigger(new VMessage(vMessage.getSource(), owner, Transport.TCP, put), net);
                trigger(new VMessage(vMessage.getSource(), replica, Transport.TCP, put), net);
            }
        }
    };

    private VAddress getPrevious(int refNode) {
        // We are searching for the maximum id of the view without our own and its less than our own
        //              max(V \ ourID && v in V \ ourID: v < ourID)
        VAddress prevNode = null;
        for (int i = 0; i < view.size(); i++) {
            int nodeId = Ints.fromByteArray(view.get(i).getId());
            if(nodeId == refNode) { // Found myself, now I have to find the previous
                if(i == 0)
                    prevNode = view.get(view.size()-1);
                else
                    prevNode = view.get(i - 1);
            }
        }

        return prevNode;
    }

    private VAddress getNext(int refNode) {
        // We are searching for the maximum id of the view without our own and its less than our own
        //              max(V \ ourID && v in V \ ourID: v > ourID) && cycle
        VAddress nextNode = null;
        for (int i = 0; i < view.size(); i++) {
            int nodeId = Ints.fromByteArray(view.get(i).getId());
            if(nodeId == refNode) { // Found myself, now I have to find the next
                if(i == (view.size() - 1))
                    nextNode = view.get(0);
                else
                    nextNode = view.get(i + 1);
            }
        }

        return nextNode;
    }

    {
        subscribe(startHandler, control);
        subscribe(joinHandler, net);
        subscribe(viewHandler, net);
        subscribe(getHandler, net);
        subscribe(putHandler, net);
    }

    public static class Init extends se.sics.kompics.Init<Node> {
        public final VAddress leader;
        public boolean isLeader;
        public Init(VAddress leader, boolean isLeader) {
            this.leader = leader;
            this.isLeader = isLeader;
        }
    }
}
