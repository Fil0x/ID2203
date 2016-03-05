package kth.id2203.data;



import java.util.ArrayList;
import java.util.List;

import kth.id2203.network.TAddress;

public class ReplicationGroup {

    private List<TAddress> group;
    private int lowerBound;
    private int upperBound;

    public ReplicationGroup(int lowerBound, int upperBound) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        group = new ArrayList<>();
    }

    public void addToGroup(TAddress newNode) {
        group.add(newNode);
    }

    public boolean inRange(int key) {
        return lowerBound <= key && key < upperBound;
    }

    public boolean contains(TAddress addr) {
        return group.contains(addr);
    }

    public List<TAddress> getGroup() {
        return group;
    }
}
