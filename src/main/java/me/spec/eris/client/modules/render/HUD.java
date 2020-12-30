package me.spec.eris.client.modules.render;

import java.awt.Color;
import java.util.Collection;
import java.util.List;

import me.spec.eris.api.module.ModuleCategory;
import me.spec.eris.api.value.types.ModeValue;
import me.spec.eris.api.value.types.NumberValue;
import org.lwjgl.opengl.GL11;

import com.mojang.realmsclient.gui.ChatFormatting;

import me.spec.eris.Eris;
import me.spec.eris.api.event.Event;
import me.spec.eris.client.events.render.EventRender2D;
import me.spec.eris.api.module.Module;
import me.spec.eris.ui.fonts.TTFFontRenderer;
import me.spec.eris.utils.visual.RenderUtilities;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

public class HUD extends Module {
    private NumberValue<Integer> xPosition = new NumberValue<>("X-Position", 3, 0, 10, this, null, "Where the arraylist will begin on the X-Axis");
    private NumberValue<Integer> yPosition = new NumberValue<>("Y-Position", 3, 0, 10, this, null, "Where the arraylist will begin on the Y-Axis");

    private ModeValue<ColorMode> colorMode = new ModeValue<>("Color", ColorMode.STATIC, this);

    private NumberValue<Double> rainSpeed = new NumberValue<>("Speed", 3d, 1d, 6d, this, () -> colorMode.getValue().equals(ColorMode.RAINBOW), "Rainbow Speed");
    private NumberValue<Double> rainOffset = new NumberValue<>("Offset", 2d, 1d, 6d, this, () -> colorMode.getValue().equals(ColorMode.RAINBOW), "Rainbow Offset");
    private NumberValue<Double> saturation = new NumberValue<>("Saturation", 1d, 0d, 1d, this, () -> colorMode.getValue().equals(ColorMode.RAINBOW), "Rainbow Saturation");
    private NumberValue<Double> brightness = new NumberValue<>("Brightness", 1d, 0d, 1d, this, () -> colorMode.getValue().equals(ColorMode.RAINBOW), "Rainbow Brightness");

    private NumberValue<Integer> red = new NumberValue<>("Red", 255, 0, 255, this, () -> colorMode.getValue().equals(ColorMode.STATIC), "RED for Static ArrayList Color");
    private NumberValue<Integer> green = new NumberValue<>("Green", 0, 0, 255, this, () -> colorMode.getValue().equals(ColorMode.STATIC), "GREEN for Static ArrayList Color");
    private NumberValue<Integer> blue = new NumberValue<>("Blue", 0, 0, 255, this, () -> colorMode.getValue().equals(ColorMode.STATIC), "BLUE for Static ArrayList Color");

    public enum ColorMode {
        STATIC, RAINBOW
    }

    public HUD(String racism) {
        super("HUD", ModuleCategory.RENDER, racism);
    }

    private int y;
    private static TTFFontRenderer fontRender;

    public static TTFFontRenderer getFont() {
        if (fontRender == null) {
            fontRender = Eris.instance.fontManager.getFont("SFUI 18");
        }

        return fontRender;
    }

    int yText = 3;

    @Override
    public void onEvent(Event e) {
        if (e instanceof EventRender2D) {
            yText = yPosition.getValue();
            ScaledResolution scaledResolution = new ScaledResolution(mc);

            mc.fontRendererObj.drawStringWithShadow(Eris.getInstance().clientName.substring(0, 1) + EnumChatFormatting.WHITE + Eris.getInstance().clientName.replace(Eris.getInstance().clientName.substring(0, 1), ""), 2, 2, Eris.getClientColor().getRGB());

            List<Module> modulesForRender = Eris.getInstance().moduleManager.getModulesForRender();

            modulesForRender.sort((b, a) -> Double.compare(getFont().getStringWidth(a.getFullModuleDisplayName()), getFont().getStringWidth(b.getFullModuleDisplayName())));

            if (!modulesForRender.isEmpty()) {
                GlStateManager.pushMatrix();
                GlStateManager.scale(1, 1f, 1);

                y = yPosition.getValue();

                modulesForRender.forEach(mod -> {
                    String name = mod.getFullModuleDisplayName();

                    double x = scaledResolution.getScaledWidth() - getFont().getStringWidth(name) - xPosition.getValue();

                    RenderUtilities.drawRectangle(x - 2, y, (double) getFont().getStringWidth(name) + 2, getFont().getHeight(name) + 2, new Color(0, 0, 0, 145).getRGB());

                    switch (colorMode.getValue()) {
                        case RAINBOW: {
                            getFont().drawStringWithShadow(name, (float) x, y, getRainbow(6000, -15 * yText));
                            break;
                        }

                        case STATIC: {
                            getFont().drawStringWithShadow(name, (float) x, y, new Color(red.getValue(), green.getValue(), blue.getValue()).getRGB());
                            break;
                        }
                    }

                    y += getFont().getHeight(name) + 2;
                    yText += 12;
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
            for (PotionEffect var7 : Module.mc.thePlayer.getActivePotionEffects()) {
                Potion var8 = Potion.potionTypes[var7.getPotionID()];
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/container/inventory.png"));
                if (var8.hasStatusIcon()) {
                    int var9 = var8.getStatusIconIndex();
                    Gui theGui = new Gui();
                    theGui.drawTexturedModalRect((int) x, (int) y - (18 * i), var9 % 8 * 18, 198 + var9 / 8 * 18, 18, 18);
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

    public Color fade(long offset, float fade) {
        float hue = (float) (System.nanoTime() + offset) / 1.0E10F % 1.0F;
        long color = Long.parseLong(Integer.toHexString(Color.HSBtoRGB(hue, 1.0f, 1.0F)),
                16);
        Color c = new Color((int) color);
        return new Color(c.getRed() / 255.0F * fade, c.getGreen() / 255.0F * fade, c.getBlue() / 255.0F * fade,
                c.getAlpha() / 155.0F);
    }

    public int getRainbow(int speed, int offset) {
        float hue = (float) ((System.currentTimeMillis() * rainSpeed.getValue() + offset / rainOffset.getValue()) % speed * 2);
        hue /= speed;
        return Color.getHSBColor(hue, saturation.getValue().floatValue(), brightness.getValue().floatValue()).getRGB();
    }


}
