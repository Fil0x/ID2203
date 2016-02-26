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
    private final int UPPERBOUND = Integer.MAX_VALUE;

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

        }
    };

    ClassMatchedHandler<Put, VMessage> putHandler = new ClassMatchedHandler<Put, VMessage>() {
        @Override
        public void handle(Put put, VMessage vMessage) {

        }
    };

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
