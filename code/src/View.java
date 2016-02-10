import se.sics.kompics.KompicsEvent;

import java.util.ArrayList;
import java.util.List;

public class View implements KompicsEvent {
    public List<VAddress> view = new ArrayList<>();

    public View(List<VAddress> view){
        this.view = view;
    }
}
