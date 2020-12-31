package me.spec.eris.client.modules.movement;

import me.spec.eris.Eris;
import me.spec.eris.api.event.Event;
import me.spec.eris.api.module.ModuleCategory;
import me.spec.eris.client.antiflag.prioritization.enums.ModulePriority;
import me.spec.eris.client.antiflag.prioritization.enums.ModuleType;
import me.spec.eris.client.events.player.EventMove;
import me.spec.eris.client.events.player.EventUpdate;
import me.spec.eris.api.module.Module;
import me.spec.eris.client.modules.combat.Criticals;
import me.spec.eris.api.value.types.ModeValue;
import me.spec.eris.client.modules.combat.Killaura;
import me.spec.eris.utils.world.TimerUtils;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.potion.Potion;

public class Longjump extends Module {

	public Longjump(String racism) {
		super("LongJump", ModuleCategory.MOVEMENT, racism);;
		setModuleType(ModuleType.FLAGGABLE);
		setModulePriority(ModulePriority.HIGH);
	}

	private ModeValue<Mode> mode = new ModeValue<Mode>("Mode", Mode.WATCHDOG, this);
	public enum Mode {WATCHDOG}
	private final TimerUtils groundTimer = new TimerUtils();
	private boolean onGroundCheck;
	private int stage, collisionTime;
	private double speed;

	@Override
	public void onEvent(Event e) {
		if (e instanceof EventUpdate) {
			setMode(mode.getValue().toString());
			EventUpdate eu = (EventUpdate) e;
			if (!onGroundCheck && groundTimer.hasReached(100) && eu.isPre()) {
				onGroundCheck = mc.thePlayer.onGround && mc.thePlayer.isCollidedVertically;
				groundTimer.reset();
			}

			if (!onGroundCheck || Eris.getInstance().getModuleManager().isEnabled(Flight.class)) return;
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
						Eris.getInstance().moduleManager.getModuleByClass(Longjump.class).toggle(false);
					}

					break;
			}
		}

		if (e instanceof EventMove) {
			EventMove em = (EventMove) e;
			if (!onGroundCheck || Eris.getInstance().getModuleManager().isEnabled(Flight.class)) return;
			switch (mode.getValue()) {
				case WATCHDOG:
					if (mc.thePlayer.isMoving() && stage >= 0) {
						switch (stage) {
							case 2:
								if (mc.thePlayer.onGround && mc.thePlayer.isCollidedVertically) {
									em.setY(mc.thePlayer.motionY = em.getMotionY(0.4F));
									speed = 2 * em.getMovementSpeed();
								}
								break;
							case 3:
								speed = 2.1499 * em.getMovementSpeed();
								break;
							case 4:
								speed *= mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 1.15 : 1.3;
								break;
							default:
								if (mc.thePlayer.fallDistance > 2.8 || stage > 14) {
									speed = Math.max(em.getMovementSpeed(), speed * .646);
								} else {
									if (mc.thePlayer.motionY < 0) em.setY(mc.thePlayer.motionY *= .646);
									speed = getLastDistance() - getLastDistance() / 159;
								}
								break;
						}
						stage++;
					}
					if (stage >= 2) {
						em.setMoveSpeed(Math.max(speed, em.getMovementSpeed()));
					} else {
						setLastDistance(0);
						em.setX(0);
						em.setZ(0);
					}
					break;
			}

		}
	}

	@Override
	public void onEnable() {
		onGroundCheck = mc.thePlayer.onGround && mc.thePlayer.isCollidedVertically;
		Criticals crits = ((Criticals)Eris.getInstance().moduleManager.getModuleByClass(Criticals.class));
		Killaura aura = ((Killaura)Eris.getInstance().moduleManager.getModuleByClass(Killaura.class));
		aura.fuckCheckVLs = true;
		crits.accumulatedFall = 0;
		if (crits.airTime > 0) {
			sendPosition(0,0,0,true,false);
			crits.airTime = 0;
			crits.waitTicks = 3;
		}
		speed = 0;
		stage = 0;
		collisionTime = 1;
		super.onEnable();
	}

	@Override
	public void onDisable() {
		super.onDisable();
	}
}