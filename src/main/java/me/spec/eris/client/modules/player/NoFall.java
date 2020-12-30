package me.spec.eris.client.modules.player;

import me.spec.eris.Eris;
import me.spec.eris.api.event.Event;
import me.spec.eris.client.events.player.EventMove;
import me.spec.eris.client.events.player.EventUpdate;
import me.spec.eris.api.module.ModuleCategory;
import me.spec.eris.api.module.Module;
import me.spec.eris.client.modules.combat.Criticals;
import me.spec.eris.client.modules.combat.Killaura;
import me.spec.eris.client.modules.movement.Longjump;
import me.spec.eris.api.value.types.ModeValue;
import me.spec.eris.client.modules.movement.Speed;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;

public class NoFall extends Module {

    public NoFall() {
        super("NoFall", ModuleCategory.PLAYER);
    } 
    private ModeValue<Mode> mode = new ModeValue<Mode>("Mode", Mode.WATCHDOG, this);
    private enum Mode {WATCHDOG}
    
    private boolean fallen;
    
    @Override
    public void onEvent(Event e) { 
        if (e instanceof EventUpdate) {
			EventUpdate event = (EventUpdate)e;
            setMode(mode.getValue().toString());
    		switch (mode.getValue()) {
				case WATCHDOG:
					if (fallen && mc.thePlayer.isCollidedVertically && event.isPre()) {
						mc.thePlayer.motionY = .015;
						setLastDistance(0);
						mc.thePlayer.motionX = mc.thePlayer.motionZ = 0;
						Speed speed = ((Speed)Eris.getInstance().moduleManager.getModuleByClass(Speed.class));
						speed.waitTicks = 1;
						speed.hops = -1;
						mc.getNetHandler().addToSendQueueNoEvent(new C03PacketPlayer.C05PacketPlayerLook(mc.thePlayer.serverSideYaw, mc.thePlayer.serverSidePitch, true));
						fallen = false;//Don't remove this vaziak, it will be used later I swear on jahsey onfroy
					}
				break;
    		}
        } 
        if (e instanceof EventMove) {
    		switch (mode.getValue()) {
				case WATCHDOG: 
		        	if (AntiVoid.isBlockUnder() && !Eris.instance.moduleManager.isEnabled(Longjump.class)) {
						if (mc.thePlayer.fallDistance > 2.1) {
							Criticals crits = ((Criticals)Eris.getInstance().moduleManager.getModuleByClass(Criticals.class));
							crits.accumulatedFall = 0;
							fallen = true;
							mc.getNetHandler().addToSendQueueNoEvent(new C03PacketPlayer.C05PacketPlayerLook(mc.thePlayer.serverSideYaw, mc.thePlayer.serverSidePitch, true));
							Killaura aura = ((Killaura)Eris.getInstance().moduleManager.getModuleByClass(Killaura.class));
							aura.fuckCheckVLs = true;
							mc.thePlayer.fallDistance = 0;
						}
		        	}
				break;
    		}
        }
    }

    @Override
    public void onEnable() {  
    	fallen = false;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}