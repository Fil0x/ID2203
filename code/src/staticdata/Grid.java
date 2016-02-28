package staticdata;


import com.google.common.primitives.Ints;
import network.VAddress;

import java.util.ArrayList;
import java.util.List;

public class Grid {

    public static List<VAddress> pi = null;

    public static List<VAddress> getAllNodes(VAddress base) {
        // Create the static 9 nodes
        List<VAddress> pi = new ArrayList<>();

        for (int i = 100; i < 110; i++) {
            pi.add(base.withVirtual(Ints.toByteArray(i)));
        }

        Grid.pi = pi;
        return pi;
    }
}
