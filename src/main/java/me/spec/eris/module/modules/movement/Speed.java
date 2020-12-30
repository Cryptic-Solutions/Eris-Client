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
import net.minecraft.stats.StatList;

public class Speed extends Module {
    private ModeValue<Mode> mode = new ModeValue<Mode>("Mode", Mode.WATCHDOG, this);

    private enum Mode {WATCHDOG}
	private int hops, stage;
    public int waitTicks;
	private double speed;

	public Speed() {
        super("Speed", Category.MOVEMENT);
        setModuleType(ModuleType.FLAGGABLE);
		setModulePriority(ModulePriority.MODERATE);
    }



	@Override
	public void onEnable() {
		Criticals criticals = ((Criticals)Eris.getInstance().modules.getModuleByClass(Criticals.class));
		criticals.accumulatedFall = 0;
		if (criticals.airTime > 0) {
			sendPosition(0,0,0,true,false);
			criticals.airTime = 0;
			criticals.waitTicks = 3;
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

    @Override
    public void onEvent(Event e) {
		boolean reset = mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0.0, mc.thePlayer.motionY, 0.0)).size() > 0 && mc.thePlayer.onGround;

		switch (mode.getValue()) {
			case WATCHDOG:
				if (Eris.instance.modules.isEnabled(Flight.class)) return;
				if (e instanceof EventUpdate) {
					setMode(mode.getValue().toString());
					EventUpdate eu = (EventUpdate) e;
					if (Eris.instance.modules.isEnabled(Flight.class)) return;
					double xDist = mc.thePlayer.posX - mc.thePlayer.prevPosX;
					double zDist = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
					setLastDistance(Math.sqrt(xDist * xDist + zDist * zDist));
					if (eu.isPre() && mc.thePlayer.onGround && mc.thePlayer.isCollidedVertically && !Eris.instance.modules.isEnabled(Scaffold.class)) {
						eu.setY(eu.getY() + 3.99999e-9);
					}
				}
				if (e instanceof EventStep) {
					EventStep event = (EventStep) e;
					if (!event.isPre()) {
						double height = mc.thePlayer.getEntityBoundingBox().minY - mc.thePlayer.posY;
						if (height <= .5 && height > 0) {
							hops = -1;
							setLastDistance(0.0);
						}
					}
				}
				if (e instanceof EventMove) {
					EventMove em = (EventMove) e;
					Step step = ((Step) Eris.instance.modules.getModuleByClass(Step.class));
					if (Eris.instance.modules.isEnabled(Scaffold.class) || Eris.instance.modules.isEnabled(Flight.class) || step.cancelMorePackets) {
						hops = -1;
						return;
					}
					if (Eris.getInstance().getGameMode().equals(Eris.Gamemode.DUELS) && !mc.thePlayer.onGround) {
						boolean timer = Eris.getInstance().modules.getModuleByClass(Killaura.class).isToggled() && Killaura.target != null;
						mc.timer.timerSpeed = timer ? 1.15f : 1.0f;
					}

					if (waitTicks > 0 && mc.thePlayer.onGround) waitTicks--;
					if (waitTicks > 0 || !mc.thePlayer.isMoving() || mc.thePlayer.fallDistance > 2.25) {
						setLastDistance(0.0);
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
								if (!Eris.instance.modules.isEnabled(Scaffold.class)) mc.timer.timerSpeed = 1.2f;
								mc.thePlayer.isAirBorne = true;
								mc.thePlayer.triggerAchievement(StatList.jumpStat);
								em.setY(mc.thePlayer.motionY = (float) em.getMotionY(.4 + 1.0e-4));
								speed = em.getMovementSpeed() * (Eris.instance.modules.isEnabled(Scaffold.class) || hops < 0 || waitTicks > 0 ? 1.4 : hops % 3 != 0 ? 2.16 : 2.1499);
								hops++;
							}
							setLastDistance(0.0);
						break;
						case 1:
							speed = getLastDistance() - (hops % 3 != 0 && hops > 0 && !Eris.instance.modules.isEnabled(Scaffold.class) ? .658 : .66) * (getLastDistance() - em.getMovementSpeed());
							break;
						default:
							if ((stage == 2 || stage == 4) && mc.timer.timerSpeed > 1.0f) mc.timer.timerSpeed -= .1f;
							if (!Eris.instance.modules.isEnabled(Scaffold.class)) {
								if ((stage == 2 || stage == 4) && mc.timer.timerSpeed > 1.0f)
									mc.timer.timerSpeed -= .1f;
							}
							speed = getLastDistance() - getLastDistance() / 159;
						break;
				}
				em.setMoveSpeed(waitTicks > 0 ? .2 : speed);
				stage++;
			}
		}
	}
}
