package me.spec.eris.module.modules.movement;

import java.util.LinkedList;
import java.util.List;

import me.spec.eris.event.Event;
import me.spec.eris.event.client.EventPacket;
import me.spec.eris.event.player.EventMove;
import me.spec.eris.event.player.EventUpdate;
import me.spec.eris.module.Category;
import me.spec.eris.module.Module;
import me.spec.eris.module.antiflag.prioritization.ModulePrioritizer;
import me.spec.eris.module.antiflag.prioritization.enums.ModulePriority;
import me.spec.eris.module.antiflag.prioritization.enums.ModuleType;
import me.spec.eris.module.values.valuetypes.ModeValue;
import me.spec.eris.module.values.valuetypes.NumberValue;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;

public class Flight extends Module {

    private ModeValue<Mode> mode = new ModeValue<Mode>("Mode", Mode.VANILLA, this);
    private NumberValue<Float> speed = new NumberValue<Float>("Speed", 1F, 0.3F, 3F, this, "Speed");
    private boolean onGroundCheck;
    private final List<Packet> packets = new LinkedList<>();
    public Flight() {
        super("Flight", Category.MOVEMENT);
        setModuleType(ModuleType.FLAGGABLE);
        setModulePriority(ModulePriority.HIGHEST);
    }

    @Override
    public void onEvent(Event e) {
        setMode(mode.getValue().toString()); 
        if (e instanceof EventMove) {
            EventMove em = (EventMove) e;
            switch (mode.getValue()) {
                case VANILLA:
                    if (mc.gameSettings.keyBindJump.isKeyDown() && !mc.gameSettings.keyBindSneak.isKeyDown()) {
                        em.setY(mc.thePlayer.motionY = this.speed.getValue());
                    } else if (!mc.gameSettings.keyBindJump.isKeyDown() && mc.gameSettings.keyBindSneak.isKeyDown()) {
                        em.setY(mc.thePlayer.motionY = -this.speed.getValue());
                    } else {
                        em.setY(mc.thePlayer.motionY = 0);
                    }
                    em.setMoveSpeed(speed.getValue());
                    break;
			case WATCHDOG:
				break;
			default:
				break;
            }
        }

        if (e instanceof EventUpdate) {
            EventUpdate event = (EventUpdate) e;
            switch (mode.getValue()) {
                case VANILLA:

                    break;
			case WATCHDOG:
				if (onGroundCheck) {
					mc.thePlayer.onGround = true;
					mc.thePlayer.motionY = 0;
					mc.thePlayer.cameraYaw = .1f;
					event.setY(mc.thePlayer.posY + (mc.thePlayer.ticksExisted % 2 == 0 ? .0017 : 0));
					
				} else if (mc.thePlayer.ticksExisted % 5 == 0){
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
            				if (packet.isMoving()) {
            					packets.add(packet);
            					event.setCancelled();
            				}
            				if (packets.size() >= 30) {
            					packets.forEach(lePaqquete -> {
            						mc.thePlayer.sendQueue.addToSendQueueNoEvent(lePaqquete); 
            					});
            					packets.clear();
            				}
            			}
            		}
            		break;
            	default:
            		break;
            }
        }
    }

    @Override
    public void onEnable() {
    	onGroundCheck = mc.thePlayer.onGround && mc.thePlayer.isCollidedVertically;
        setLastDistance(0.0);
        super.onEnable();
    }

    @Override
    public void onDisable() {
        switch (mode.getValue()) {
	    	case VANILLA:
	
	    	break;
	    	case WATCHDOG:
	    		packets.forEach(lePaqquete -> {
	    			mc.thePlayer.sendQueue.addToSendQueueNoEvent(lePaqquete); 
	    		});
	    		packets.clear();
	    		break;
	    	default:
	    		break;
	    }
        super.onDisable();
    }

    public enum Mode {
        VANILLA, WATCHDOG
    }
}
