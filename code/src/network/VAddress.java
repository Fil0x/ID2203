package network;

import se.sics.kompics.network.virtual.Address;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Objects;

public class VAddress implements Address {

    private final InetSocketAddress isa;
    private final byte[] id;

    public VAddress(InetAddress addr, int port) {
        this(addr, port, null);
    }

    public VAddress(InetAddress addr, int port, byte[] id) {
        this.isa = new InetSocketAddress(addr, port);
        this.id = id;
    }

    public VAddress withVirtual(byte[] id) {
        return new VAddress(this.isa.getAddress(), this.isa.getPort(), id);
    }

    @Override
    public byte[] getId() {
        return this.id;
    }

    @Override
    public InetAddress getIp() {
        return this.isa.getAddress();
    }

    @Override
    public int getPort() {
        return this.isa.getPort();
    }

    @Override
    public InetSocketAddress asSocket() {
        return this.isa;
    }

    @Override
    public boolean sameHostAs(se.sics.kompics.network.Address other) {
        return this.isa.equals(other.asSocket());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.isa.getHostString());
        sb.append(":");
        sb.append(isa.getPort());
        if(id != null) {
            sb.append(":");
            sb.append(Arrays.toString(this.id));
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.isa, this.id);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }
        if(getClass() != obj.getClass()) {
            return false;
        }

        final VAddress other = (VAddress) obj;
        return Objects.equals(this.isa, other.isa) && Objects.deepEquals(this.id, other.id);
    }
}
