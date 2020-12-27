package me.spec.eris.module.modules.render;

import java.awt.Color;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.mojang.realmsclient.gui.ChatFormatting;

import me.spec.eris.Eris;
import me.spec.eris.event.Event;
import me.spec.eris.event.render.EventRender2D;
import me.spec.eris.module.Category;
import me.spec.eris.module.Module;
import me.spec.eris.ui.fonts.TTFFontRenderer;
import me.spec.eris.utils.RenderUtilities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class HUD extends Module {
    public HUD() {
        super("HUD", Category.RENDER);
    }

    private int y;
    private static TTFFontRenderer fontRender;

    public static TTFFontRenderer getFont() {
        if (fontRender == null) {
            fontRender = Eris.instance.fontManager.getFont("SFUI 18");
        }
        return fontRender;
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof EventRender2D) {

            ScaledResolution scaledResolution = new ScaledResolution(mc);
            Eris.getInstance();
			mc.fontRendererObj.drawStringWithShadow(Eris.getInstance().clientName.substring(0, 1) + EnumChatFormatting.WHITE + Eris.getInstance().clientName.replace(Eris.getInstance().clientName.substring(0, 1), ""), 2, 2, Eris.getClientColor().getRGB());
			//
            List<Module> mods = Eris.getInstance().modules.getModulesForRender();

            mods.sort((b, a) -> Double.compare(getFont().getStringWidth(a.getFullModuleDisplayName()), getFont().getStringWidth(b.getFullModuleDisplayName())));
            if (!mods.isEmpty()) {

                GlStateManager.pushMatrix();   
                GlStateManager.scale(1,1.05f,1);
                y = 0;
                mods.forEach(mod -> {
                    String name = mod.getFullModuleDisplayName();

                    RenderUtilities.drawRectangle(scaledResolution.getScaledWidth() - (double)getFont().getStringWidth(name), y, (double)getFont().getStringWidth(name) + .35, (double)getFont().getHeight(name), new Color(0,0,0,145).getRGB());
                    getFont().drawStringWithShadow(name, scaledResolution.getScaledWidth() - getFont().getStringWidth(name), y, new Color(255, 0, 0).getRGB());
                    y += getFont().getHeight(name);
                });
                GlStateManager.popMatrix();
            }
            renderPotions();
        }
    }


    public void renderPotions() {
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        GL11.glPushMatrix();
        int size = 16;
        float x = 37;
        float y = (scaledResolution.getScaledHeight() - (230) - size * 2) - 5;
        Collection<?> var4 = Module.mc.thePlayer.getActivePotionEffects();
        int i = 0;
        if (!var4.isEmpty()) {
            for (Iterator<?> var6 = Module.mc.thePlayer.getActivePotionEffects().iterator(); var6.hasNext(); ) {
                PotionEffect var7 = (PotionEffect) var6.next();
                Potion var8 = Potion.potionTypes[var7.getPotionID()];
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/container/inventory.png"));
                if (var8.hasStatusIcon()) {
                    int var9 = var8.getStatusIconIndex();
                    Gui theGui = new Gui();
                    theGui.drawTexturedModalRect((int) x, (int) y - (18 * i), var9 % 8 * 18, 198 + var9 / 8 * 18, 18, 18);
                    Eris.getInstance();
					getFont().drawStringWithShadow("" + (var7.getDuration() <= 300 ? ChatFormatting.RED : ChatFormatting.WHITE) + Potion.getDurationString(var7), (int) x - Eris.getFontRenderer().getStringWidth("" + Potion.getDurationString(var7)) - 5, (int) y - (18 * i) + 6, -1);
                    i++;
                }
            }
        }
        GL11.glPopMatrix();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }
}
