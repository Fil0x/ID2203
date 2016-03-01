package domain;


import network.TAddress;

import java.util.ArrayList;
import java.util.List;

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
}
