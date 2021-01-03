package me.spec.eris.client.modules.client;

import me.spec.eris.api.event.Event;
import me.spec.eris.client.events.client.EventPacket;
import me.spec.eris.api.module.ModuleCategory;
import me.spec.eris.api.module.Module;
import me.spec.eris.api.value.types.ModeValue;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.network.play.server.S39PacketPlayerAbilities;

public class Disabler extends Module {

    public Disabler(String racism) {
        super("Disabler", ModuleCategory.MISC, racism);
    }
    private ModeValue<Mode> mode = new ModeValue<Mode>("Mode", Mode.WATCHDOG, this);
    private enum Mode {WATCHDOG}
    
    @Override
    public void onEvent(Event e) {
        if (e instanceof EventPacket) {
        	EventPacket event = (EventPacket)e;
            setMode(mode.getValue().toString());
        	if (event.isReceiving()) {
        		switch (mode.getValue()) {
        			case WATCHDOG:
	        		if (event.getPacket() instanceof S39PacketPlayerAbilities) event.setCancelled();
	        		
	        		if (event.getPacket() instanceof S32PacketConfirmTransaction) {
	        			S32PacketConfirmTransaction packet = (S32PacketConfirmTransaction) event.getPacket();
	        			
	        			if (packet.getActionNumber() < 0) event.setCancelled();
	        		}
					break;
        		}
        	} 
        	if (event.isSending()) {
        		switch (mode.getValue()) {
    			case WATCHDOG:
            		//if (event.getPacket() instanceof C00PacketKeepAlive) event.setCancelled();
				break;
        		}
        	}
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() { 
        super.onDisable();
    }
}
