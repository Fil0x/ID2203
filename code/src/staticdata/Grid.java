package staticdata;


import domain.ReplicationGroup;
import network.TAddress;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class Grid {

    private static final int DELTA = 2;
    private static final int NODESCOUNT = 6;
    private static final int KEYSPERGROUP = 20;

    public static List<TAddress> getAllNodes() {
        try {
            // Create the static 6 nodes
            List<TAddress> nodes = new ArrayList<>();
            InetAddress baseIP = InetAddress.getByName("127.0.0.1");
            int basePort = 20000;

            for (int i = 0; i < NODESCOUNT; i++) {
                nodes.add(new TAddress(baseIP, basePort + i));
            }

            return nodes;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<ReplicationGroup> getReplicationGroups() {
        List<TAddress> allNodes = getAllNodes();
        List<ReplicationGroup> group = new ArrayList<>();

        int nodesLeft = NODESCOUNT, temp = 0, previousBound = 0, previousIndex = 0, loop = nodesLeft / DELTA;
        for (int i = 0; i < loop; i++) {
            temp = nodesLeft - DELTA;
            ReplicationGroup r = new ReplicationGroup(previousBound, previousBound + KEYSPERGROUP);
            if(temp < DELTA) {
                for (int j = 0; j < nodesLeft; j++)
                    r.addToGroup(allNodes.get(previousIndex + j));
                group.add(r);
                break;
            }
            else {
                for (int j = 0; j < DELTA; j++)
                    r.addToGroup(allNodes.get(previousIndex + j));
                previousBound += KEYSPERGROUP;
                previousIndex += DELTA;
                nodesLeft -= DELTA;
                group.add(r);
            }
        }

        return group;
    }

    public static ReplicationGroup getReplicaGroupByAddress(TAddress addr) {
        for (ReplicationGroup r: getReplicationGroups()) {
            if(r.contains(addr))
                return r;
        }
        // Shouldn't happen
        return null;
    }

    public static ReplicationGroup getReplicaGroupByKey(int key) {
        for (ReplicationGroup r: getReplicationGroups()) {
            if(r.inRange(key))
                return r;
        }
        // Shouldn't happen
        return null;
    }
}
