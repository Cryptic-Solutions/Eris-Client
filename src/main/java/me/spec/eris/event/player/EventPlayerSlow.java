package me.spec.eris.event.player;

import me.spec.eris.event.Event;

public class EventPlayerSlow extends Event {
	
	private float speed;
	
	public EventPlayerSlow(float speed) {
		this.speed = speed;
	}

	public float getSpeed() {
		return speed;
	}
	
	public void setSpeed(float speed) {
		this.speed = speed;
	}
}
