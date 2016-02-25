package simulation;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import components.NodeParent;
import network.VAddress;
import se.sics.kompics.Init;
import se.sics.kompics.network.Address;
import se.sics.kompics.simulator.SimulationScenario;
import se.sics.kompics.simulator.adaptor.Operation1;
import se.sics.kompics.simulator.adaptor.Operation2;
import se.sics.kompics.simulator.adaptor.distributions.extra.BasicIntSequentialDistribution;
import se.sics.kompics.simulator.events.system.StartNodeEvent;

public class NodeStructureScenario {

	static Operation1 startLeaderViewOp = new Operation1<StartNodeEvent, Integer>() {

		@Override
		public StartNodeEvent generate(final Integer self) {
			return new StartNodeEvent() {

				VAddress selfAdr;

				{
					try {
						selfAdr = new VAddress(InetAddress.getByName("192.193.0." + self), 10000);
					} catch (UnknownHostException ex) {
						throw new RuntimeException(ex);
					}
				}
				
				@Override
				public Class getComponentDefinition() {
					return LeaderViewParent.class;
				}

				@Override
				public Init getComponentInit() {
					return Init.NONE;
				}

				@Override
				public Address getNodeAddress() {
					return selfAdr;
				}
				
			};
		}
		
	};
	
	static Operation2 startParentNodeOp = new Operation2<StartNodeEvent, Integer, Integer>() {

		@Override
		public StartNodeEvent generate(final Integer self, final Integer nodeView) {
			return new StartNodeEvent() {
				VAddress selfAdr;
				VAddress nodeViewAdr;
				{
					try {
						selfAdr = new VAddress(InetAddress.getByName("192.193.0." + self), 10000);
						nodeViewAdr = new VAddress(InetAddress.getByName("192.193.0." + nodeView), 10000);
					} catch (UnknownHostException ex) {
						throw new RuntimeException(ex);
					}
				}

				@Override
				public Class getComponentDefinition() {
					return NodeParent.class;
				}

				@Override
				public Init getComponentInit() {
					return Init.NONE;
				}

				@Override
				public Address getNodeAddress() {
					return selfAdr;
				}

				@Override
				public Map<String, Object> initConfigUpdate() {
					HashMap<String, Object> config = new HashMap<>();
					config.put("network.node", selfAdr);
					return config;
				}

			};
		}

	};

	public static SimulationScenario simpleNodeStructure() {
		SimulationScenario scenario = new SimulationScenario() {
			{
				SimulationScenario.StochasticProcess nodeView = new SimulationScenario.StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(1000));
                        raise(5, startLeaderViewOp, new BasicIntSequentialDistribution(1));
                    }
                };

				SimulationScenario.StochasticProcess parentNode = new SimulationScenario.StochasticProcess() {
					{
						eventInterArrivalTime(constant(1000));
						raise(1, startParentNodeOp, new BasicIntSequentialDistribution(1), new BasicIntSequentialDistribution(1));
					}
				};
				
				nodeView.start();
				parentNode.startAfterTerminationOf(1000, nodeView);
				terminateAfterTerminationOf(10000, parentNode);
			}

		};

		return scenario;
	}
}
