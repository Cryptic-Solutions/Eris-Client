package me.spec.eris.client.modules.combat;

import me.spec.eris.Eris;
import me.spec.eris.api.event.Event;
import me.spec.eris.api.module.Module;
import me.spec.eris.api.module.ModuleCategory;
import me.spec.eris.client.events.player.EventUpdate;
import me.spec.eris.client.modules.movement.Scaffold;
import me.spec.eris.client.modules.player.AntiVoid;

public class TargetStrafe extends Module {
    public int direction = 1;
    public TargetStrafe(String racism) {
        super("TargetStrafe", ModuleCategory.COMBAT, racism);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof EventUpdate) {
            EventUpdate eu = (EventUpdate)e;
            if (eu.isPre()) {
                boolean changeDirectionInFight = false;
                Killaura aura = (Killaura) Eris.getInstance().getModuleManager().getModuleByClass(Killaura.class);
                if (mc.thePlayer.ticksExisted % 35 == 0 && Killaura.target != null && aura.targetList.size() > 0) {
                    if (mc.thePlayer.getDistanceToEntity(Killaura.target) <= aura.range.getValue() - .25f) changeDirectionInFight = true;
                }
                boolean thevoid = !AntiVoid.isBlockUnder() && mc.thePlayer.ticksExisted % 3 == 0;
                if (mc.thePlayer.isCollidedHorizontally || thevoid || !thevoid && changeDirectionInFight) {
                    if (direction == 1) {
                        direction = -1;
                    } else {
                        direction = 1;
                    }
                }
                if (canStrafe()) {
                    mc.thePlayer.movementInput.setForward(0);
                }
            }
        }
    }

    public boolean canStrafe() {
        return mc.gameSettings.keyBindJump.isKeyDown() && !Eris.getInstance().getModuleManager().isEnabled(Scaffold.class) && Killaura.currentEntity != null;
    }

}