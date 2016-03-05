package host;


import kth.id2203.Node;
import kth.id2203.beb.event.BEBMessage;
import kth.id2203.message.MessagePayload;
import kth.id2203.network.TAddress;
import kth.id2203.network.THeader;
import kth.id2203.network.TMessage;
import kth.id2203.pp2p.event.P2PAckMessage;
import kth.id2203.pp2p.event.P2PMessage;
import kth.id2203.serializer.NetSerializer;
import kth.id2203.serializer.PayloadSerializer;
//import events.Join;
import se.sics.kompics.Init;
import se.sics.kompics.Kompics;
import se.sics.kompics.network.netty.serialization.Serializers;


public class Main {

    static {
        // register
        Serializers.register(new NetSerializer(), "netS");
        Serializers.register(new PayloadSerializer(), "ppS");
        // map
        Serializers.register(TAddress.class, "netS");
        Serializers.register(THeader.class, "netS");
        Serializers.register(TMessage.class, "netS");
        Serializers.register(Node.class, "ppS");


        Serializers.register(BEBMessage.class, "ppS");
        Serializers.register(P2PMessage.class, "ppS");
        Serializers.register(P2PAckMessage.class, "ppS");
        Serializers.register(MessagePayload.class, "ppS");

    }

    public static void main(String[] args) {
        Kompics.createAndStart(Spawner.class, Init.NONE);
    }
}