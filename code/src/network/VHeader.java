package network;

import se.sics.kompics.network.Transport;
import se.sics.kompics.network.virtual.Header;

public class VHeader implements Header<VAddress> {

    public final VAddress src;
    public final VAddress dst;
    public final Transport proto;

    public VHeader(VAddress src, VAddress dst, Transport proto) {
        this.src = src;
        this.dst = dst;
        this.proto = proto;
    }

    @Override
    public byte[] getDstId() {
        return dst.getId();
    }

    @Override
    public VAddress getSource() {
        return this.src;
    }

    @Override
    public VAddress getDestination() {
        return this.dst;
    }

    @Override
    public Transport getProtocol() {
        return this.proto;
    }
}
