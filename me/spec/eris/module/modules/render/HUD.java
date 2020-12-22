package me.spec.eris.module.modules.render;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.mojang.realmsclient.gui.ChatFormatting;

import me.spec.eris.Eris;
import me.spec.eris.event.Event;
import me.spec.eris.event.player.EventUpdate;
import me.spec.eris.event.render.EventRender2D;
import me.spec.eris.module.Category;
import me.spec.eris.module.Module;
import me.spec.eris.module.modules.combat.Killaura.BlockMode;
import me.spec.eris.module.values.valuetypes.BooleanValue;
import me.spec.eris.module.values.valuetypes.ModeValue;
import me.spec.eris.module.values.valuetypes.NumberValue;
import me.spec.eris.ui.fonts.TTFFontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

public class HUD extends Module {
	public ModeValue<HudMode> logoMode = new ModeValue<>("Logo", HudMode.TEXT, this, null, "Changes the logo of the Client");
	
	public enum HudMode {TEXT, PIC}
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
            
            if(logoMode.getValue().equals(HudMode.TEXT)) {
            	
            mc.fontRendererObj.drawStringWithShadow(Eris.getInstance().clientName.substring(0, 1) + EnumChatFormatting.WHITE + Eris.getInstance().clientName.replace(Eris.getInstance().clientName.substring(0, 1), ""), 2, 2, Eris.getInstance().getClientColor().getRGB());
           
            }else {
            	mc.getTextureManager().bindTexture(new ResourceLocation("eris/pics/logo1.png"));
            	Gui.drawModalRectWithCustomSizedTexture(4, 4, 0, 0, 196 / 2, 132 / 2, 196 / 2, 132 / 2);
            }
            
            List<Module> mods = Eris.getInstance().modules.getModulesForRender();

            mods.sort((b, a) -> Double.compare(getFont().getStringWidth(a.getFullModuleDisplayName()), getFont().getStringWidth(b.getFullModuleDisplayName())));
            if (!mods.isEmpty()) {
                y = 0;
                mods.forEach(mod -> {
                    String name = mod.getFullModuleDisplayName();
                    getFont().drawStringWithShadow(name, scaledResolution.getScaledWidth() - getFont().getStringWidth(name), y, new Color(255, 0, 0).getRGB());
                    y += getFont().getHeight(name);
                });


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
        Collection var4 = this.mc.thePlayer.getActivePotionEffects();
        int i = 0;
        if (!var4.isEmpty()) {
            for (Iterator var6 = this.mc.thePlayer.getActivePotionEffects().iterator(); var6.hasNext(); ) {
                PotionEffect var7 = (PotionEffect) var6.next();
                Potion var8 = Potion.potionTypes[var7.getPotionID()];
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/container/inventory.png"));
                if (var8.hasStatusIcon()) {
                    int var9 = var8.getStatusIconIndex();
                    Gui theGui = new Gui();
                    theGui.drawTexturedModalRect((int) x, (int) y - (18 * i), var9 % 8 * 18, 198 + var9 / 8 * 18, 18, 18);
                    getFont().drawStringWithShadow("" + (var7.getDuration() <= 300 ? ChatFormatting.RED : ChatFormatting.WHITE) + Potion.getDurationString(var7), (int) x - Eris.getInstance().getFontRenderer().getStringWidth("" + Potion.getDurationString(var7)) - 5, (int) y - (18 * i) + 6, -1);
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
