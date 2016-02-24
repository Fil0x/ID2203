package simulation;

import components.NodeParent;
import se.sics.kompics.Init;
import se.sics.kompics.network.Address;
import se.sics.kompics.simulator.SimulationScenario;
import se.sics.kompics.simulator.adaptor.Operation1;
import se.sics.kompics.simulator.adaptor.distributions.extra.BasicIntSequentialDistribution;
import se.sics.kompics.simulator.events.system.StartNodeEvent;

public class NodeStructureScenario {

	static Operation1 startNodeParentOp = new Operation1<StartNodeEvent, Integer>() {

		@Override
		public StartNodeEvent generate(Integer arg0) {
			return new StartNodeEvent() {

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
					// TODO Auto-generated method stub
					return null;
				}

			};
		}

	};

	public static SimulationScenario simpleNodeStructure() {
		SimulationScenario scenario = new SimulationScenario() {
			{
				SimulationScenario.StochasticProcess nodeParent = new SimulationScenario.StochasticProcess() {
					{
						eventInterArrivalTime(constant(1000));
						raise(1, startNodeParentOp, new BasicIntSequentialDistribution(1));
					}
				};
				
				nodeParent.start();
			}

		};

		return scenario;
	}
}
