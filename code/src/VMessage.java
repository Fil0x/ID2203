import se.sics.kompics.KompicsEvent;
import se.sics.kompics.PatternExtractor;
import se.sics.kompics.network.Msg;
import se.sics.kompics.network.Transport;

public class VMessage implements Msg<VAddress, VHeader>, PatternExtractor<Class, KompicsEvent> {

    public final VHeader header;
    public final KompicsEvent payload;

    public VMessage(VAddress src, VAddress dst, Transport proto, KompicsEvent payload) {
        this.header = new VHeader(src, dst, proto);
        this.payload = payload;
    }

    public VMessage(VHeader header, KompicsEvent payload) {
        this.header = header;
        this.payload = payload;
    }

    @Override
    public VHeader getHeader() {
        return this.header;
    }

    @Override
    public VAddress getSource() {
        return this.header.src;
    }

    @Override
    public VAddress getDestination() {
        return this.header.dst;
    }

    @Override
    public Transport getProtocol() {
        return this.header.proto;
    }

    @Override
    public Class extractPattern() {
        return (Class) payload.getClass();
    }

    @Override
    public KompicsEvent extractValue() {
        return payload;
    }
}
