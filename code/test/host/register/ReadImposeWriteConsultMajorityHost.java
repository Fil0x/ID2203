package host.register;

import java.util.List;

import kth.id2203.beb.BroadcastComponent;
import kth.id2203.beb.port.BroadcastPort;
import kth.id2203.data.Grid;
import kth.id2203.network.TAddress;
import kth.id2203.pp2p.Pp2pLink;
import kth.id2203.pp2p.port.Pp2pLinkPort;
import kth.id2203.register.ReadImposeWriteConsultMajority;
import kth.id2203.register.port.AtomicRegister;
import se.sics.kompics.Channel;
import se.sics.kompics.Component;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Init;
import se.sics.kompics.network.Network;
import se.sics.kompics.network.netty.NettyInit;
import se.sics.kompics.network.netty.NettyNetwork;

public class ReadImposeWriteConsultMajorityHost extends ComponentDefinition {

	public ReadImposeWriteConsultMajorityHost() {
		List<TAddress> all = Grid.getAllNodes();
		for (TAddress self : all) {
			Component network = create(NettyNetwork.class, new NettyInit(self));
			Component pl = create(Pp2pLink.class, new Pp2pLink.Init(self));
			connect(pl.getNegative(Network.class), network.getPositive(Network.class), Channel.TWO_WAY);
			
			Component beb = create(BroadcastComponent.class, new BroadcastComponent.Init(self, all));
			connect(beb.getNegative(Pp2pLinkPort.class), pl.getPositive(Pp2pLinkPort.class));
			
			Component regsiter = create(ReadImposeWriteConsultMajority.class, new ReadImposeWriteConsultMajority.Init(self, all.size()));
			connect(regsiter.getNegative(BroadcastPort.class), beb.getPositive(BroadcastPort.class));
			connect(regsiter.getNegative(Pp2pLinkPort.class), pl.getPositive(Pp2pLinkPort.class));
			
			Component testapp = create(ReadImposeWriteConsultMajorityTestApp.class, new ReadImposeWriteConsultMajorityTestApp.Init(self.getPort() == 20008 ? true : false));
			connect(testapp.getNegative(AtomicRegister.class), regsiter.getPositive(AtomicRegister.class));
		}
	}
}
