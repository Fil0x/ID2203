package simulation;

import se.sics.kompics.Channel;
import se.sics.kompics.Component;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Init;
import se.sics.kompics.Positive;
import se.sics.kompics.network.Network;
import se.sics.kompics.timer.Timer;

public class LeaderViewParent extends ComponentDefinition {
	
	Positive<Network> network = requires(Network.class);
    Positive<Timer> timer = requires(Timer.class);

    public LeaderViewParent() {
    	Component nodeView = create(LeaderView.class, Init.NONE);
    	connect(nodeView.getNegative(Network.class), network, Channel.TWO_WAY);
    }
}
