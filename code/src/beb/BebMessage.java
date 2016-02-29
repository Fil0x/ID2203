package beb;


import network.TAddress;
import ports.BebDeliver;

public class BebMessage extends BebDeliver {

    private final String message;

    private static final long serialVersionUID = 5491596109178800519L;

    public BebMessage(TAddress source, String message) {
        super(source);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
