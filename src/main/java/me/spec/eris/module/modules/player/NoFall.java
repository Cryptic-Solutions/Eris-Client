package me.spec.eris.module.modules.player;

import me.spec.eris.Eris;
import me.spec.eris.event.Event;
import me.spec.eris.event.player.EventMove;
import me.spec.eris.event.player.EventUpdate;
import me.spec.eris.module.Category;
import me.spec.eris.module.Module;
import me.spec.eris.module.modules.combat.Criticals;
import me.spec.eris.module.modules.combat.Killaura;

public class NoFall extends Module {

    public NoFall() {
        super("NoFall", Category.PLAYER); 
    } 
    
    private boolean fallen;
    @Override
    public void onEvent(Event e) { 
        if (e instanceof EventUpdate) {
            if(mc.thePlayer.fallDistance >= 2.75) {
    			if (fallen && mc.thePlayer.isCollidedVertically) {
    				mc.timer.timerSpeed = 1.0f;
    				fallen = false;
    			}

        	}
        } 
        if (e instanceof EventMove) {
        	if  (AntiVoid.isBlockUnder()) {
				if (mc.thePlayer.fallDistance > 2.4) {
					for (int i= 0; i < (int)mc.thePlayer.fallDistance / 2; i++) {
						Criticals crits = ((Criticals)Eris.getInstance().modules.getModuleByClass(Criticals.class));
						crits.accumulatedFall = 0; 
						Killaura aura = ((Killaura)Eris.getInstance().modules.getModuleByClass(Killaura.class));
						 
						fallen = true;  
						aura.clientRaper.reset();

						sendPosition(0,0,0,true,true);
					} 
					mc.thePlayer.fallDistance *= .1;
				}
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