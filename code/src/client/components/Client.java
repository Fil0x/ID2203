package client.components;


import client.events.GetRequest;
import client.events.PutRequest;
import network.TAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pp2p.event.Pp2pMessage;
import pp2p.event.Pp2pSend;
import pp2p.port.PerfectPointToPointLink;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;

import java.util.List;
import java.util.Random;

public class Client extends ComponentDefinition {

    private static final Logger LOG = LoggerFactory.getLogger(Client.class);
    private static byte GET = 1;
    private static byte PUT = 2;

    private Positive<PerfectPointToPointLink> pp2p = requires(PerfectPointToPointLink.class);
    private Integer key, value;
    private byte type;
    private TAddress self;
    private List<TAddress> all;

    public Client(Init init) {
        this.self = init.getSelf();
        this.all = init.getAllNodes();
        this.key = init.getKey();
        this.value = init.getValue();
        this.type = init.getRequestType();

        subscribe(startHandler, control);
    }

    Handler<Start> startHandler = new Handler<Start>() {
        @Override
        public void handle(Start event) {
            int rand = (new Random()).nextInt(all.size());
            TAddress dest = all.get(rand);
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(type == GET)
                trigger(new Pp2pSend(dest, new GetRequest(self, key)), pp2p);
            else if(type == PUT) {
                LOG.info("------------------------");
                LOG.info(String.format("Sending PUT(%d, %d) to: %s", key, value, dest.toString()));
                trigger(new Pp2pSend(dest, new PutRequest(self, key, value)), pp2p);
            }
        }
    };

    public static class Init extends se.sics.kompics.Init<Client> {
        private final TAddress self;
        private List<TAddress> allNodes;
        private byte requestType;
        private Integer key, value;

        public Init(TAddress self, List<TAddress> allNodes, byte requestType, Integer key, Integer value) {
            this.self = self;
            this.allNodes = allNodes;
            this.requestType = requestType;
            this.key = key;
            this.value = value;
        }

        public TAddress getSelf() {
            return self;
        }

        public List<TAddress> getAllNodes() {
            return allNodes;
        }

        public byte getRequestType() {
            return requestType;
        }

        public Integer getKey() {
            return key;
        }

        public Integer getValue() {
            return value;
        }
    }
}
