package simulation.register.event;

import se.sics.kompics.KompicsEvent;

public class Get implements KompicsEvent {

	private final Integer key;
	
	public Get(Integer key) {
		this.key = key;
	}

	public Integer getKey() {
		return key;
	}
	
}