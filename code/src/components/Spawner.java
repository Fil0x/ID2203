package components;

import client.components.ClientHost;
import network.TAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.sics.kompics.ComponentDefinition;
import staticdata.Grid;

public class Spawner extends ComponentDefinition {
    private static final Logger LOG = LoggerFactory.getLogger(Spawner.class);

    public Spawner() {
        for(TAddress addr: Grid.getAllNodes())
            create(ComponentHost.class, new ComponentHost.Init(addr));

        // Start a client
        create(ClientHost.class, new ClientHost.Init(Grid.getClientAddress(), Grid.getAllNodes()));
    }
}
