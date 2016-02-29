package general;

import components.Node;
import components.NodeParent;
import components.Spawner;
//import events.Join;
import network.TAddress;
import network.THeader;
import network.TMessage;
import se.sics.kompics.Init;
import se.sics.kompics.Kompics;
import se.sics.kompics.network.netty.serialization.Serializers;
import serializer.NetSerializer;
import serializer.PayloadSerializer;
import staticdata.Grid;

import java.util.List;

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

//        Serializers.register(Join.class, "ppS");
        // Conversions
        // Conversions.register(new VAddressConverter());
    }

    public static void main(String[] args) {
        Kompics.createAndStart(Spawner.class, Init.NONE);
    }
}