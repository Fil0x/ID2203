package staticdata;


import network.TAddress;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class Grid {

    public static List<TAddress> pi = null;

    public static List<TAddress> getAllNodes() {

        if(pi != null)
            return pi;

        try {
            // Create the static 6 nodes
            List<TAddress> nodes = new ArrayList<>();
            InetAddress baseIP = InetAddress.getByName("127.0.0.1");
            int basePort = 20000;

            for (int i = 0; i < 6; i++) {
                nodes.add(new TAddress(baseIP, basePort + i));
            }

            Grid.pi = nodes;
            return nodes;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        return null;
    }
}
