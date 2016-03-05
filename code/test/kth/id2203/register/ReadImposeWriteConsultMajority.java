package kth.id2203.register;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kth.id2203.beb.event.BEBDeliver;
import kth.id2203.beb.event.BEBroadcast;
import kth.id2203.beb.port.BroadcastPort;
import kth.id2203.message.MessagePayload;
import kth.id2203.network.TAddress;
import kth.id2203.pp2p.event.P2PAckDeliver;
import kth.id2203.pp2p.event.P2PAckSend;
import kth.id2203.pp2p.port.Pp2pLinkPort;
import kth.id2203.register.event.ArReadRequest;
import kth.id2203.register.event.ArReadResponse;
import kth.id2203.register.event.ArWriteRequest;
import kth.id2203.register.event.ArWriteResponse;
import kth.id2203.register.port.AtomicRegister;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;

public class ReadImposeWriteConsultMajority extends ComponentDefinition {
	private static final Logger log = LoggerFactory.getLogger(ReadImposeWriteConsultMajority.class);

	private static final String READ_OPT = "READ";
	private static final String WRITE_OPT = "WRITE";
	
	private static final String READ_ACK = "VALUE";
	private static final String WRITE_ACK = "ACK";
	
	private Positive<BroadcastPort> beb = requires(BroadcastPort.class);
	private Positive<Pp2pLinkPort> pp2p = requires(Pp2pLinkPort.class);
	private Negative<AtomicRegister> nnar = provides(AtomicRegister.class);

	private TAddress self;
	private Integer numNodes;
	
	private List<ReadInfo> readlist;
	private Integer ts, wr, val;
	private Integer rid;
	private Integer acks;
	private boolean reading;
	
    private Integer readval;
    private Integer rr;
    private Integer maxts;
    
	private Integer writeval;

	public ReadImposeWriteConsultMajority(Init init) {
		log.info("Initiate ReadImposeWriteConsultMajority Component");
		this.self = init.self;
		this.numNodes = init.numNodes;

		this.ts = 0;
        this.val = 0; // This is the value of the register we are trying to read
        this.wr = 0;
        
		this.readlist = new ArrayList<ReadInfo>();
		this.rid = 0;
		this.acks = 0;
		this.reading = false;
		
		
	}

	

	

	private Handler<ArReadRequest> handleReadRequest = new Handler<ArReadRequest>() {

		/**
		 * upon event ⟨ nnar, Read ⟩ do
         *       rid := rid + 1;
         *       acks := 0;
         *       readlist := [⊥]N ;
         *       reading := TRUE;
         *       trigger ⟨ beb, Broadcast | [READ, rid] ⟩;
		 */
		@Override
		public void handle(ArReadRequest event) {
			System.out.println("R1. Handle ArReadRequest at " + self.getIp() + ":" + self.getPort());

			rid++;
			acks = 0;
			readlist.clear();
			reading = true;

			MessagePayload message = new MessagePayload(READ_OPT + "," + rid);
			trigger(new BEBroadcast(message), beb);

		}

	};

	private Handler<BEBDeliver> bebDeliver = new Handler<BEBDeliver>() {

		@Override
		public void handle(BEBDeliver event) {
			System.out.println("R2. Handle Beb Deliver message [" + event.getMessage().getPayload() + "] at "
					+ self.getIp() + ":" + self.getPort());
			String request = event.getMessage().getPayload();
			if(request.contains(READ_OPT)) {
				handleRead(event.getFrom(), request);
			} else if(request.contains(WRITE_OPT)) {
				handleWrite(event.getFrom(), request);
			}
			
		}
		
		/**
		 * upon event ⟨ beb, Deliver | p, [READ, r] ⟩ do
         *       trigger ⟨ pl, Send | p, [VALUE, r, ts, wr, val] ⟩;
         *       
		 * @param src
		 * @param readRequest
		 */
		private void handleRead(TAddress src, String readRequest) {
			System.out.println("	Handle READ Request: " + readRequest);
			String r = readRequest.split(",")[1];
			
			MessagePayload message = new MessagePayload("VALUE," + r + "," + ts + "," + wr + "," + val);
			trigger(new P2PAckSend(src, message), pp2p);
			
		}
		
		/**
		 * upon event ⟨ beb, Deliver | p, [WRITE, r, ts′, wr′, v′] ⟩ do
		 * 		if (ts′, wr′) is larger than (ts, wr) then
		 * 			(ts, wr, val) := (ts′, wr′, v′);
		 * 		trigger ⟨ pl, Send | p, [ACK, r] ⟩;
		 * 
		 * @param writeRequest
		 */
		private void handleWrite(TAddress src, String writeRequest) {
			System.out.println("	Handle WRITE Request: " + writeRequest);
			String[] request = writeRequest.split(",");
			Integer r = Integer.valueOf(request[1]);
			Integer ts1 = Integer.valueOf(request[2]);
			Integer wr1 = Integer.valueOf(request[3]);
			Integer v1 = Integer.valueOf(request[4]);
			
			if((ts1 > ts) && (wr1 > wr)) {
				ts = ts1;
				wr = wr1;
				val = v1;
			}
			
			MessagePayload message = new MessagePayload("ACK," + r);
			trigger(new P2PAckSend(src, message), pp2p);
			
		}

	};
	
	private Handler<P2PAckDeliver> handleP2PAckMessage = new Handler<P2PAckDeliver>() {
		@Override
		public void handle(P2PAckDeliver event) {
			String request = event.getMessage().getPayload();
			if(request.contains(WRITE_ACK)) {
				handleWriteAck(event.getFrom(), event.getMessage().getPayload());
			} else if(request.contains(READ_ACK)) {
				handleReadAck(event.getFrom(), event.getMessage().getPayload());
			}

		}
		
		/**
		 * upon event ⟨ pl, Deliver | q, [VALUE, r, ts′, wr′, v′] ⟩ such that r = rid do
		 * 		readlist[q] := (ts′, wr′, v′);
		 * 		if #(readlist) > N/2 then
		 * 			(maxts, rr, readval) := highest(readlist);
		 * 			readlist := [⊥]N ;
		 * 			if reading = TRUE then
		 * 				trigger ⟨ beb, Broadcast | [WRITE, rid, maxts, rr, readval] ⟩;
		 * 			else
		 * 				trigger ⟨ beb, Broadcast | [WRITE, rid, maxts + 1, rank(self), writeval] ⟩;
		 * 
		 */
		private void handleReadAck(TAddress src, String ack) {
			System.out.println("R3. Hanlde Pp2p Deliver Ack message [" + ack + "] of "
					+ src.getIp() + ":" + src.getPort());
			String[] request = ack.split(",");
			Integer r = Integer.valueOf(request[1]);
			Integer ts1 = Integer.valueOf(request[2]);
			Integer wr1 = Integer.valueOf(request[3]);
			Integer v1 = Integer.valueOf(request[4]);

			if(r.equals(rid)) {
				readlist.add(new ReadInfo(ts1, wr1, v1, src.hashCode()));
				if (readlist.size() > (numNodes / 2)) {
					Collections.sort(readlist, new Comparator<ReadInfo>() {
						@Override
						public int compare(ReadInfo o1, ReadInfo o2) {
							if (o1.getTs() < o2.getTs())
								return -1;
							else if (o1.getTs() > o2.getTs())
								return 1;
							else
								return (o1.getNodeid() < o2.getNodeid()) ? -1 : 1;
						}
					});
					
					ReadInfo highest = readlist.get(readlist.size() - 1);
					rr = new Integer(highest.getWr());
	                readval = new Integer(highest.getVal());
	                maxts = new Integer(highest.getTs());
	                
	                readlist.clear();
	                
					if (reading) {
						MessagePayload message = new MessagePayload(WRITE_OPT + "," + rid + "," + maxts + "," + rr + "," + readval);
						trigger(new BEBroadcast(message), beb);
					} else {
						MessagePayload message = new MessagePayload(WRITE_OPT + "," + rid + "," + (maxts + 1) + "," + self.hashCode() + "," + writeval);
						trigger(new BEBroadcast(message), beb);
					}
				}
				
			}
		}
		
		/**
		 * upon event ⟨ pl, Deliver | q, [ACK, r] ⟩ such that r = rid do
		 * 	acks := acks + 1;
		 * 	if acks > N/2 then
		 * 		acks := 0;
		 * 		if reading = TRUE then
		 * 			reading := FALSE;
		 * 			trigger ⟨ nnar, ReadReturn | readval ⟩;
		 * 		else
		 * 			trigger ⟨ nnar, WriteReturn ⟩;
		 * 
		 * @param src
		 * @param ack
		 */
		private void handleWriteAck(TAddress src, String ack) {
			System.out.println("R4. Hanlde Pp2p Deliver Ack message [" + ack + "] of "
					+ src.getIp() + ":" + src.getPort());
			acks += 1;
			if(acks > (numNodes / 2)) {
				acks = 0;
				
				if(reading) {
					reading = false;
					trigger(new ArReadResponse(), nnar);
				} else {
					trigger(new ArWriteResponse(), nnar);
				}
			}
			
		}

	};

	private Handler<ArWriteRequest> handleWriteRequest = new Handler<ArWriteRequest>() {

		/**
		 * upon event ⟨ nnar, Write | v ⟩ do
         *       rid := rid + 1;
         *       writeval := v;
         *       acks := 0;
         *       readlist := [⊥]N ;
         *       trigger ⟨ beb, Broadcast | [READ, rid] ⟩;
		 */
		@Override
		public void handle(ArWriteRequest event) {
			System.out.println("W1. Handle ArWriteRequest at " + self.getIp() + ":" + self.getPort());
			rid++;
			writeval = event.getValue();
			acks = 0;
			readlist.clear();
			
			MessagePayload message = new MessagePayload(READ_OPT + "," + rid);
			trigger(new BEBroadcast(message), beb);
		}

	};
	
	

	{
		subscribe(handleReadRequest, nnar);
		subscribe(bebDeliver, beb);
		subscribe(handleP2PAckMessage, pp2p);

		subscribe(handleWriteRequest, nnar);
	}
	
	public static class Init extends se.sics.kompics.Init<ReadImposeWriteConsultMajority> {
		public final TAddress self;
		public final Integer numNodes;
		public Init(TAddress self, Integer numNodes) {
			this.self = self;
			this.numNodes = numNodes;
		}
	}
}
