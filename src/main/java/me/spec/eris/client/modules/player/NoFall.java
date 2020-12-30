package me.spec.eris.client.modules.player;

import me.spec.eris.Eris;
import me.spec.eris.api.event.Event;
import me.spec.eris.client.events.player.EventMove;
import me.spec.eris.client.events.player.EventUpdate;
import me.spec.eris.api.module.ModuleCategory;
import me.spec.eris.api.module.Module;
import me.spec.eris.client.modules.combat.Criticals;
import me.spec.eris.client.modules.combat.Killaura;
import me.spec.eris.client.modules.movement.Longjump;
import me.spec.eris.api.value.types.ModeValue;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;

public class NoFall extends Module {

    public NoFall() {
        super("NoFall", ModuleCategory.PLAYER);
    } 
    private ModeValue<Mode> mode = new ModeValue<Mode>("Mode", Mode.WATCHDOG, this);
    private enum Mode {WATCHDOG}
    
    private boolean fallen;
    
    @Override
    public void onEvent(Event e) { 
        if (e instanceof EventUpdate) {
            setMode(mode.getValue().toString());
    		switch (mode.getValue()) {
				case WATCHDOG: 
		            if(mc.thePlayer.fallDistance >= 2.75) {
		    			if (fallen && mc.thePlayer.isCollidedVertically) {
		    				mc.timer.timerSpeed = 1.0f;
		    				fallen = false;
		    			}

		        	}
				break;
    		}
        } 
        if (e instanceof EventMove) {
    		switch (mode.getValue()) {
				case WATCHDOG: 
		        	if (AntiVoid.isBlockUnder() && !Eris.instance.moduleManager.isEnabled(Longjump.class)) {
						if (mc.thePlayer.fallDistance > 2.4) { 
							Criticals crits = ((Criticals)Eris.getInstance().moduleManager.getModuleByClass(Criticals.class));
							crits.accumulatedFall = 0; 
							Killaura aura = ((Killaura)Eris.getInstance().moduleManager.getModuleByClass(Killaura.class));
							fallen = true;
							mc.getNetHandler().addToSendQueueNoEvent(new C03PacketPlayer.C05PacketPlayerLook(mc.thePlayer.serverSideYaw, mc.thePlayer.serverSidePitch, true));
							mc.timer.timerSpeed =  .9f;
							mc.thePlayer.fallDistance *= .1; 
						}
		        	}
				break;
    		}
        }
    }

    @Override
    public void onEnable() {  
    	fallen = false;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}