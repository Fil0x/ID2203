package epfd.component;


import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timeout;

public class CheckTimeout extends Timeout {

    protected CheckTimeout(ScheduleTimeout request) {
        super(request);
    }
}
