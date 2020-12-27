package me.spec.eris.event;

import me.spec.eris.Eris;
import me.spec.eris.module.Module;
import net.minecraft.client.Minecraft;

public class Event {

    private final static Module antiCrash;
    private boolean cancelled;
    private boolean debugSecurity;

    static {
        antiCrash = Eris.instance.modules.getModuleByName("AntiCrash");
    }

    private Stage stage;

    public void call() {
        if (Eris.getInstance() == null) return;

        if (antiCrash.isToggled()) {
            for (Module m : Eris.instance.modules.getModules()) {
                if (m.isToggled() && Minecraft.getMinecraft().thePlayer != null) {
                    try {
                        m.onEvent(this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            for (Module m : Eris.instance.modules.getModules()) {
                if (m.isToggled() && Minecraft.getMinecraft().thePlayer != null) {
                    m.onEvent(this);
                }
            }
        }
    }

    public boolean getSecure() {
        return debugSecurity;
    }

    public void setSecurity(boolean secure) {
        this.debugSecurity = secure;
    }

    public enum Stage {PRE, POST}

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private Stage getStage() {
        return stage;
    }

    public void setCancelled() {
        cancelled = true;
    }

    public boolean isCancelled() {
        return cancelled;
    }

}
