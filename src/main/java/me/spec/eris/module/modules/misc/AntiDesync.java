package me.spec.eris.module.modules.misc;

import me.spec.eris.event.Event;
import me.spec.eris.event.client.EventPacket;
import me.spec.eris.event.player.EventUpdate;
import me.spec.eris.module.Category;
import me.spec.eris.module.Module;
import net.minecraft.network.play.client.C09PacketHeldItemChange;

public class AntiDesync extends Module {
    private int lastSlot;

    public AntiDesync() {
        super("AntiDesync", Category.MISC);
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof EventUpdate) {
            EventUpdate eu = (EventUpdate) e;

            if (eu.isPre() && this.lastSlot != -1 && this.lastSlot != mc.thePlayer.inventory.currentItem)
                mc.thePlayer.sendQueue
                        .addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
        }

        if (e instanceof EventPacket) {
            EventPacket ep = (EventPacket) e;

            if (ep.getPacket() instanceof C09PacketHeldItemChange) {
                C09PacketHeldItemChange packetHeldItemChange = (C09PacketHeldItemChange) ep.getPacket();
                this.lastSlot = packetHeldItemChange.getSlotId();
            }
        }
    }
}
