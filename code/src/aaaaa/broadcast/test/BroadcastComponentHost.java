package aaaaa.broadcast.test;

import java.util.List;

import aaaaa.broadcast.component.BroadcastComponent;
import aaaaa.broadcast.port.BroadcastPort;
import network.TAddress;
import se.sics.kompics.Channel;
import se.sics.kompics.Component;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.network.Network;
import se.sics.kompics.network.netty.NettyInit;
import se.sics.kompics.network.netty.NettyNetwork;

public class BroadcastComponentHost extends ComponentDefinition {
	public BroadcastComponentHost(Init init) {

		Component network = create(NettyNetwork.class, new NettyInit(init.self));
		Component boradcast = create(BroadcastComponent.class, new BroadcastComponent.Init(init.self, init.all));

		connect(boradcast.getNegative(Network.class), network.getPositive(Network.class), Channel.TWO_WAY);

		Component brodcastTest = create(BroadcastTest.class,
				new BroadcastTest.Init(init.self.getPort() == 20005 ? true : false));
		connect(brodcastTest.getNegative(BroadcastPort.class), boradcast.getPositive(BroadcastPort.class));

	}

	public static class Init extends se.sics.kompics.Init<BroadcastComponentHost> {
		public final TAddress self;
		public List<TAddress> all;

		public Init(TAddress self, List<TAddress> all) {
			this.self = self;
			this.all = all;
		}
	}
}
