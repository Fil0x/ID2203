package beb.event;

import network.TAddress;
import network.THeader;
import network.TMessage;
import se.sics.kompics.network.Transport;

public class BEBMessage extends TMessage {

	private final BEBDeliver event;

	public BEBMessage(TAddress src, TAddress dst, BEBDeliver event) {
        super(src, dst, Transport.TCP);

        this.event = event;
	}

    public BEBDeliver getDeliverEvent() {
        return event;
    }
}
