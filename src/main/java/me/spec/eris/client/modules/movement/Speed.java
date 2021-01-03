package me.spec.eris.client.modules.movement;

import me.spec.eris.Eris;
import me.spec.eris.api.event.Event;
import me.spec.eris.api.module.ModuleCategory;
import me.spec.eris.client.events.player.EventMove;
import me.spec.eris.client.events.player.EventStep;
import me.spec.eris.client.events.player.EventUpdate;
import me.spec.eris.api.module.Module;
import me.spec.eris.client.antiflag.prioritization.enums.ModulePriority;
import me.spec.eris.client.antiflag.prioritization.enums.ModuleType;
import me.spec.eris.client.integration.server.interfaces.Gamemode;
import me.spec.eris.client.modules.combat.Criticals;
import me.spec.eris.client.modules.combat.Killaura;
import me.spec.eris.api.value.types.ModeValue;
import net.minecraft.stats.StatList;

public class Speed extends Module {
    private ModeValue<Mode> mode = new ModeValue<Mode>("Mode", Mode.WATCHDOG, this);

    private enum Mode {WATCHDOG}
	private int stage;
    public int waitTicks, hops;
	private double speed;

	public Speed(String racism) {
        super("Speed", ModuleCategory.MOVEMENT, racism);
        setModuleType(ModuleType.FLAGGABLE);
		setModulePriority(ModulePriority.MODERATE);
    }



	@Override
	public void onEnable() {
		Criticals criticals = ((Criticals)Eris.getInstance().moduleManager.getModuleByClass(Criticals.class));
		criticals.accumulatedFall = 0;
		if (criticals.airTime > 0) {
			sendPosition(0,0,0,true,false);
			criticals.airTime = 0;
			criticals.waitTicks = 3;
		}
		if (!Eris.INSTANCE.moduleManager.isEnabled(Flight.class)) {
			hops = 0;
			setLastDistance(0.0);
			stage = 0;
		}
		super.onEnable();
	}

	@Override
	public void onDisable() {
		mc.timer.timerSpeed = 1.0f;
		super.onDisable();
	}

    @Override
    public void onEvent(Event e) {

		switch (mode.getValue()) {
			case WATCHDOG:
				if (e instanceof EventUpdate) {
					setMode(mode.getValue().toString());
					if (Eris.INSTANCE.moduleManager.isEnabled(Flight.class) || Eris.INSTANCE.moduleManager.isEnabled(Longjump.class)) return;
					EventUpdate eu = (EventUpdate) e;
					double xDist = mc.thePlayer.posX - mc.thePlayer.prevPosX;
					double zDist = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
					setLastDistance(Math.sqrt(xDist * xDist + zDist * zDist));
					if (eu.isPre() && mc.thePlayer.onGround && mc.thePlayer.isCollidedVertically && !Eris.INSTANCE.moduleManager.isEnabled(Scaffold.class)) {
						eu.setY(eu.getY() + 9.0E-4D / 2);
					}
				}
				if (e instanceof EventStep) {
					EventStep event = (EventStep) e;
					if (Eris.INSTANCE.moduleManager.isEnabled(Flight.class) || Eris.INSTANCE.moduleManager.isEnabled(Longjump.class)) return;
					if (!event.isPre()) {
						double height = mc.thePlayer.getEntityBoundingBox().minY - mc.thePlayer.posY;
						if (height <= .6 && height >= -.5 && height != 0.0) {
							hops = -2;
							setLastDistance(0.0);
						}
					}
				}
				if (e instanceof EventMove) {
					Step step = ((Step) Eris.getInstance().moduleManager.getModuleByClass(Step.class));

					if (Eris.getInstance().moduleManager.isEnabled(Scaffold.class) || Eris.getInstance().moduleManager.isEnabled(Flight.class) || Eris.getInstance().moduleManager.isEnabled(Longjump.class) || step.cancelMorePackets) {
						mc.timer.timerSpeed = 1.0f;
						hops = -2;
						waitTicks = 4;
						if (!Eris.INSTANCE.moduleManager.isEnabled(Scaffold.class)) return;
					}
					boolean reset = mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0.0, mc.thePlayer.motionY, 0.0)).size() > 0 && mc.thePlayer.onGround;
					EventMove em = (EventMove) e;

					if (waitTicks > 0 && mc.thePlayer.onGround) waitTicks--;
					if (waitTicks > 0 || !mc.thePlayer.isMoving() || mc.thePlayer.fallDistance > 2.25) {
						stage = 0;
						return;
					}

					if (reset) stage = 0;
					switch (stage) {
						case 0:
							if (reset) {
								if (stage < 1 && stage > 0) {
									stage = -1;
								} else {
									stage = 0;
								}
							}
							setLastDistance(0.0);
							if (mc.thePlayer.onGround) {
								if (!Eris.INSTANCE.moduleManager.isEnabled(Scaffold.class)) mc.timer.timerSpeed = 1.4f;
								mc.thePlayer.isAirBorne = true;
								mc.thePlayer.triggerAchievement(StatList.jumpStat);
								em.setY(mc.thePlayer.motionY = (float) em.getMotionY(.42f - 9.0E-4D * 2));
								speed = em.getMovementSpeed() * (Eris.INSTANCE.moduleManager.isEnabled(Scaffold.class) || hops < 0 || waitTicks > 0 ? 1.8 : hops % 3 != 0 ? 2.24 : 2.1499);
								hops++;
							}
							setLastDistance(0.0);
						break;
						case 1:
							speed = getLastDistance() - .66 * (getLastDistance() - em.getMovementSpeed());
							break;
						default:
							if ((stage == 2 || stage == 6) && mc.timer.timerSpeed > 1.0f) {
								mc.timer.timerSpeed -= stage == 2 ? .2f : .2f;
							}
							speed = getLastDistance() - getLastDistance() / 160 - 1.0e-5;
						break;
				}
				em.setMoveSpeed(waitTicks > 0 ? .2 : speed);
				stage++;
			}
		}
	}
}
