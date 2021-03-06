package general;

import beb.event.BEBMessage;
import components.Node;
import components.Spawner;
//import events.Join;
import network.TAddress;
import network.THeader;
import network.TMessage;
import pp2p.event.Pp2pMessage;
import se.sics.kompics.Init;
import se.sics.kompics.Kompics;
import se.sics.kompics.network.netty.serialization.Serializers;
import serializer.NetSerializer;
import serializer.PayloadSerializer;

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
        Serializers.register(Pp2pMessage.class, "ppS");
    }

    public static void main(String[] args) {
        Kompics.createAndStart(Spawner.class, Init.NONE);
    }
}