package simulation.register;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kth.id2203.network.TMessage;
import kth.id2203.register.event.ArReadRequest;
import kth.id2203.register.event.ArReadResponse;
import kth.id2203.register.event.ArWriteRequest;
import kth.id2203.register.event.ArWriteResponse;
import kth.id2203.register.port.AtomicRegister;
import se.sics.kompics.ClassMatchedHandler;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Positive;
import se.sics.kompics.network.Network;
import se.sics.kompics.simulator.util.GlobalView;
import se.sics.kompics.timer.Timer;
import simulation.register.event.Get;
import simulation.register.event.Put;

public class RegistryApp extends ComponentDefinition {

	private static final Logger log = LoggerFactory.getLogger(RegistryApp.class);
	
	private Positive<AtomicRegister> nnar = requires(AtomicRegister.class);

	Positive<Network> network = requires(Network.class);
	Positive<Timer> timer = requires(Timer.class);
	
	private String previousReadValue;
	
	ClassMatchedHandler<Put, TMessage> putHandler = new ClassMatchedHandler<Put, TMessage>() {

        @Override
        public void handle(Put event, TMessage context) {
        	log.info("Receive Put request [key: " + event.getKey() + ", value:" + event.getValue() + "]");
        	trigger(new ArWriteRequest(event.getKey(), event.getValue()), nnar);
        }
    };
    
    ClassMatchedHandler<Get, TMessage> getHandler = new ClassMatchedHandler<Get, TMessage>() {

        @Override
        public void handle(Get event, TMessage context) {
        	log.info("Receive Get request [key: " + event.getKey() + "]");
        	trigger(new ArReadRequest(event.getKey()), nnar);
        }
    };
    
	Handler<ArReadResponse> readResponseHanlder = new Handler<ArReadResponse>() {

		@Override
		public void handle(ArReadResponse event) {
			log.info("Got Read Value " + event.getValue());
			GlobalView gv = config().getValue("simulation.globalview", GlobalView.class);
			
			String simulationType = gv.getValue("simulation.register.type", String.class);
			if(simulationType.equals("SIMPLE")) {
				gv.setValue("simulation.register.read_response_value", event.getValue());
				 if(!event.getValue().equals(WriteAfterReadAllScenario.DATA_VALUE)) {
		        	 gv.setValue("simulation.register.read_same_value", false);
		        }
			} else if(simulationType.equals("WRITE_READ")) {
				 if(!event.getValue().equals(WriteAfterReadAllScenario.DATA_VALUE)) {
		        	 gv.setValue("simulation.register.read_same_value", false);
		        }
				gv.setValue("simulation.register.readers_resopnse_count", gv.getValue("simulation.register.readers_resopnse_count", Integer.class) + 1);
			} else if(simulationType.equals("2WRITES_LAST_READ")) {
				
				if(previousReadValue != null && previousReadValue.equals(event.getValue())) {
					gv.setValue("simulation.register.read_last_write", false);
		        }
				gv.setValue("simulation.register.readers_resopnse_count", gv.getValue("simulation.register.readers_resopnse_count", Integer.class) + 1);
				
				if(previousReadValue == null) {
					previousReadValue = event.getValue();
				}
			}
  
		}
		
	};
	
	Handler<ArWriteResponse> writeResponseHanlder = new Handler<ArWriteResponse>() {

		@Override
		public void handle(ArWriteResponse event) {
			log.info("Got Write Response ");
			GlobalView gv = config().getValue("simulation.globalview", GlobalView.class);
	        gv.setValue("simulation.register.write_response_received", true);
		}
		
	};

	
	{
		subscribe(readResponseHanlder, nnar);
		subscribe(writeResponseHanlder, nnar);
		subscribe(putHandler, network);
		subscribe(getHandler, network);
	}

}