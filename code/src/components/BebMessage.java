package components;


import network.VAddress;
import ports.BebDeliver;
import se.sics.kompics.Event;

import java.io.Serializable;

public class BebMessage extends BebDeliver {

    private final String message;

    private static final long serialVersionUID = 5491596109178800519L;

    public BebMessage(VAddress source, String message) {
        super(source);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
