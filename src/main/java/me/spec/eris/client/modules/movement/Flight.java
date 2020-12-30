package me.spec.eris.client.modules.movement;

import java.util.LinkedList;
import java.util.List;

import me.spec.eris.Eris;
import me.spec.eris.api.event.Event;
import me.spec.eris.client.events.client.EventPacket;
import me.spec.eris.client.events.player.EventMove;
import me.spec.eris.client.events.player.EventUpdate;
import me.spec.eris.api.module.ModuleCategory;
import me.spec.eris.api.module.Module;
import me.spec.eris.api.module.antiflag.prioritization.enums.ModulePriority;
import me.spec.eris.api.module.antiflag.prioritization.enums.ModuleType;
import me.spec.eris.api.value.types.BooleanValue;
import me.spec.eris.api.value.types.ModeValue;
import me.spec.eris.api.value.types.NumberValue;
import me.spec.eris.client.modules.combat.Killaura;
import me.spec.eris.utils.world.TimerUtils;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.potion.Potion;

public class Flight extends Module {

	public Flight(String racism) {
		super("Flight", ModuleCategory.MOVEMENT, racism);;
        setModuleType(ModuleType.FLAGGABLE);
        setModulePriority(ModulePriority.HIGHEST);
    }
    
    private ModeValue<Mode> mode = new ModeValue<Mode>("Mode", Mode.VANILLA, this);
    public enum Mode { VANILLA, WATCHDOG }
    
    public BooleanValue<Boolean> blink = new BooleanValue<>("Blink", false, this, () -> mode.getValue().equals(Mode.WATCHDOG), "Blink while flying");
    public BooleanValue<Boolean> timerAbuse = new BooleanValue<>("Timer abuse", false, this, () -> mode.getValue().equals(Mode.WATCHDOG), "Abuse timer speed");

    private NumberValue<Float> timerSpeedAbuse = new NumberValue<Float>("Timer", 1.5F, 1F, 5F, this, () -> timerAbuse.getValue() && mode.getValue().equals(Mode.WATCHDOG), "Timer speed abused?");
    private NumberValue<Float> timerDelay = new NumberValue<Float>("Timer Delay", 1.5F, .1F, 5F, this, () -> timerAbuse.getValue() && mode.getValue().equals(Mode.WATCHDOG), "How long in seconds to abuse timer for?");
    private NumberValue<Float> flySpeed = new NumberValue<Float>("Speed", 1F, 0.3F, 3F, this, "Speed");
    
    private final TimerUtils timerAbuseStopwatch = new TimerUtils();
    private final TimerUtils damageStopwatch = new TimerUtils();
    private final List<Packet> packets = new LinkedList<>();

    private boolean onGroundCheck, damagePlayer;
    private double speed;
    private int counter;

	private boolean damaged;

    @Override
    public void onEvent(Event e) {
        setMode(mode.getValue().toString()); 
        if (e instanceof EventMove) {
            EventMove event = (EventMove) e;
            switch (mode.getValue()) {
                case VANILLA:
                    if (mc.gameSettings.keyBindJump.isKeyDown() && !mc.gameSettings.keyBindSneak.isKeyDown()) {
                    	event.setY(mc.thePlayer.motionY = flySpeed.getValue());
                    } else if (!mc.gameSettings.keyBindJump.isKeyDown() && mc.gameSettings.keyBindSneak.isKeyDown()) {
                    	event.setY(mc.thePlayer.motionY = -flySpeed.getValue());
                    } else {
                    	event.setY(mc.thePlayer.motionY = 0);
                    }
                    event.setMoveSpeed(flySpeed.getValue());
                    break;
			case WATCHDOG:
	        	if (onGroundCheck) {
	        		if (!damagePlayer) {
	        			if (damageStopwatch.hasReached(150)) {
		        			for (int i = 0; i < 9; i++) {
		        				mc.getNetHandler().addToSendQueueNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + event.getLegitMotion(), mc.thePlayer.posZ, false));
		        				mc.getNetHandler().addToSendQueueNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + (event.getLegitMotion() % .0000625), mc.thePlayer.posZ, false));
		        				mc.getNetHandler().addToSendQueueNoEvent(new C03PacketPlayer(false));
		        			}
		        			mc.getNetHandler().addToSendQueueNoEvent(new C03PacketPlayer(true));
		        			damagePlayer = true;
	        			} else {
	        				event.setX(0);
	        				event.setY(0);
	        				event.setZ(0);
	        			}
	        		} else {
		        		switch (counter) {
		        		case 0:
		        			break;
		        		case 1:
		        			mc.timer.timerSpeed = 1;
		        			if (mc.thePlayer.onGround && mc.thePlayer.isCollidedVertically) {
		        				event.setY(mc.thePlayer.motionY = event.getJumpBoostModifier((float) 0.42d));
								speed =  .6;
		        			}
		        			break;
		        		case 2:
		        			mc.timer.timerSpeed = 1;
		        			speed = 1.33;
		        			break;
		        		default:
		        			speed = getLastDistance() - getLastDistance() / 159;
		        			break;
		        		}
		        		if (damaged) { 
		        			speed = Math.max(speed, event.getMovementSpeed());
		        			counter++;
		        		}
		        		event.setMoveSpeed(speed);
	        		}
	        	}
				break;
			default:
				break;
            }
        }

        if (e instanceof EventUpdate) {
            EventUpdate event = (EventUpdate) e;
            setMode(mode.getValue().toString());
            switch (mode.getValue()) {
                case VANILLA:

                    break;
			case WATCHDOG:
				if (onGroundCheck) {  
					mc.thePlayer.onGround = true;
	        		if (timerAbuse.getValue() && counter >= 15 & damaged) {
	                    if (!timerAbuseStopwatch.hasReached(timerDelay.getValue() * 1000)) {
	                        mc.timer.timerSpeed = timerSpeedAbuse.getValue();
	                    } else {
	                        mc.timer.timerSpeed = 1F;
	                    }
	                }
	                if (counter < 15 && !damaged) {
	                    timerAbuseStopwatch.reset();
	                }
	                if (!damaged && mc.thePlayer.hurtTime > 0) {
	                    damaged = true; 
	                }
                    if (event.isPre()) {  
                        double xDif = mc.thePlayer.posX - mc.thePlayer.prevPosX;
                        double zDif = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
                        setLastDistance(Math.sqrt(xDif * xDif + zDif * zDif));
    	               
                    	if (counter > 9) {
                    		mc.thePlayer.motionY = 0;
                    		event.setY(mc.thePlayer.posY + (mc.thePlayer.ticksExisted % 2 == 0 ? .0003: -.0003));
                    	}
                    }
				} else if (mc.thePlayer.ticksExisted % 12 == 0){
					setLastDistance(0.0);
					mc.thePlayer.motionX = mc.thePlayer.motionZ = 0;
					onGroundCheck = mc.thePlayer.onGround && mc.thePlayer.isCollidedVertically;
				}
				break;
			default:
				break;
            }
        }
        
        if (e instanceof EventPacket) {
        	EventPacket event = (EventPacket) e;
            switch (mode.getValue()) {
            	case VANILLA:

            	break;
            	case WATCHDOG:
            		if (event.isSending()) {
            			if (event.getPacket() instanceof C0APacketAnimation || event.getPacket() instanceof C02PacketUseEntity || event.getPacket() instanceof C08PacketPlayerBlockPlacement || event.getPacket() instanceof C07PacketPlayerDigging) {
            				if (counter < 20) {
								Killaura aura = ((Killaura)Eris.getInstance().moduleManager.getModuleByClass(Killaura.class));
								aura.waitTicks = 4;
            					event.setCancelled();
							}
						}
            			if (event.getPacket() instanceof C03PacketPlayer) {
            				C03PacketPlayer packet = (C03PacketPlayer)event.getPacket();
            				
            				if (blink.getValue()){ //|| !timerAbuseStopwatch.hasReached(timerDelay.getValue() * 1000) && counter > 7) {
	            				if (packet.isMoving()) {
	            					packets.add(packet);
	            					event.setCancelled();
	            				}
            				} else if (counter == 0 && onGroundCheck) {
            					event.setCancelled();
            				}

							if (packets.size() >= 25 && blink.getValue()) {// || counter > 7 && packets.size() > ((int)((timerDelay.getValue() * 1000 / 20) / 6) - 5) && !timerAbuseStopwatch.hasReached(timerDelay.getValue() * 1000) && mc.timer.timerSpeed > 1F) {
								flush();
							}
            			}
            		}
            		break;
            	default:
            		break;
            }
        }
    }
    
    public void forceMove() {
		double speed = .15;
		mc.thePlayer.motionX = (-Math.sin(mc.thePlayer.getDirection())) * speed;
		mc.thePlayer.motionZ = Math.cos(mc.thePlayer.getDirection()) * speed;
    }
    
    public void flush() {
		packets.forEach(lePaqquete -> {
			mc.thePlayer.sendQueue.addToSendQueueNoEvent(lePaqquete); 
		});
		packets.clear();
    }

    @Override
    public void onEnable() {
    	if (Eris.instance.moduleManager.isEnabled(Speed.class)) {
        	Eris.instance.moduleManager.getModuleByClass(Speed.class).toggle(false);
    	}
    	damagePlayer = false;
    	damaged = false;
    	onGroundCheck = mc.thePlayer.onGround && mc.thePlayer.isCollidedVertically; 
    	timerAbuseStopwatch.reset();
    	damageStopwatch.reset();
        counter = 0;
        speed = 0;
        setLastDistance(0.0);
        super.onEnable();
    }

    @Override
    public void onDisable() {
		mc.thePlayer.stepHeight = 0.626f;
        switch (mode.getValue()) {
	    	case VANILLA:
	
	    	break;
	    	case WATCHDOG:
				Killaura aura = ((Killaura)Eris.getInstance().moduleManager.getModuleByClass(Killaura.class));
				aura.fuckCheckVLs = true;
	    		Speed sped = ((Speed)Eris.instance.moduleManager.getModuleByClass(Speed.class));
	    		sped.waitTicks = 5;
	    		mc.thePlayer.onGround = false;
	        	mc.timer.timerSpeed = 1.0f;
	        	mc.thePlayer.motionY = 0;
	        	mc.thePlayer.motionX = mc.thePlayer.motionZ = 0;
	    		
				if (blink.getValue()) {
					flush();
				}
	    		break;
	    	default:
	    		break;
	    }
        super.onDisable();
    }
}
