package me.spec.eris.client.modules.render;

import me.spec.eris.api.event.Event;
import me.spec.eris.api.module.Module;
import me.spec.eris.api.module.ModuleCategory;
import me.spec.eris.api.value.types.ModeValue;
import me.spec.eris.client.events.client.EventPacket;
import me.spec.eris.client.modules.movement.NoSlowDown;

public class Animations extends Module {

    private ModeValue<Mode> mode = new ModeValue<Mode>("Mode", Mode.WATCHDOG, this);
    private enum Mode {WATCHDOG}
    public Animations(String racism) {
        super("Animations", ModuleCategory.RENDER, racism);
    }

    @Override
    public void onEvent(Event e) {
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
