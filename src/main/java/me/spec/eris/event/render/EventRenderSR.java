package me.spec.eris.event.render;

import me.spec.eris.event.Event;
import net.minecraft.client.gui.ScaledResolution;

public class EventRenderSR extends Event {

    public ScaledResolution sr;
    private int width, height;

    public EventRenderSR(ScaledResolution sr, float ticks, int width, int height) {
        this.partialTicks = ticks;
        this.sr = sr;
        this.width = width;
        this.height = height;
    }

    float partialTicks;

    public float getTicks() {
        return this.partialTicks;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

}
