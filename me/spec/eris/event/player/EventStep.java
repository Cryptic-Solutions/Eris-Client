package me.spec.eris.event.player;

import me.spec.eris.event.Event;
import net.minecraft.entity.Entity;

public class EventStep extends Event {

    private Entity entity;
    private float stepHeight;
    private boolean pre;

    public EventStep(Entity entity, float stepHeight, boolean pre) {
        this.entity = entity;
        this.stepHeight = stepHeight;
        this.pre = pre;
    }

    public void setStepHeight(float stepHeight) {
        this.stepHeight = stepHeight;
    }

    public Entity getEntity() {
        return entity;
    }

    public float getStepHeight() {
        return stepHeight;
    }

    public boolean isPre() {
        return this.pre;
    }

    public void setStage(boolean pre) {
        this.pre = pre;
    }
} 
