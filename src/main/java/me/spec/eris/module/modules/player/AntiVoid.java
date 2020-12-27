package me.spec.eris.module.modules.player;

import me.spec.eris.event.Event;
import me.spec.eris.event.player.EventUpdate;
import me.spec.eris.module.Category;
import me.spec.eris.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

public class AntiVoid extends Module {

    public AntiVoid() {
        super("AntiVoid", Category.PLAYER); 
    }

	private boolean motion;
    private BlockPos lastGroundPosition;


    @Override
    public void onEnable() { 
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
    
    @Override
    public void onEvent(Event e) { 
        if (e instanceof EventUpdate) {
            if (mc.thePlayer.onGround) {
            	lastGroundPosition = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
            }
            if(!isBlockUnder()) {
            	if(mc.thePlayer.fallDistance > 2.99f) {
                	if (motion) {
                        mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(lastGroundPosition.getX(), lastGroundPosition.getY() + 0.5, lastGroundPosition.getZ(), false));
                		motion = false;
                	} else { 
                        mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(lastGroundPosition.getX(), lastGroundPosition.getY(), lastGroundPosition.getZ(), true));
                        mc.thePlayer.fallDistance = 0;
                	}
                } else {
                	motion = true;
                }
            } else {
            	motion = true;
            }
        }
    }
    
    public static boolean isBlockUnder() {
        if(Minecraft.getMinecraft().thePlayer.posY < 0) return false;
        for(int off = 0; off < (int)Minecraft.getMinecraft().thePlayer.posY+2; off += 2){
            AxisAlignedBB bb = Minecraft.getMinecraft().thePlayer.getEntityBoundingBox().offset(0, -off, 0);
            if(!Minecraft.getMinecraft().theWorld.getCollidingBoundingBoxes(Minecraft.getMinecraft().thePlayer, bb).isEmpty()){
                return true;
            }
        }
        return false;
    }
}
