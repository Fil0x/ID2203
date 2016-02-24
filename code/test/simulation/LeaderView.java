package simulation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.primitives.Ints;

import events.View;
import network.VAddress;
import network.VMessage;
import se.sics.kompics.ClassMatchedHandler;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Positive;
import se.sics.kompics.network.Network;
import se.sics.kompics.timer.Timer;

public class LeaderView extends ComponentDefinition {

	private static final Logger LOG = LoggerFactory.getLogger(LeaderView.class);
	
	Positive<Network> net = requires(Network.class);
	
	public LeaderView(){
		
	}
	
	ClassMatchedHandler<View, VMessage> viewHandler = new ClassMatchedHandler<View, VMessage>() {
        @Override
        public void handle(View content, VMessage context) {
            int leaderId = Ints.fromByteArray(context.getSource().getId());
            StringBuilder sb = new StringBuilder();
            sb.append("\n");
            sb.append("Leader View : ").append(leaderId).append("\n");
            sb.append("Nodes: ");
            for(VAddress v: content.view) {
            	sb.append(Ints.fromByteArray(v.getId())).append(" ");
            }
            sb.append("\n");
            
            LOG.info(sb.toString());
        }
    };
    
    {
		subscribe(viewHandler, net);
	}
}
