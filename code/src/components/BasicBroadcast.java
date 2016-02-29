package components;


import com.google.common.primitives.Ints;
import network.TAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ports.BebBroadcast;
import ports.BestEffortBroadcast;
import se.sics.kompics.*;
import se.sics.kompics.network.Network;
import se.sics.kompics.network.Transport;

import java.util.List;

public class BasicBroadcast extends ComponentDefinition {
//
//    private static final Logger LOG = LoggerFactory.getLogger(BasicBroadcast.class);
//
//    private Positive<Network> network = requires(Network.class);
//    private Negative<BestEffortBroadcast> beb = provides(BestEffortBroadcast.class);
//
//    private TAddress self;
//    private List<TAddress> nodes;
//
//    public BasicBroadcast(Init init) {
//        self = init.getSelf();
//        nodes = init.getPi();
//
//        subscribe(startHandler, control);
//        subscribe(broadcastHandler, beb);
//        subscribe(deliverHandler, network);
//    }
//
//    private Handler<Start> startHandler = new Handler<Start>() {
//        @Override
//        public void handle(Start start) {
//            LOG.info("Basic broadcaster started with id:" + Ints.fromByteArray(self.getId()));
//        }
//    };
//
//    private Handler<BebBroadcast> broadcastHandler = new Handler<BebBroadcast>() {
//        @Override
//        public void handle(BebBroadcast event) {
//            for(VAddress node: nodes) {
//                BebDataMessage msg = new BebDataMessage(node, event.getDeliverEvent());
//                trigger(new VMessage(node, node, Transport.TCP, msg), network);
//            }
//        }
//    };
//
//    private ClassMatchedHandler<BebMessage, VMessage> deliverHandler = new ClassMatchedHandler<BebMessage, VMessage>() {
//        @Override
//        public void handle(BebMessage event, VMessage message) {
//            trigger(event, beb);
//        }
//    };
//
//    public VAddress getSelf() {
//        return self;
//    }
//
//    public static class Init extends se.sics.kompics.Init<BasicBroadcast> {
//        private VAddress self;
//        private List<VAddress> pi;
//
//        public Init(VAddress self, List<VAddress> pi) {
//            this.self = self;
//            this.pi = pi;
//        }
//
//        public VAddress getSelf() {
//            return self;
//        }
//
//        public List<VAddress> getPi() {
//            return pi;
//        }
//    }
}
