package me.spec.eris.module.modules.player;

import me.spec.eris.Eris;
import me.spec.eris.event.Event;
import me.spec.eris.event.player.EventMove;
import me.spec.eris.event.player.EventUpdate;
import me.spec.eris.module.Category;
import me.spec.eris.module.Module;
import me.spec.eris.module.modules.combat.Criticals;
import me.spec.eris.module.modules.combat.Killaura;
import me.spec.eris.module.modules.movement.Longjump;
import me.spec.eris.module.values.valuetypes.ModeValue;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;

public class NoFall extends Module {

    public NoFall() {
        super("NoFall", Category.PLAYER); 
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
		    			 	mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem += 1));
		    	        	mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C0APacketAnimation()); 
		    	        	mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem -= 1));
		    				fallen = false;
		    			}

		        	}
				break;
    		}
        } 
        if (e instanceof EventMove) {
    		switch (mode.getValue()) {
				case WATCHDOG: 
		        	if (AntiVoid.isBlockUnder() && !Eris.instance.modules.isEnabled(Longjump.class)) {
						if (mc.thePlayer.fallDistance > 2.4) { 
							Criticals crits = ((Criticals)Eris.getInstance().modules.getModuleByClass(Criticals.class));
							crits.accumulatedFall = 0; 
							Killaura aura = ((Killaura)Eris.getInstance().modules.getModuleByClass(Killaura.class)); 
							fallen = true;  
							aura.clientRaper.reset();
							if (mc.thePlayer.ticksExisted % 3 == 0 && aura.target == null) {
								sendPosition(0,0,0,true,true);
							} else {
								mc.getNetHandler().addToSendQueueNoEvent(new C03PacketPlayer.C05PacketPlayerLook(mc.thePlayer.serverSideYaw, mc.thePlayer.serverSidePitch, true));
							} 
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