package serializer;

import beb.event.BEBDeliver;
import com.google.common.base.Optional;
import com.google.common.primitives.Ints;

import beb.event.BEBMessage;
import events.Get;
import events.Put;
import events.Reply;
import io.netty.buffer.ByteBuf;
import network.THeader;
import org.apache.commons.lang3.SerializationUtils;
import pp2p.event.Pp2pDeliver;
import pp2p.event.Pp2pMessage;
import se.sics.kompics.network.netty.serialization.Serializer;
import se.sics.kompics.network.netty.serialization.Serializers;

public class PayloadSerializer implements Serializer {

    private static final byte GET = 3;
    private static final byte PUT = 4;
    private static final byte REPLY = 5;
    
    private static final byte BEB = 7;
    private static final byte P2P = 8;
    
    @Override
    public int identifier() {
        return 200;
    }

    @Override
    public void toBinary(Object o, ByteBuf buf) {
    	if(o instanceof Get) {
            Get g = (Get) o;
            buf.writeByte(GET);
            buf.writeInt(g.getKey());
            buf.writeBoolean(g.toForward());
            // Total: 1 + 4 + boolean_size
        }
        else if(o instanceof Put) {
            Put p = (Put) o;
            buf.writeByte(PUT);
            buf.writeInt(p.getKey());
            buf.writeInt(p.getValue());
            buf.writeBoolean(p.toForward());
            // Total: 1 + 4(key) + 4(value) + boolean_size
        }
        else if(o instanceof Reply) {
            Reply r = (Reply) o;
            buf.writeByte(REPLY);
            buf.writeInt(r.getKey());
            buf.writeInt(r.getValue());
            // Total = 1 + 4(key) + 4(value)
        }

        else if(o instanceof BEBMessage) {
        	BEBMessage r = (BEBMessage) o;
            byte[] event = SerializationUtils.serialize(r.getDeliverEvent());

            buf.writeByte(BEB);
            Serializers.toBinary(r.header, buf);
            buf.writeInt(event.length);
            buf.writeBytes(event);
        }
    	
        else if(o instanceof Pp2pMessage) {
        	Pp2pMessage r = (Pp2pMessage) o;
            byte[] event = SerializationUtils.serialize(r.getDeliverEvent());

            buf.writeByte(P2P);
            Serializers.toBinary(r.header, buf);
            buf.writeInt(event.length);
            buf.writeBytes(event);
        }

    }

    @Override
    public Object fromBinary(ByteBuf buf, Optional<Object> hint) {
        byte type = buf.readByte(); // 1 byte
        switch (type) {
            case GET:
                byte[] rawKey = new byte[4];
                buf.readBytes(rawKey);

                Get g = new Get(Ints.fromByteArray(rawKey));
                g.setToForward(buf.readBoolean());
                return g;
            case PUT:
                byte[] pKey = new byte[4];
                byte[] pValue = new byte[4];
                buf.readBytes(pKey);
                buf.readBytes(pValue);

                Put p = new Put(Ints.fromByteArray(pKey), Ints.fromByteArray(pValue));
                p.setToForward(buf.readBoolean());
                return p;
            case REPLY:
                byte[] rKey = new byte[4];
                byte[] rvalue = new byte[4];
                buf.readBytes(rKey);
                buf.readBytes(rvalue);

                return new Reply(Ints.fromByteArray(rKey),
                                 Ints.fromByteArray(rvalue));

            case BEB: {
                THeader header = (THeader) Serializers.fromBinary(buf, Optional.absent()); // 1 byte serialiser id + 16 bytes THeader
                int eventSize = buf.readInt();
                byte[] event = new byte[eventSize];
                buf.readBytes(event);
                BEBDeliver bebDeliver = SerializationUtils.deserialize(event);
                return new BEBMessage(header, bebDeliver); // 18 bytes total, check
            } 
            
            case P2P: {
                THeader header = (THeader) Serializers.fromBinary(buf, Optional.absent()); // 1 byte serialiser id + 16 bytes THeader
                int eventSize = buf.readInt();
                byte[] event = new byte[eventSize];
                buf.readBytes(event);
                Pp2pDeliver pp2pDeliver = SerializationUtils.deserialize(event);
                return new Pp2pMessage(header, pp2pDeliver); // 18 bytes total, check
            } 
            
            
        }
        return null;
    }
}
