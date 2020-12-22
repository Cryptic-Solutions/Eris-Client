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
import me.spec.eris.module.values.valuetypes.BooleanValue;
import me.spec.eris.module.values.valuetypes.ModeValue;
import me.spec.eris.module.values.valuetypes.NumberValue;
import me.spec.eris.utils.PlayerUtils;

public class Speed extends Module {

    private ModeValue<Mode> mode = new ModeValue<Mode>("Mode", Mode.WATCHDOG, this);

    public enum Mode {WATCHDOG}

    public double speed;
    public boolean reset;
    public int stage, waitTicks, hops;

    public Speed() {
        super("Speed", Category.MOVEMENT);
        setModuleType(ModuleType.FLAGGABLE);
        setModulePriority(ModulePriority.MODERATE);
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof EventStep) {
            if (!((EventStep) e).isPre()) {
                double height = mc.thePlayer.getEntityBoundingBox().minY - mc.thePlayer.posY;
                speed = height > .626 ? 0 : .25;
                mc.thePlayer.motionY *= .98f;
            }
        }
        if (e instanceof EventUpdate) {
            setMode(mode.getValue().toString());
            EventUpdate eu = (EventUpdate) e;
            double xDist = mc.thePlayer.posX - mc.thePlayer.prevPosX;
            double zDist = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
            setLastDistance(Math.sqrt(xDist * xDist + zDist * zDist));
            if (mc.thePlayer.onGround && mc.thePlayer.isCollidedVertically) {
                eu.setY(eu.getY() + 4.0e-8);
            }
        }

        
        if (e instanceof EventMove) {
            EventMove em = (EventMove) e;
            if (Eris.getInstance().getGameMode().equals(Eris.Gamemode.DUELS)) {
                if (!mc.thePlayer.onGround) {
                    Killaura theAura = ((Killaura) Eris.getInstance().modules.getModuleByClass(Killaura.class));
                    if (Eris.getInstance().modules.getModuleByClass(Killaura.class).isToggled() && theAura.target != null) {
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
	           			mc.timer.timerSpeed = 1.2f;
	           			reset = true;
	           			em.setY(mc.thePlayer.motionY = (float) em.getMotionY(.42f - 4.0e-9f * 1.25));
	           			speed = em.getMovementSpeed() * (waitTicks > 0 ? 1.9 : hops > 3 ? 2.1499 : 2.18);
	           			hops++;
	           		}
	           	} else if (stage == 1) {
	           		speed = getLastDistance() - .65999 * (getLastDistance() - em.getMovementSpeed());
	           	} else {
	           		if (stage == 2 || stage == 4) {
	           			mc.timer.timerSpeed -= .1f;
	           		}
	           	}   
            em.setMoveSpeed(stage > 1 ? getLastDistance() - getLastDistance() / 160 - 1.0e-9 : speed); 
            stage++;
        }
    }

    @Override
    public void onEnable() {
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
