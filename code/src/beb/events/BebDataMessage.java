package beb.events;


import network.TAddress;
import network.TMessage;
import se.sics.kompics.KompicsEvent;
import se.sics.kompics.network.Transport;

import java.io.Serializable;

public class BebDataMessage extends TMessage {

    public BebDataMessage(TAddress src, TAddress dst, Transport protocol) {
        super(src, dst, protocol);
    }
}
