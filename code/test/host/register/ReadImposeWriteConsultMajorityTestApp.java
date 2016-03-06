package host.register;

import java.util.UUID;

import kth.id2203.register.event.ArReadRequest;
import kth.id2203.register.event.ArReadResponse;
import kth.id2203.register.event.ArWriteRequest;
import kth.id2203.register.event.ArWriteResponse;
import kth.id2203.register.port.AtomicRegister;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;
import se.sics.kompics.timer.SchedulePeriodicTimeout;
import se.sics.kompics.timer.Timeout;
import se.sics.kompics.timer.Timer;

public class ReadImposeWriteConsultMajorityTestApp extends ComponentDefinition {

	private Positive<AtomicRegister> nnar = requires(AtomicRegister.class);

	Positive<Timer> timer = requires(Timer.class);
	
	private boolean isReader;
	
	private boolean isWriter;
	
	private UUID timerId;

	public ReadImposeWriteConsultMajorityTestApp(Init init) {
		System.out.println("Initiate Register Test App Reader: " + init.isReader);
		this.isReader = init.isReader;
		this.isWriter = init.isWriter;
	}

	public static class Init extends se.sics.kompics.Init<ReadImposeWriteConsultMajorityTestApp> {
		public final boolean isReader;
		public final boolean isWriter;

		public Init(boolean isReader, boolean isWriter) {
			this.isReader = isReader;
			this.isWriter = isWriter;
		}
	}

	Handler<Start> startHandler = new Handler<Start>() {
		public void handle(Start event) {
			if (isWriter) {
				System.out.println("Trigger Write request ");
				trigger(new ArWriteRequest(10, "Pradeep"), nnar);
			}
			
			if(isReader) {
				SchedulePeriodicTimeout spt = new SchedulePeriodicTimeout(10000, 10000);
				ReaderTimeout timeout = new ReaderTimeout(spt);
				spt.setTimeoutEvent(timeout);
				trigger(spt, timer);
				timerId = timeout.getTimeoutId();
			}
		}
	};

	Handler<ReaderTimeout> timeoutHandler = new Handler<ReaderTimeout>() {
		public void handle(ReaderTimeout event) {
			System.out.println("Trigger read request ");
			trigger(new ArReadRequest(10), nnar);
		}
	};
	
	Handler<ArReadResponse> readResponseHanlder = new Handler<ArReadResponse>() {

		@Override
		public void handle(ArReadResponse event) {
			System.out.println(">>>>> Got Read Value " + event.getValue());
			
		}
		
	};
	
	Handler<ArWriteResponse> writeResponseHanlder = new Handler<ArWriteResponse>() {

		@Override
		public void handle(ArWriteResponse event) {
			System.out.println(">>>> Got Write Response ");
			
		}
		
	};
	
	{
		subscribe(startHandler, control);
		subscribe(readResponseHanlder, nnar);
		subscribe(writeResponseHanlder, nnar);
		subscribe(timeoutHandler, timer);
	}
	
	public static class ReaderTimeout extends Timeout {
		public ReaderTimeout(SchedulePeriodicTimeout spt) {
			super(spt);
		}
	}
}
