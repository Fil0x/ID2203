package serializer;

import com.google.common.base.Optional;
import io.netty.buffer.ByteBuf;
import network.VAddress;
import network.VHeader;
import network.VMessage;
import se.sics.kompics.KompicsEvent;
import se.sics.kompics.network.Transport;
import se.sics.kompics.network.netty.serialization.Serializer;
import se.sics.kompics.network.netty.serialization.Serializers;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class NetSerializer implements Serializer {

    private static final byte ADDR = 1;
    private static final byte HEADER = 2;
    private static final byte MSG = 3;

    @Override
    public int identifier() {
        return 100;
    }

    @Override
    public void toBinary(Object o, ByteBuf buf) {
        if (o instanceof VAddress) {
            VAddress addr = (VAddress) o;
            buf.writeByte(ADDR); // mark which type we are serialising (1 byte)
            addressToBinary(addr, buf); // 6 bytes
            // total 7 bytes
        } else if (o instanceof VHeader) {
            VHeader header = (VHeader) o;
            buf.writeByte(HEADER); // mark which type we are serialising (1 byte)
            headerToBinary(header, buf); // 13 bytes
            // total 14 bytes
        } else if (o instanceof VMessage) {
            VMessage msg = (VMessage) o;
            buf.writeByte(MSG); // mark which type we are serialising (1 byte)
            headerToBinary(msg.header, buf); // 13 bytes
            Serializers.toBinary(msg.payload, buf); // no idea what it is, let the framework deal with it
        }
    }

    @Override
    public Object fromBinary(ByteBuf buf, Optional<Object> hint) {
        byte type = buf.readByte(); // read the first byte to figure out the type
        switch (type) {
            case ADDR:
                return addressFromBinary(buf);
            case HEADER:
                return headerFromBinary(buf);
            case MSG: {
                VHeader header = headerFromBinary(buf); // 13 bytes
                KompicsEvent payload = (KompicsEvent) Serializers.fromBinary(buf, Optional.absent()); // don't know what it is but KompicsEvent is the upper bound
                return new VMessage(header, payload);
            }
        }
        return null; // strange things happened^^
    }

    private void headerToBinary(VHeader header, ByteBuf buf) {
        addressToBinary(header.src, buf); // 6 bytes
        addressToBinary(header.dst, buf); // 6 bytes
        buf.writeByte(header.proto.ordinal()); // 1 byte is enough
        // total of 13 bytes
    }

    private VHeader headerFromBinary(ByteBuf buf) {
        VAddress src = addressFromBinary(buf); // 6 bytes
        VAddress dst = addressFromBinary(buf); // 6 bytes
        int protoOrd = buf.readByte(); // 1 byte
        Transport proto = Transport.values()[protoOrd];
        return new VHeader(src, dst, proto); // total of 13 bytes, check
    }

    private void addressToBinary(VAddress addr, ByteBuf buf) {
        buf.writeBytes(addr.getIp().getAddress()); // 4 bytes IP (let's hope it's IPv4^^)
        buf.writeShort(addr.getPort()); // we only need 2 bytes here
        if (addr.getId() != null) {
            buf.writeByte(addr.getId().length); // 1 byte - we only want to use 4 bytes for ids, so let's not waste space
            buf.writeBytes(addr.getId()); // should be 4 bytes
        } else {
            buf.writeByte(-1); //  we'll use this a null-marker (while a length of 0 indicates an empty array)
        }
    }

    private VAddress addressFromBinary(ByteBuf buf) {
        byte[] ipBytes = new byte[4];
        buf.readBytes(ipBytes); // 4 bytes
        try {
            InetAddress ip = InetAddress.getByAddress(ipBytes);
            int port = buf.readUnsignedShort(); // 2 bytes
            int idLength = buf.readByte(); // 1 byte
            if (idLength >= 0) {
                byte[] id = new byte[idLength]; // should be 4 bytes
                buf.readBytes(id);
                return new VAddress(ip, port, id);
            } else {
                return new VAddress(ip, port); // total of 6, check
            }
        } catch (UnknownHostException ex) {
            throw new RuntimeException(ex); // let Netty deal with this
        }
    }
}
