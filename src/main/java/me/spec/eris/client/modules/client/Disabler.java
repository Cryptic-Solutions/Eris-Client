package me.spec.eris.client.modules.client;

import me.spec.eris.Eris;
import me.spec.eris.api.event.Event;
import me.spec.eris.client.events.client.EventPacket;
import me.spec.eris.api.module.ModuleCategory;
import me.spec.eris.api.module.Module;
import me.spec.eris.api.value.types.ModeValue;
import me.spec.eris.client.modules.movement.Flight;
import me.spec.eris.client.modules.movement.Longjump;
import me.spec.eris.client.modules.movement.Speed;
import me.spec.eris.utils.math.MathUtils;
import me.spec.eris.utils.player.PlayerUtils;
import net.minecraft.network.login.server.S02PacketLoginSuccess;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.network.play.server.S39PacketPlayerAbilities;

public class Disabler extends Module {

	public Disabler(String racism) {
        super("Disabler", ModuleCategory.MISC, racism);
    }
    private ModeValue<Mode> mode = new ModeValue<Mode>("Mode", Mode.WATCHDOG, this);
    private enum Mode {WATCHDOG}
    public boolean needs;

    @Override
    public void onEvent(Event e) {
        if (e instanceof EventPacket) {
        	EventPacket event = (EventPacket)e;
            setMode(mode.getValue().toString());
        	if (event.isReceiving()) {
        		switch (mode.getValue()) {
        			case WATCHDOG:
					if (event.getPacket() instanceof S02PacketLoginSuccess) {
						needs = true;
					}
					if (event.getPacket() instanceof S39PacketPlayerAbilities) {
						S39PacketPlayerAbilities packet = (S39PacketPlayerAbilities) event.getPacket();
						packet.invulnerable = !packet.invulnerable;
						packet.creativeMode = !packet.creativeMode;
						packet.allowFlying = !packet.allowFlying;
					}
					if (event.getPacket() instanceof S32PacketConfirmTransaction) {
						S32PacketConfirmTransaction packet = (S32PacketConfirmTransaction) event.getPacket();
						if (packet.getActionNumber() < 0) packet.actionNumber = (short) 25;
					}
					break;
        		}
        	}
        	if (event.isSending()) {
        		switch (mode.getValue()) {
    			case WATCHDOG:
    				if (event.getPacket() instanceof C03PacketPlayer && needs) {
						mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C00PacketKeepAlive(-1));
						mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C0FPacketConfirmTransaction(Integer.MAX_VALUE, Short.MIN_VALUE, true));
						needs = false;
					}
					if (event.getPacket() instanceof C00PacketKeepAlive) event.setCancelled();
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
