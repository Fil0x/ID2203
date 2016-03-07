package simulation.register.event;

import se.sics.kompics.KompicsEvent;

public class Put implements KompicsEvent {

	private final Integer key;
	
	private final String value;
	
	public Put(Integer key, String value) {
		this.key = key;
		this.value = value;
	}

	public Integer getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}
	
}