import se.sics.kompics.Kompics;
import se.sics.kompics.config.Conversions;
import se.sics.kompics.network.netty.serialization.Serializers;

public class Main {

    static {
        // register
        Serializers.register(new NetSerializer(), "netS");
        Serializers.register(new PayloadSerializer(), "ppS");
        // map
        Serializers.register(VAddress.class, "netS");
        Serializers.register(VHeader.class, "netS");
        Serializers.register(VMessage.class, "netS");
        Serializers.register(Node.class, "ppS");
        // Conversions
        Conversions.register(new VAddressConverter());
    }

    public static void main(String[] args) {
        Kompics.createAndStart(NodeParent.class);
    }
}