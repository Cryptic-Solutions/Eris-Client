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
    
    private NumberValue<Float> flySpeed = new NumberValue<Float>("Speed", 1F, 0.3F, 3F, this, "Speed");
    
    private final TimerUtils damageTimer = new TimerUtils();
    private final List<Packet> packets = new LinkedList<>();

    private boolean onGroundCheck, damageFly;
    private double speed;
    private int counter;

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
	        		mc.thePlayer.setSprinting(true);
	        		mc.thePlayer.stepHeight = 0;
	        		mc.thePlayer.cameraYaw = .1f;
	            	mc.thePlayer.onGround = true; 
	        		switch (counter) {
	        		case 0:
	        			if (damageTimer.hasReached(150)) {
	        				for (int i = 0; i < 9; i++) {
	        					mc.getNetHandler().addToSendQueueNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + event.getMotionY(event.getLegitMotion()) , mc.thePlayer.posZ, false));
	        					mc.getNetHandler().addToSendQueueNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + (event.getMotionY(event.getLegitMotion()) % .0000625), mc.thePlayer.posZ, false));
	        					mc.getNetHandler().addToSendQueueNoEvent(new C03PacketPlayer(false));
	        				}
	        				mc.getNetHandler().addToSendQueueNoEvent(new C03PacketPlayer(true));
                            speed = event.getMovementSpeed() * 1.45;
	        				damageTimer.reset();
	        				counter = 1;
	        			} else {
	        				speed = 0;
	        				event.setX(mc.thePlayer.motionX = 0);
	        				event.setY(mc.thePlayer.motionY = 0);
	        				event.setZ(mc.thePlayer.motionZ = 0);
	        			}
	        			break;
	        		case 1: 
                        speed = event.getMovementSpeed() * 2.2;
	        			event.setY(mc.thePlayer.motionY = event.getMotionY(event.getLegitMotion()));
	        			counter = 2;
	        			break;
	        		case 2:
	        			if (mc.thePlayer.isPotionActive(Potion.jump)) {
	            			event.setY(mc.thePlayer.motionY = -(event.getMotionY(event.getLegitMotion()) - .01));
	        			}
                    	speed *= mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 1.5 : 2.2;
	        			counter = 3;
	        			break;
	        		default: 
	        			speed -= speed / 159; 
	        			counter++;
	        			if (mc.thePlayer.isCollidedHorizontally || !mc.thePlayer.isMoving()) {
	        				mc.timer.timerSpeed = 1.0f;
	        				damageFly = false;
	        			}
	        			break;
	        		}
	        		event.setMoveSpeed(speed == 0 ? 0 : Math.max(speed, event.getMovementSpeed()));
	        	} else {
    				event.setX(mc.thePlayer.motionX = 0);
    				event.setZ(mc.thePlayer.motionZ = 0);
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
					mc.thePlayer.fallDistance = 0;
					mc.thePlayer.onGround = true;
					mc.thePlayer.motionY = 0;
					if (counter >= 6 && event.isPre()) {
						event.setY(mc.thePlayer.posY + (mc.thePlayer.ticksExisted % 2 == 0 ? .0003 : 0)); 
					} 
	    			if (!mc.thePlayer.isMoving()) {
	    				forceMove();
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
            				} else if (counter < 1 && onGroundCheck) {
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
    	onGroundCheck = mc.thePlayer.onGround && mc.thePlayer.isCollidedVertically;
    	damageFly = true;
    	damageTimer.reset();
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
