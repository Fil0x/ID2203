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
import se.sics.kompics.simulator.adaptor.distributions.extra.BasicIntSequentialDistribution;
import se.sics.kompics.simulator.events.system.StartNodeEvent;

public class NodeStructureScenario {

	static Operation1 startParentNodeOp = new Operation1<StartNodeEvent, Integer>() {

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
				SimulationScenario.StochasticProcess parentNode = new SimulationScenario.StochasticProcess() {
					{
						eventInterArrivalTime(constant(1000));
						raise(1, startParentNodeOp, new BasicIntSequentialDistribution(1));
					}
				};

				parentNode.start();
				terminateAfterTerminationOf(10000, parentNode);
			}

		};

		return scenario;
	}
}
