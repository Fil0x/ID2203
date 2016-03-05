package host.register;

import kth.id2203.register.event.ArReadResponse;
import kth.id2203.register.event.ArWriteRequest;
import kth.id2203.register.event.ArWriteResponse;
import kth.id2203.register.port.AtomicRegister;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;

public class ReadImposeWriteConsultMajorityTestApp extends ComponentDefinition {

	private Positive<AtomicRegister> nnar = requires(AtomicRegister.class);

	private boolean isReader;

	public ReadImposeWriteConsultMajorityTestApp(Init init) {
		System.out.println("Initiate Register Test App Reader: " + init.isReader);
		this.isReader = init.isReader;
	}

	public static class Init extends se.sics.kompics.Init<ReadImposeWriteConsultMajorityTestApp> {
		public final boolean isReader;

		public Init(boolean isReader) {
			this.isReader = isReader;
		}
	}

	Handler<Start> startHandler = new Handler<Start>() {
		public void handle(Start event) {
			if (isReader) {
				System.out.println("Trigger read request ");
				trigger(new ArWriteRequest(10, 100), nnar);
			}
		}
	};

	Handler<ArReadResponse> readResponseHanlder = new Handler<ArReadResponse>() {

		@Override
		public void handle(ArReadResponse event) {
			System.out.println(">>>>> Got Read Value ");
			
		}
		
	};
	
	Handler<ArWriteResponse> writeResponseHanlder = new Handler<ArWriteResponse>() {

		@Override
		public void handle(ArWriteResponse event) {
			System.out.println(">>>> Got Write Value ");
			
		}
		
	};
	
	{
		subscribe(startHandler, control);
		subscribe(readResponseHanlder, nnar);
		subscribe(writeResponseHanlder, nnar);
	}
}
