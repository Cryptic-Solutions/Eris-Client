package me.spec.eris.module.modules.movement;

import java.util.LinkedList;
import java.util.List;

import me.spec.eris.Eris;
import me.spec.eris.event.Event;
import me.spec.eris.event.client.EventPacket;
import me.spec.eris.event.player.EventMove;
import me.spec.eris.event.player.EventUpdate;
import me.spec.eris.module.Category;
import me.spec.eris.module.Module;
import me.spec.eris.module.antiflag.prioritization.enums.ModulePriority;
import me.spec.eris.module.antiflag.prioritization.enums.ModuleType;
import me.spec.eris.module.modules.player.AntiVoid;
import me.spec.eris.module.values.valuetypes.BooleanValue;
import me.spec.eris.module.values.valuetypes.ModeValue;
import me.spec.eris.module.values.valuetypes.NumberValue;
import me.spec.eris.utils.TimerUtils;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.potion.Potion;

public class Flight extends Module {

    public Flight() {
        super("Flight", Category.MOVEMENT);
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
    
    private final TimerUtils stopwatch = new TimerUtils();
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

	        		} else {
		        		switch (counter) {
		        		case 0:
		        			break;
		        		case 1:
		        			mc.timer.timerSpeed = 1;
		        			if (mc.thePlayer.onGround && mc.thePlayer.isCollidedVertically) {
		        				event.setY(mc.thePlayer.motionY = event.getJumpBoostModifier((float) 0.42D));
		        			}
		        			speed = 0.601;
		        			break;
		        		case 2:
		        			speed = 1.4;
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
	                    if (!stopwatch.hasReached(timerDelay.getValue().longValue())) {
	                        mc.timer.timerSpeed = timerSpeedAbuse.getValue().floatValue();
	                    } else {
	                        mc.timer.timerSpeed = 1F;
	                    }
	                }
	                if (counter < 15) {
	                    stopwatch.reset();
	                }
	                if (!damaged && mc.thePlayer.hurtTime > 0) {
	                    damaged = true;
	                }
                    if (event.isPre()) {  
                        double xDif = mc.thePlayer.posX - mc.thePlayer.prevPosX;
                        double zDif = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
                        setLastDistance(Math.sqrt(xDif * xDif + zDif * zDif));
    	               
                    	if (counter > 10) {
                    		mc.thePlayer.motionY = 0;
                    		event.setY(mc.thePlayer.posY + (mc.thePlayer.ticksExisted % 2 == 0 ? .0006 : 0));
                    	}
                    }
				} else if (mc.thePlayer.ticksExisted % 15 == 0){
					mc.thePlayer.motionX = mc.thePlayer.motionZ = 0;
					setLastDistance(0);
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
            			if (event.getPacket() instanceof C03PacketPlayer) {
            				C03PacketPlayer packet = (C03PacketPlayer)event.getPacket();
            				
            				if (blink.getValue()) {
	            				if (packet.isMoving()) {
	            					packets.add(packet);
	            					event.setCancelled();
	            				}
	            				if (packets.size() >= 30) {
	            					flush();
	            				}
            				} else if (counter == 0) {
            					event.setCancelled();
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
    	if (Eris.instance.modules.isEnabled(Speed.class)) {
        	Eris.instance.modules.getModuleByClass(Speed.class).toggle(false);
    	}
		for (int i = 0; i < 9; i++) {
			mc.getNetHandler().addToSendQueueNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + .42f, mc.thePlayer.posZ, false));
			mc.getNetHandler().addToSendQueueNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + (.42f % .0000625), mc.thePlayer.posZ, false));
			mc.getNetHandler().addToSendQueueNoEvent(new C03PacketPlayer(false));
		}
		mc.getNetHandler().addToSendQueueNoEvent(new C03PacketPlayer(true));
		damagePlayer = true; 
    	damaged = false;
    	onGroundCheck = mc.thePlayer.onGround && mc.thePlayer.isCollidedVertically; 
    	stopwatch.reset();
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
	    		Speed sped = ((Speed)Eris.instance.modules.getModuleByClass(Speed.class));
	    		sped.waitTicks = 5;
	    		mc.thePlayer.onGround = false;
	        	mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem += 1));
	        	mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C0APacketAnimation()); 
	        	mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem -= 1));
	        	mc.timer.timerSpeed = 1.0f;
	    		double value = mc.thePlayer.ticksExisted % 2 == 0 ? -.00002 : .0;
	    		mc.thePlayer.setPositionAndUpdate(mc.thePlayer.posX, mc.thePlayer.posY + value, mc.thePlayer.posZ);
    			mc.thePlayer.motionY = -.41995f;
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
