package serializer;

import beb.event.BEBDeliver;
import com.google.common.base.Optional;
import com.google.common.primitives.Ints;

import beb.event.BEBMessage;
import client.events.GetRequest;
import client.events.PutRequest;
import client.events.GetReply;
import io.netty.buffer.ByteBuf;
import network.THeader;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pp2p.event.Pp2pDeliver;
import pp2p.event.Pp2pMessage;
import se.sics.kompics.network.netty.serialization.Serializer;
import se.sics.kompics.network.netty.serialization.Serializers;
import staticdata.Grid;

public class PayloadSerializer implements Serializer {

    private static final Logger LOG = LoggerFactory.getLogger(PayloadSerializer.class);

    @Override
    public int identifier() {
        return 200;
    }

    @Override
    public void toBinary(Object o, ByteBuf buf) {
        LOG.info("Serializing...");
    	if(o instanceof BEBMessage) {
            LOG.info("...a BEBMessage");
        	BEBMessage r = (BEBMessage) o;
            byte[] event = SerializationUtils.serialize(r.getDeliverEvent());

            buf.writeByte(Grid.BEB);
            Serializers.toBinary(r.header, buf);
            buf.writeInt(event.length);
            buf.writeBytes(event);
        }
    	
        else if(o instanceof Pp2pMessage) {
        	Pp2pMessage r = (Pp2pMessage) o;
            LOG.info("...a Pp2pMessage with type:" + r.getDeliverEvent().getType());
            byte[] event = SerializationUtils.serialize(r.getDeliverEvent());
            buf.writeByte(Grid.P2P);
            LOG.info((""+ (r.getDeliverEvent().getType() == Grid.PUTREQ)));
            if(r.getDeliverEvent().getType() == Grid.GETREQ) {
                LOG.info("...which contains a GET request...");
                buf.writeByte(Grid.GETREQ);
            }
            else if(r.getDeliverEvent().getType() == Grid.PUTREQ) {
                LOG.info("...which contains  a PUT request...");
                buf.writeByte(Grid.PUTREQ);
            }
            Serializers.toBinary(r.header, buf);
            buf.writeInt(event.length);
            buf.writeBytes(event);
        }

    }

    @Override
    public Object fromBinary(ByteBuf buf, Optional<Object> hint) {
        byte type = buf.readByte(); // 1 byte
        switch (type) {
            case Grid.BEB: {
                THeader header = (THeader) Serializers.fromBinary(buf, Optional.absent()); // 1 byte serialiser id + 16 bytes THeader
                int eventSize = buf.readInt();
                byte[] event = new byte[eventSize];
                buf.readBytes(event);
                BEBDeliver bebDeliver = SerializationUtils.deserialize(event);
                return new BEBMessage(header, bebDeliver); // 18 bytes total, check
            } 
            
            case Grid.P2P: {
                byte requestType = buf.readByte();
                THeader header = (THeader) Serializers.fromBinary(buf, Optional.absent()); // 1 byte serialiser id + 16 bytes THeader
                int eventSize = buf.readInt();
                byte[] event = new byte[eventSize];
                buf.readBytes(event);
                if(requestType == Grid.GETREQ) {
                    GetRequest get = (GetRequest) SerializationUtils.deserialize(event);
                    return new Pp2pMessage(header, get); // 18 bytes total, check
                }
                else if(requestType == Grid.PUTREQ) {

                    PutRequest put = (PutRequest) SerializationUtils.deserialize(event);
                    return new Pp2pMessage(header, put); // 18 bytes total, check
                }
            }
            
            
        }
        return null;
    }
}
