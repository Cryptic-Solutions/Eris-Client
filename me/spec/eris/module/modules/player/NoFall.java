package me.spec.eris.module.modules.player;

import me.spec.eris.Eris;
import me.spec.eris.event.Event;
import me.spec.eris.event.client.EventPacket;
import me.spec.eris.event.player.EventMove;
import me.spec.eris.event.player.EventUpdate;
import me.spec.eris.module.Category;
import me.spec.eris.module.Module;
import me.spec.eris.module.modules.combat.Criticals;
import me.spec.eris.utils.math.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

public class NoFall extends Module {

    public NoFall() {
        super("NoFall", Category.MOVEMENT); 
    } 
    
    private double estFallDist;
    private boolean fallen;
    private int division;
    
    @Override
    public void onEvent(Event e) { 
        if (e instanceof EventUpdate) {
            EventUpdate eu = (EventUpdate) e;

        	if(mc.thePlayer.fallDistance >= 2.75) {
    			if (fallen && mc.thePlayer.isCollidedVertically) {
    				mc.timer.timerSpeed = 1.0f;
    				fallen = false;
    			}

        	}
        } 
        if (e instanceof EventMove) {
        	EventMove event = (EventMove)e;
        	if  (AntiVoid.isBlockUnder()) {
				if (mc.thePlayer.fallDistance > 2.2) {
					for (int i= 0; i < (int)mc.thePlayer.fallDistance / 2; i++) {
						Criticals crits = ((Criticals)Eris.getInstance().modules.getModuleByClass(Criticals.class));
						crits.accumulatedFall = 0; 
						fallen = true; 
						sendPosition(0,0,0,true,true);
					} 
					mc.thePlayer.fallDistance *= .1;
				}
        	}
        }
 
    }

    @Override
    public void onEnable() {  
    	division = 1;
    	estFallDist = 0;
    	fallen = false;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}