package simulation;

import java.util.List;

import components.Node;
import components.NodeParent;
import network.TAddress;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Init;
import se.sics.kompics.network.Address;
import se.sics.kompics.simulator.SimulationScenario;
import se.sics.kompics.simulator.adaptor.Operation;
import se.sics.kompics.simulator.adaptor.Operation1;
import se.sics.kompics.simulator.adaptor.distributions.ConstantDistribution;
import se.sics.kompics.simulator.adaptor.distributions.extra.BasicIntSequentialDistribution;
import se.sics.kompics.simulator.events.system.StartNodeEvent;
import staticdata.Grid;

public class NodeStructureScenario {

	static Operation1 startLeader = new Operation1<StartNodeEvent, Integer>() {

		@Override
		public StartNodeEvent generate(final Integer self) {
			return new StartNodeEvent() {

                List<TAddress> allNodes = Grid.getAllNodes();
                TAddress selfAdr = allNodes.get(0);

				@Override
				public Class getComponentDefinition() {
					return Node.class;
				}

				@Override
				public Init getComponentInit() {
                    return new Node.Init(selfAdr, allNodes, true, null);
                    // return new NodeParent.Init(selfAdr, allNodes, true, null);
				}

				@Override
				public Address getNodeAddress() {
					return selfAdr;
				}

			};
		}

	};


	static Operation1 startSlave = new Operation1<StartNodeEvent, Integer>() {

        @Override
        public StartNodeEvent generate(final Integer n) {
            return new StartNodeEvent() {

                List<TAddress> allNodes = Grid.getAllNodes();
                TAddress selfAdr = allNodes.get(n);

                @Override
                public Address getNodeAddress() {
                    return selfAdr;
                }

                @Override
                public Class<? extends ComponentDefinition> getComponentDefinition() {
                    return Node.class;
                    // return NodeParent.class;
                }

                @Override
                public Init getComponentInit() {
                    return new Node.Init(selfAdr, allNodes, false, allNodes.get(0));
                    //return new NodeParent.Init(selfAdr, allNodes, false, allNodes.get(0));
                }
            };
        }
    };

	public static SimulationScenario simpleNodeStructure() {
		SimulationScenario scenario = new SimulationScenario() {
			{
                SimulationScenario.StochasticProcess spawnLeader = new SimulationScenario.StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(1000));
                        raise(1, startLeader, new BasicIntSequentialDistribution(1));
                    }
                };

				final SimulationScenario.StochasticProcess slaveNodes = new SimulationScenario.StochasticProcess() {
					{
						eventInterArrivalTime(constant(1000));
                        raise(5, startSlave, new BasicIntSequentialDistribution(1));
					}
				};

				spawnLeader.start();
				slaveNodes.startAfterTerminationOf(1000, spawnLeader);
				terminateAfterTerminationOf(10000, spawnLeader);
			}

		};

		return scenario;
	}
}