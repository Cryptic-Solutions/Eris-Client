package me.spec.eris.module.modules.combat;

import java.util.Arrays;
import java.util.List;

import me.spec.eris.Eris;
import me.spec.eris.event.Event;
import me.spec.eris.event.client.EventPacket;
import me.spec.eris.event.player.EventJump;
import me.spec.eris.event.player.EventStep;
import me.spec.eris.event.player.EventUpdate;
import me.spec.eris.module.Category;
import me.spec.eris.module.Module;
import me.spec.eris.module.antiflag.prioritization.enums.ModulePriority;
import me.spec.eris.module.antiflag.prioritization.enums.ModuleType;
import me.spec.eris.module.modules.movement.Flight;
import me.spec.eris.module.modules.movement.Scaffold;
import me.spec.eris.module.modules.movement.Speed;
import me.spec.eris.module.modules.movement.Step;
import me.spec.eris.module.values.valuetypes.ModeValue;
import me.spec.eris.utils.PlayerUtils;
import me.spec.eris.utils.ServerUtils;
import net.minecraft.network.play.client.C02PacketUseEntity;

public class Criticals extends Module {
    private double groundSpoofDist = 0.001;
    private boolean forceUpdate;
    public int airTime, waitTicks;
    public ModeValue<Mode> modeValue = new ModeValue<>("Mode", Mode.SPOOF, this);
    public double accumulatedFall, posY;

    private static final double[] OFFSETS1 = new double[]{0.05F, 0.0016F, 0.03F, 0.0016F};
    private static final double[] OFFSETS2 = new double[]{0.05D, 0.0D, 0.012511D, 0.0D};

    public Criticals() {
        super("Criticals", Category.COMBAT);
        setModuleType(ModuleType.FLAGGABLE);
        setModulePriority(ModulePriority.LOW);
    }

    public enum Mode {
        SPOOF, SJUMP, WATCHDOG
    }

    @Override
    public void onEnable() {
        super.onEnable();
        airTime = 0;
        waitTicks = 3;
        groundSpoofDist = 1.0E-13D;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        airTime = 0;
        groundSpoofDist = 0.001;
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof EventJump) {
            if (airTime != 0 && mc.thePlayer.isMoving()) {
                waitTicks = 4;
                PlayerUtils.sendPosition(0, 0, 0, true, false);
                e.setCancelled();
                mc.thePlayer.motionY = .42f;
                airTime = 0;
            }
        } else if (e instanceof EventUpdate) {
        	this.setMode(modeValue.getValue().toString());
            EventUpdate eu = (EventUpdate) e;
 
            Killaura aura = ((Killaura) Eris.instance.modules.getModuleByClass(Killaura.class));

            if ((!aura.isToggled() || Killaura.target == null) && Step.needStep && !Step.safe) {
                Step.safe = true;
            }
            if (modeValue.getValue() == Mode.SPOOF) {
                if (groundSpoofDist < 0.0001) {
                    groundSpoofDist = 0.001;
                }
                if (mc.thePlayer.isSwingInProgress && mc.thePlayer.isCollidedVertically) {
                    eu.setY(eu.getY() + groundSpoofDist);
                    eu.setOnGround(false);
                    groundSpoofDist -= 1.0E-11;
                }
            }

            if (modeValue.getValue() == Mode.SJUMP && eu.isPre()) {
                if (interferanceFree() && mc.thePlayer.hurtTime == 0) {
                    if (waitTicks > 0) waitTicks--;
                    if (waitTicks > 0) return;

                    if (airTime == 13) {
                        groundSpoofDist = 0.41999998688697815;
                    }
                    if (airTime == 12) {
                        groundSpoofDist = 0.7531999805212024;
                    } else if (airTime == 11) {
                        groundSpoofDist = 1.0013359791121417;
                    } else if (waitTicks == 10) {
                        groundSpoofDist = 1.1661092609382138;
                    } else if (airTime == 9) {
                        groundSpoofDist = 1.2491870787446828;
                    } else if (airTime == 8) {
                        groundSpoofDist = 1.2491870787446828;
                    } else if (airTime == 7) {
                        groundSpoofDist = 1.1707870772188045;
                    } else if (airTime == 6) {
                        groundSpoofDist = 1.015555072702199;
                    } else if (airTime == 5) {
                        groundSpoofDist = 0.7850277037892397;
                    } else if (airTime == 4) {
                        groundSpoofDist = 0.48071087633169896;
                    } else if (airTime == 3) {
                        groundSpoofDist = 0.1040803780930446;
                    } else if (airTime == 2) {
                        groundSpoofDist = 0;
                    }
                    eu.setY(mc.thePlayer.posY + (airTime == 0 ? 0 : groundSpoofDist));
                    eu.setOnGround(eu.getY() == mc.thePlayer.posY);
                    if (airTime > 0) airTime--;
                } else {
                    groundSpoofDist = 0;
                    airTime = 0;
                    waitTicks = 6;
                }
            }
        } else if (e instanceof EventStep) { 
                if (mc.thePlayer == null)
                    return;
                if (mc.thePlayer.getEntityBoundingBox().minY - mc.thePlayer.posY < .626 && mc.thePlayer.getEntityBoundingBox().minY - mc.thePlayer.posY > .4) {
                    waitTicks = 4; 
                    airTime = 0;
                } 
        } else if (e instanceof EventPacket) {
            if (mc.thePlayer == null || !interferanceFree()) return;
            EventPacket ep = (EventPacket) e;
            if (ep.getPacket() instanceof C02PacketUseEntity) {
                C02PacketUseEntity packet = (C02PacketUseEntity) ep.getPacket();
                if (packet.getAction() == C02PacketUseEntity.Action.ATTACK) {
                    if (modeValue.getValue() == Mode.SJUMP && airTime == 0 && mc.thePlayer.hurtTime == 0 && waitTicks == 0 && interferanceFree()) {
                        airTime = 13;
                    }
                }
            }
        }
    }

    public double[] getOffsets() {
        return (ServerUtils.onServer("hypixel")) ? OFFSETS1 : OFFSETS2;
    }

    public void doUpdate(EventUpdate eventPlayerUpdate) {
        Killaura aura = ((Killaura) Eris.instance.modules.getModuleByClass(Killaura.class));

        if (!(!aura.isToggled() || Killaura.target == null)) {
            if (interferanceFree()) {

                if (Step.needStep || !Eris.instance.modules.isEnabled(Step.class)) {
                    if (waitTicks == 0) {
                        if (!(modeValue.getValue() == Mode.WATCHDOG)) return;

                        aura.critStopwatch.reset();
                        eventPlayerUpdate.setOnGround(false);
                        forceUpdate = true;
                        if (airTime >= 3) {
                            posY = .0626 * 3;
                            airTime = 0;
                        } else {
                            posY = .0626 * 2;
                            if (airTime == 2) {
                                posY = .0001;
                            }
                        }
                        eventPlayerUpdate.setY(mc.thePlayer.posY + posY); 
                        accumulatedFall += eventPlayerUpdate.getY() - mc.thePlayer.posY;
                        airTime++;

                        if (accumulatedFall >= 3) {
                            if (mc.thePlayer.onGround) { 
                                sendPosition(0, 0, 0, true, false);
                                accumulatedFall = 0;
                            }
                        }
                    } else {
                        eventPlayerUpdate.setY(mc.thePlayer.posY);
                        waitTicks--;
                    }
                } else {
                	waitTicks = 2;
                    Step.safe = true; 
                }
            } else {
                Step.safe = true; 
                waitTicks = 3;
            }
        } else {
            Step.safe = true; 
            waitTicks = 0;
        }
    }


    public void forceUpdate() {
        if (!forceUpdate || airTime == 0) return;
        //You don't send c06s standing still, doing so flags any half decent anticheat - food for thought
        sendPosition(0, 0, 0, mc.thePlayer.onGround, false);

        accumulatedFall = 0;
        forceUpdate = false;
    }

    public boolean interferanceFree() {
        if (Eris.instance.modules.isEnabled(Speed.class)) return false;
        if (Eris.instance.modules.isEnabled(Flight.class)) return false;
        if (Eris.instance.modules.isEnabled(Scaffold.class)) return false;
        if (mc.gameSettings.keyBindJump.isKeyDown() || mc.thePlayer.isInWater() || mc.thePlayer.isInLava() || mc.thePlayer.isOnLadder())
            return false;
        return (mc.thePlayer.onGround && mc.thePlayer.isCollidedVertically && mc.thePlayer.fallDistance == 0.0 && mc.thePlayer.stepHeight < .7);
    }
}
