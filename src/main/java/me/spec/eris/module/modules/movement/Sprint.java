package me.spec.eris.module.modules.movement;

import me.spec.eris.event.Event;
import me.spec.eris.event.player.EventUpdate;
import me.spec.eris.module.Category;
import me.spec.eris.module.Module;

public class Sprint extends Module {

    public Sprint() {
        super("Sprint", Category.MOVEMENT);
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof EventUpdate) {
            mc.thePlayer.setSprinting(mc.thePlayer.isMoving());
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        mc.gameSettings.keyBindSprint.pressed = false;
        super.onDisable();
    }
}
