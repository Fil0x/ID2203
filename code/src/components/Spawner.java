package components;

import network.TAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.sics.kompics.Component;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Kompics;
import staticdata.Grid;

import java.util.List;

public class Spawner extends ComponentDefinition {
    private static final Logger LOG = LoggerFactory.getLogger(NodeParent.class);

    public Spawner() {
        List<TAddress> allNodes = Grid.getAllNodes();
        TAddress leader = null;

        for (int i = 0; i < allNodes.size(); i++) {
            if(i == 0) {
                leader = allNodes.get(i);
                create(NodeParent.class, new NodeParent.Init(allNodes.get(i), allNodes, true, leader));
            }
            else {
                create(NodeParent.class, new NodeParent.Init(allNodes.get(i), allNodes, true, leader));
            }
        }
    }
}
