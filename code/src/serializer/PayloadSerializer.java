package serializer;

import com.google.common.base.Optional;
import com.google.common.primitives.Ints;
import events.*;
import io.netty.buffer.ByteBuf;
import network.VAddress;
import se.sics.kompics.network.netty.serialization.Serializer;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class PayloadSerializer implements Serializer {

    private static final byte VIEW = 1;
    private static final byte JOIN = 2;
    private static final byte GET = 3;
    private static final byte PUT = 4;
    private static final byte REPLY = 5;

    @Override
    public int identifier() {
        return 200;
    }

    @Override
    public void toBinary(Object o, ByteBuf buf) {
        if(o instanceof View) {
            View v = (View) o;
            List<VAddress> groupView = v.view;
            buf.writeByte(VIEW);
            buf.writeByte(groupView.size()); // 4 bytes
            for(VAddress nodeAddress: groupView) // size * 6 bytes
                addressToBinary(nodeAddress, buf); // 6 bytes
            // Total: 1 + 4 + size * 6
        }
        else if(o instanceof Join) {
            Join j = (Join) o;
            buf.writeByte(JOIN);
            // Total: 1
        }
        else if(o instanceof Get) {
            Get g = (Get) o;
            buf.writeByte(GET);
            buf.writeInt(g.getKey());
            // Total: 1 + 4
        }
        else if(o instanceof Put) {
            Put p = (Put) o;
            buf.writeByte(PUT);
            buf.writeInt(p.getKey());
            buf.writeInt(p.getValue());
            // Total: 1 + 4(key) + 4(value)
        }
        else if(o instanceof Reply) {
            Reply r = (Reply) o;
            buf.writeByte(REPLY);
            buf.writeInt(r.getKey());
            buf.writeInt(r.getValue());
            // Total = 1 + 4(key) + 4(value)
        }
    }

    @Override
    public Object fromBinary(ByteBuf buf, Optional<Object> hint) {
        byte type = buf.readByte(); // 1 byte
        switch (type) {
            case VIEW:
                // First read the size of the view
                byte[] size = new byte[4];
                buf.readBytes(size);
                int s = Ints.fromByteArray(size);
                // Now loop "s" times and read 6 bytes everytime
                List<VAddress> groupView = new ArrayList<>();
                for (int i = 0; i < s; i++) {
                    groupView.add(addressFromBinary(buf));
                }

                return new View(groupView);
            case JOIN:
                return new Join();
            case GET:
                byte[] rawKey = new byte[4];
                buf.readBytes(rawKey);

                return new Get(Ints.fromByteArray(rawKey));
            case PUT:
                byte[] pKey = new byte[4];
                byte[] pValue = new byte[4];
                buf.readBytes(pKey);
                buf.readBytes(pValue);

                return new Put(Ints.fromByteArray(pKey),
                               Ints.fromByteArray(pValue));
            case REPLY:
                byte[] rKey = new byte[4];
                byte[] rvalue = new byte[4];
                buf.readBytes(rKey);
                buf.readBytes(rvalue);

                return new Reply(Ints.fromByteArray(rKey),
                                 Ints.fromByteArray(rvalue));
        }
        return null;
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