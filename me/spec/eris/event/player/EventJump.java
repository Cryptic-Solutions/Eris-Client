package me.spec.eris.event.player;

import me.spec.eris.event.Event;

public class EventJump extends Event {
    public double motionY;

    public EventJump(double motionY) {
        this.motionY = motionY;
    }

    public void setMotionY(double d) {
        this.motionY = d;
    }

    public double getMotionY() {
        return motionY;
    }
}
