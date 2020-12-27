package me.spec.eris.module.modules.render;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import me.spec.eris.event.Event;
import me.spec.eris.event.render.EventEntityRender;
import me.spec.eris.module.Category;
import me.spec.eris.module.Module;
import me.spec.eris.module.modules.combat.AntiBot;
import me.spec.eris.module.modules.combat.Killaura;
import me.spec.eris.module.values.valuetypes.BooleanValue;
import me.spec.eris.module.values.valuetypes.NumberValue;
import me.spec.eris.utils.RenderUtilities;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class ESP extends Module {
    public ESP() {
        super("ESP", Category.RENDER);
    }

    public BooleanValue<Boolean> players = new BooleanValue<>("Players", false, this, "ESP Targets Players");
    public BooleanValue<Boolean> chests = new BooleanValue<>("Chests", false, this, "ESP Targets Chests - INDEV");
    public NumberValue<Integer> distance = new NumberValue<Integer>("Distance", 15, 1, 200, this, "Distance at which esp is drawn on the victim");

    @Override
    public void onEvent(Event espEvent) {
        if (espEvent instanceof EventEntityRender) {
            EventEntityRender event = (EventEntityRender) espEvent;
            if (players.getValue()) {
                for (Entity e : mc.theWorld.loadedEntityList) {
                    if (e instanceof EntityPlayer && (e.getEntityId() != mc.thePlayer.getEntityId() || mc.gameSettings.thirdPersonView != 0)) {
                        EntityPlayer player = (EntityPlayer) e;
                        if (!AntiBot.bots.contains(player)) {
	                        double posX = (player.lastTickPosX + (player.posX - player.lastTickPosX) * event.getPartialTicks());
	                        double posY = (player.lastTickPosY + (player.posY - player.lastTickPosY) * event.getPartialTicks());
	                        double posZ = (player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * event.getPartialTicks());
	                        draw2D(player, posX - RenderManager.renderPosX, posY - RenderManager.renderPosY, posZ - RenderManager.renderPosZ, new Color(255, 255, 255, 255).getRGB());
	                        GL11.glColor4f(1f, 1f, 1f, 1f);
                        }
                    }
                }
            }
        }
    }

    public void draw2D(final Entity e, final double posX, final double posY, final double posZ, final int color) {
        EntityLivingBase entity = (EntityLivingBase) e;
        GlStateManager.pushMatrix();
        GlStateManager.translate(posX, posY, posZ);
        GL11.glNormal3f(0.0f, 0.0f, 0.0f);
        GlStateManager.rotate(-RenderManager.playerViewY, 0.0f, 1.0f, 0.0f);
        GlStateManager.scale(-0.1, -0.1, 0.1);
        GL11.glDisable(2896);
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        /*BOTTOM*/
        int renderColor = Killaura.target == e ? Color.red.getRGB() : Color.white.getRGB();
        RenderUtilities.drawHorizontalLine(-6.0, 5.8, 1.0, renderColor);

        /*TOP*/
        RenderUtilities.drawHorizontalLine(-6.0, 5.8, -20.0, renderColor);

        /*LEFT*/
        RenderUtilities.drawVerticalLine(-6.0, 1.0, -21.0, renderColor);

        /*RIGHT*/
        RenderUtilities.drawVerticalLine(6.75, 1.0, -21.0, renderColor);

        double health = entity.getHealth() / 20;
        if (health > 1)
            health = 1;
        else if (health < 0)
            health = 0;

        double height = (20.0) * health;

        int r = (int) (230 + (50 - 230) * health);
        int g = (int) (50 + (230 - 50) * health);
        int b = 50;

        Gui.drawRect(-6.45, -(height), -6.75, 1.0, new Color(r, g, b).getRGB());

        GL11.glDisable(3042);
        GL11.glEnable(2929);
        GL11.glEnable(2896);
        GlStateManager.popMatrix();
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
