package me.spec.eris.module.modules.movement;

import me.spec.eris.Eris;
import me.spec.eris.event.Event;
import me.spec.eris.event.player.EventMove;
import me.spec.eris.event.player.EventUpdate;
import me.spec.eris.module.Category;
import me.spec.eris.module.Module;
import me.spec.eris.module.antiflag.prioritization.ModulePrioritizer;
import me.spec.eris.module.antiflag.prioritization.enums.ModulePriority;
import me.spec.eris.module.antiflag.prioritization.enums.ModuleType;
import me.spec.eris.module.modules.combat.Criticals;
import me.spec.eris.module.values.valuetypes.ModeValue;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.potion.Potion;

public class Longjump extends Module {

    public Longjump() {
        super("LongJump", Category.MOVEMENT);
        setModuleType(ModuleType.FLAGGABLE);
        setModulePriority(ModulePriority.HIGH);
    }
    
    private ModeValue<Mode> mode = new ModeValue<Mode>("Mode", Mode.WATCHDOG, this);
    public enum Mode {WATCHDOG}
    
    private int stage, collisionTime;
    private double speed;

    @Override
    public void onEvent(Event e) {
     //   if (ModulePrioritizer.isModuleUsable(this)) return;

        if (e instanceof EventUpdate) {
            setMode(mode.getValue().toString());
            EventUpdate eu = (EventUpdate) e;
            double xDist = mc.thePlayer.posX - mc.thePlayer.prevPosX;
            double zDist = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
            setLastDistance(Math.sqrt(xDist * xDist + zDist * zDist));
    		switch (mode.getValue()) {
				case WATCHDOG:
					if (!eu.isPre()) return; 
		            if (mc.thePlayer.onGround && mc.thePlayer.isCollidedVertically) {
		                eu.setY(eu.getY() + 1.225e-8);
		            }
	                if (mc.thePlayer.isMoving() && mc.thePlayer.onGround && mc.thePlayer.isCollidedVertically && (stage > 2 || stage < 0)) {
	                	collisionTime++;
	                }
		            if (collisionTime > 1) {
		            	Eris.instance.modules.getModuleByClass(Longjump.class).toggle(false);
		            }
		            
				break;
    		}
        }

        if (e instanceof EventMove) {
            EventMove em = (EventMove) e;
    		switch (mode.getValue()) {
				case WATCHDOG:
		            if (mc.thePlayer.isMoving() && stage >= 0) {
		                switch (stage) { 
		                    case 2:
		                        if (mc.thePlayer.onGround && mc.thePlayer.isCollidedVertically) {
		                            em.setY(mc.thePlayer.motionY = em.getMotionY(0.42F));
		                            speed = em.getMovementSpeed() * 2F;
		                        }
		                        break;
		                    case 3:
		                        speed = em.getMovementSpeed() * 2.2;
		                        break;
		                    case 4:
		                    	speed *= mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 1.1 : 1.4;
		                        break;
		                    default:
		                        if (mc.thePlayer.motionY < 0.0D) em.setY(mc.thePlayer.motionY *= .62);  
		                        if (mc.thePlayer.fallDistance > 2.8 || stage > 14) {
		    		            	Eris.instance.modules.getModuleByClass(Longjump.class).toggle(false);
		                        }
		                        speed = getLastDistance() - getLastDistance() / 159;
		                        break;
		                }
		                stage++;
		            }
		            if (stage >= 0) {
			            em.setMoveSpeed(Math.max(speed, em.getMovementSpeed()));
		            }
				break;
    		}

        }
    }

    @Override
    public void onEnable() {			
    	Criticals crits = ((Criticals)Eris.getInstance().modules.getModuleByClass(Criticals.class));
    	crits.accumulatedFall = 0; 		
    	mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem += 1));
    	mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C0APacketAnimation()); 
    	mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem -= 1));
	    
    	if (crits.airTime > 0) {	
    		sendPosition(0,0,0,true,false);
    		crits.airTime = 0;
    		crits.waitTicks = 3;
    	}
    	speed = 0;
    	stage = 0;
    	collisionTime = 1;
        setLastDistance(0.0);
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
