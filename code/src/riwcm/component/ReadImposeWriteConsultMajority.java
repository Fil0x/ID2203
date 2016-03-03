package riwcm.component;


import beb.event.BEBroadcast;
import beb.port.BroadcastPort;
import network.TAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pp2p.event.Pp2pSend;
import pp2p.port.PerfectPointToPointLink;
import riwcm.event.ArReadRequest;
import riwcm.event.ArWriteRequest;
import riwcm.port.AtomicRegister;
import se.sics.kompics.*;
import se.sics.kompics.network.Address;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ReadImposeWriteConsultMajority extends ComponentDefinition {

    private static final Logger LOG = LoggerFactory.getLogger(ReadImposeWriteConsultMajority.class);

    private Positive<BroadcastPort> beb = requires(BroadcastPort.class);
    private Positive<PerfectPointToPointLink> pp2p = requires(PerfectPointToPointLink.class);
    private Negative<AtomicRegister> ar = provides(AtomicRegister.class);

    private TAddress self;
    private List<TAddress> all;

    private List<ReadInfo> readlist;
    private Integer ts, wr, val;
    private Integer acks;
    private Integer writeval;
    private Integer rid;
    private Integer readval;
    private Integer rr;
    private Integer maxts;
    private Integer numNodes;
    private boolean reading;
    private WriteArMessage wrmsg;

    public ReadImposeWriteConsultMajority(Init init) {
        this.self = init.getSelf();
        this.all = init.getAll();
        this.readlist = new ArrayList<>();
        this.numNodes = this.all.size();
        this.rid = 0;
        this.acks = 0;
        this.reading = false;
        this.ts = 0;
        this.val = 0;
        this.wr = 0;
        this.readval = 0;
        this.writeval = 0;
        this.maxts = -1;
        this.rr = 0;

        subscribe(startHandler, control);

        subscribe(readRequest, ar);
        subscribe(writeRequest, ar);

        subscribe(bebDeliver, beb);
        subscribe(writeValue, beb);

        subscribe(arDeliver, pp2p);
        subscribe(ackHandler, pp2p);
    }

    Handler<Start> startHandler = new Handler<Start>() {
        @Override
        public void handle(Start start) {
            LOG.info("Atomic register started: " + self.toString());
        }
    };

    Handler<ArReadRequest> readRequest = new Handler<ArReadRequest>() {
        @Override
        public void handle(ArReadRequest arReadRequest) {
            /*
            upon event ⟨ nnar, Read ⟩ do
                rid := rid + 1;
                acks := 0;
                readlist := [⊥]N ;
                reading := TRUE;
                trigger ⟨ beb, Broadcast | [READ, rid] ⟩;
            */
            rid++;
            acks = 0;
            readlist.clear();
            reading = true;
            trigger(new BEBroadcast(new ReadArMessage(self, rid)), beb);
        }
    };

    Handler<ReadArMessage> bebDeliver = new Handler<ReadArMessage>() {
        @Override
        public void handle(ReadArMessage event) {
            /*
            upon event ⟨ beb, Deliver | p, [READ, r] ⟩ do
                trigger ⟨ pl, Send | p, [VALUE, r, ts, wr, val] ⟩;
            */
            ArDataMessage msg = new ArDataMessage(event.getSource(), event.getRid(), ts, wr, val);
            trigger(new Pp2pSend(event.getSource(), msg), pp2p);
        }
    };

    Handler<ArDataMessage> arDeliver = new Handler<ArDataMessage>() {
        @Override
        public void handle(ArDataMessage event) {
            /*
            upon event ⟨ pl, Deliver | q, [VALUE, r, ts′, wr′, v′] ⟩ such that r = rid do
                readlist[q] := (ts′, wr′, v′);
                if #(readlist) > N/2 then
                    (maxts, rr, readval) := highest(readlist);
                    readlist := [⊥]N ;
                    if reading = TRUE then
                        trigger ⟨ beb, Broadcast | [WRITE, rid, maxts, rr, readval] ⟩;
                    else
                        trigger ⟨ beb, Broadcast | [WRITE, rid, maxts + 1, rank(self), writeval] ⟩;
            */

            if(event.getR().equals(rid)) {
                readlist.add(new ReadInfo(event.getTs(), event.getWr(), event.getVal(), event.getSource().hashCode()));
                if(readlist.size() > (numNodes / 2)) {
                    Collections.sort(readlist, new Comparator<ReadInfo>() {
                        @Override
                        public int compare(ReadInfo o1, ReadInfo o2) {
                            if(o1.getTs() < o2.getTs()) return -1;
                            else if(o1.getTs() > o2.getTs()) return 1;
                            else return (o1.getNodeid() < o2.getNodeid()) ? -1 : 1;
                        }
                    });

                    ReadInfo highest = readlist.get(readlist.size() - 1);
                    rr = new Integer(highest.getWr());
                    readval = new Integer(highest.getVal());
                    maxts = new Integer(highest.getTs());
                    readlist.clear();

                    if(reading) {
                        wrmsg = new WriteArMessage(self, rid, maxts, rr, readval);
                        trigger(new BEBroadcast(wrmsg), beb);
                    }
                    else {
                        wrmsg = new WriteArMessage(self, rid, maxts + 1, self.hashCode(), writeval);
                        trigger(new BEBroadcast(wrmsg), beb);
                    }
                }
            }
        }
    };

    private Handler<ArWriteRequest> writeRequest = new Handler<ArWriteRequest>() {
        @Override
        public void handle(ArWriteRequest event) {
            /*
            upon event ⟨ nnar, Write | v ⟩ do
                rid := rid + 1;
                writeval := v;
                acks := 0;
                readlist := [⊥]N ;
                trigger ⟨ beb, Broadcast | [READ, rid] ⟩;
            */

        }
    };

    private Handler<WriteArMessage> writeValue = new Handler<WriteArMessage>() {
        @Override
        public void handle(WriteArMessage event) {
            /*
            upon event ⟨ nnar, Write | v ⟩ do
                rid := rid + 1;
                writeval := v;
                acks := 0;
                readlist := [⊥]N ;
                trigger ⟨ beb, Broadcast | [READ, rid] ⟩;
             */
        }
    };

    private Handler<AckMessage> ackHandler = new Handler<AckMessage>() {
        @Override
        public void handle(AckMessage event) {
            /*
            upon event ⟨ nnar, Write | v ⟩ do
                rid := rid + 1;
                writeval := v;
                acks := 0;
                readlist := [⊥]N ;
                trigger ⟨ beb, Broadcast | [READ, rid] ⟩;
             */
        }
    };

    public static class Init extends se.sics.kompics.Init<ReadImposeWriteConsultMajority> {
        private final TAddress self;
        private List<TAddress> all;

        public Init(TAddress self, List<TAddress> all) {
            this.self = self;
            this.all = all;
        }

        public TAddress getSelf() {
            return self;
        }

        public List<TAddress> getAll() {
            return all;
        }
    }
}
