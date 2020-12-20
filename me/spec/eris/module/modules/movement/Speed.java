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
import me.spec.eris.module.modules.combat.Killaura;
import me.spec.eris.module.values.valuetypes.BooleanValue;
import me.spec.eris.module.values.valuetypes.ModeValue;
import me.spec.eris.module.values.valuetypes.NumberValue;
import me.spec.eris.utils.PlayerUtils;

public class Speed extends Module {

	private ModeValue<Mode> mode = new ModeValue<Mode>("Mode", Mode.WATCHDOG, this);
	public enum Mode { WATCHDOG }
	public double speed;
	public boolean reset;
	public int stage;
	
	public Speed() {
		super("Speed", Category.MOVEMENT);
		setModuleType(ModuleType.FLAGGABLE);
		setModulePriority(ModulePriority.MODERATE);
	}
	
	@Override
	public void onEvent(Event e) { 

		if (e instanceof EventUpdate) {
			setMode(mode.getValue().toString());
			EventUpdate eu = (EventUpdate)e;
			double xDist = mc.thePlayer.posX - mc.thePlayer.prevPosX;
			double zDist = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
			setLastDistance(Math.sqrt(xDist * xDist + zDist * zDist));
			if (mc.thePlayer.onGround && mc.thePlayer.isCollidedVertically) {
				eu.setY(eu.getY() + 9.0E-4d);
			}
		}
		
		if (e instanceof EventMove && mc.thePlayer.isMoving()) {
			EventMove em = (EventMove)e;
			if (Eris.getInstance().getGameMode().equals(Eris.Gamemode.DUELS)) { 
				if (!mc.thePlayer.onGround) {
					Killaura theAura = ((Killaura)Eris.getInstance().modules.getModuleByClass(Killaura.class));
					if (Eris.getInstance().modules.getModuleByClass(Killaura.class).isToggled() && theAura.target != null) {
						mc.timer.timerSpeed = 1.15f;
					} else {
						mc.timer.timerSpeed = 1.00f;
					}
				}
			}

			if (mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0.0, mc.thePlayer.motionY, 0.0)).size() > 0) {
				stage = 0;
			}
			if (stage == 0) {
				if (mc.thePlayer.onGround) { 
					if (mc.timer.timerSpeed < 1.0f) mc.timer.timerSpeed = 1.0f;
                	em.setY(mc.thePlayer.motionY = (float)em.getMotionY(.42f - 9.0e-4D * 2));
	            	speed = em.getMovementSpeed() * 2.22;
				}
				stage = 0;
			} else if (stage == 1) {  
            	speed = getLastDistance() - .65 * (getLastDistance() - em.getMovementSpeed());
			} else { 
				if (mc.thePlayer.motionY < 0) {
					em.setY(mc.thePlayer.motionY *= 1.025);
				}
				speed = getLastDistance() - getLastDistance() / 160 - 8.99999999999999e-4D;
			}
			em.setMoveSpeed(speed);
			stage++;
		}
	}

	@Override
	public void onEnable() {
		setLastDistance(0.0);
		stage = 0;
		super.onEnable();
	}

	@Override
	public void onDisable() {
		super.onDisable();
	}
}
