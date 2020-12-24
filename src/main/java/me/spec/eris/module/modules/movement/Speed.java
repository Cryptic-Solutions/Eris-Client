package me.spec.eris.module.modules.movement;

import me.spec.eris.Eris;
import me.spec.eris.event.Event;
import me.spec.eris.event.player.EventMove;
import me.spec.eris.event.player.EventStep;
import me.spec.eris.event.player.EventUpdate;
import me.spec.eris.module.Category;
import me.spec.eris.module.Module;
import me.spec.eris.module.antiflag.prioritization.ModulePrioritizer;
import me.spec.eris.module.antiflag.prioritization.enums.ModulePriority;
import me.spec.eris.module.antiflag.prioritization.enums.ModuleType;
import me.spec.eris.module.modules.combat.Criticals;
import me.spec.eris.module.modules.combat.Killaura;
import me.spec.eris.module.values.valuetypes.ModeValue;

public class Speed extends Module {

    private ModeValue<Mode> mode = new ModeValue<Mode>("Mode", Mode.WATCHDOG, this);
    private enum Mode {WATCHDOG}

    private double speed;
    private boolean reset;
    private int stage, waitTicks, hops;

    public Speed() {
        super("Speed", Category.MOVEMENT);
        setModuleType(ModuleType.FLAGGABLE);
        setModulePriority(ModulePriority.MODERATE);
    }

    @Override
    public void onEvent(Event e) { 
    	if (Eris.instance.modules.isEnabled(Flight.class)) return;
        if (e instanceof EventUpdate) {
            setMode(mode.getValue().toString());
            EventUpdate eu = (EventUpdate) e;
            double xDist = mc.thePlayer.posX - mc.thePlayer.prevPosX;
            double zDist = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
            setLastDistance(Math.sqrt(xDist * xDist + zDist * zDist));
            
            switch (mode.getValue()) {
				case WATCHDOG:
		            if (eu.isPre() && mc.thePlayer.onGround && mc.thePlayer.isCollidedVertically && !Eris.instance.modules.isEnabled(Scaffold.class) ) {
		                eu.setY(eu.getY() + 4.0e-8);
		            }
				break; 
            }
        }

        
        if (e instanceof EventMove) {
            EventMove em = (EventMove) e;
            switch (mode.getValue()) {
				case WATCHDOG:
					 if (Eris.getInstance().getGameMode().equals(Eris.Gamemode.DUELS)) {
			                if (!mc.thePlayer.onGround) {
			                    if (Eris.getInstance().modules.getModuleByClass(Killaura.class).isToggled() && Killaura.target != null) {
			                        mc.timer.timerSpeed = 1.15f;
			                    } else {
			                        mc.timer.timerSpeed = 1.00f;
			                    }
			                }
			            }



			        	if (mc.thePlayer.fallDistance > 2.25) {
			        		waitTicks = 10; 
			       	 	}
			            if (mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0.0, mc.thePlayer.motionY, 0.0)).size() > 0) {
			            	if (reset) mc.timer.timerSpeed = 1.0f;
			                stage = 0;
			                setLastDistance(0.0);
			            }
			            
			            if (waitTicks > 0) waitTicks--; 
				           	if (stage == 0) {
				           		Step step = ((Step)Eris.getInstance().modules.getModuleByClass(Step.class));
				           		if (mc.thePlayer.onGround && mc.thePlayer.isCollidedVertically && step.height < .627 && mc.thePlayer.isMoving() && waitTicks <= 0) {
				           			mc.timer.timerSpeed = Eris.instance.modules.isEnabled(Scaffold.class) ? 1.0f : 1.1f;
				           			reset = !Eris.instance.modules.isEnabled(Scaffold.class);
				           			em.setY(mc.thePlayer.motionY = (float) em.getMotionY(.42f - 4.0e-9f * 1.25));
				           			speed = em.getMovementSpeed() * (Eris.instance.modules.isEnabled(Scaffold.class) ? 1.5 : waitTicks > 0 ? 1.9 : hops > 3 ? 2.14999 : 2.12);
				           			hops++;
				           		}
				           	} else if (stage == 1) {
				           		speed = getLastDistance() - (Eris.instance.modules.isEnabled(Scaffold.class) ? .68 : .66) * (getLastDistance() - em.getMovementSpeed());
				           	} else {
				           		if ((stage == 2 || stage == 4) && reset) {
				           			mc.timer.timerSpeed -= .05f;
				           		}
				           		speed = getLastDistance() - getLastDistance() / 160 - 4.0e-9;
				           	}   
			            em.setMoveSpeed(speed); 
			            stage++;
				break; 
            }
        }
    }

    @Override
    public void onEnable() {
		Criticals crits = ((Criticals)Eris.getInstance().modules.getModuleByClass(Criticals.class));
		crits.accumulatedFall = 0; 
		if (crits.airTime > 0) {	
			sendPosition(0,0,0,true,false);
			crits.airTime = 0;
			crits.waitTicks = 3;
		}
    	hops = 0;
        setLastDistance(0.0);
        stage = 0;
        super.onEnable();
    }

    @Override
    public void onDisable() {
      	 mc.timer.timerSpeed = 1.0f;
        super.onDisable();
    }
}
