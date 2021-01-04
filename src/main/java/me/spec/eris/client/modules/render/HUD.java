package me.spec.eris.client.modules.render;

import java.awt.Color;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import me.spec.eris.api.module.ModuleCategory;
import me.spec.eris.api.value.types.BooleanValue;
import me.spec.eris.api.value.types.ModeValue;
import me.spec.eris.api.value.types.NumberValue;
import me.spec.eris.client.events.player.EventUpdate;
import me.spec.eris.utils.math.MathUtils;
import me.spec.eris.utils.player.PlayerUtils;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

import com.mojang.realmsclient.gui.ChatFormatting;

import me.spec.eris.Eris;
import me.spec.eris.api.event.Event;
import me.spec.eris.client.events.render.EventRender2D;
import me.spec.eris.api.module.Module;
import me.spec.eris.client.ui.fonts.TTFFontRenderer;
import me.spec.eris.utils.visual.RenderUtilities;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

public class HUD extends Module {

    private BooleanValue<Boolean> customClientColor = new BooleanValue<>("Custom Client Color", true, this, true, "Change client color");
    private NumberValue<Integer> customClientColorRed = new NumberValue<>("Custom Color Red", 255, 0, 255, this, () -> customClientColor.getValue(), "RED For Client Color");
    private NumberValue<Integer> customClientColorGreen = new NumberValue<>("Custom Color Green", 0, 0, 255, this, () -> customClientColor.getValue(), "GREEN For Client Color");
    private NumberValue<Integer> customClientColorBlue = new NumberValue<>("Custom Color Blue", 0, 0, 255, this, () -> customClientColor.getValue(), "BLUE For Client Color");

    private BooleanValue<Boolean> label = new BooleanValue<>("Watermark", true, this, true, "Shows Watermark");
    public BooleanValue<Boolean> labelTime = new BooleanValue<>("Watermark Time", true, this, () -> label.getValue(), "Shows Time In Watermark");
    private BooleanValue<Boolean> coordinates = new BooleanValue<>("Coordinates", true, this, "Shows Coords");
    private BooleanValue<Boolean> blocksPerSecond = new BooleanValue<>("BPS", true, this, "Shows BPS");
    public NumberValue<Integer> bpsPlaces = new NumberValue<>("BPS Rounding", 3, 1, 10, this, () -> blocksPerSecond.getValue(), "Rounding For BPS");
    public ModeValue<BPSMode> bpsType = new ModeValue<>("BPS Formatting", BPSMode.BPS, this, true, () -> blocksPerSecond.getValue(), "BPS Formatting");
    private BooleanValue<Boolean> ping = new BooleanValue<>("Ping", true, this, "Shows Ping");
    private BooleanValue<Boolean> potions = new BooleanValue<>("Potions", true, this, "Shows Potion Effects");
    private BooleanValue<Boolean> buildInfo = new BooleanValue<>("Build Info", true, this, "Shows UID And Build");
    private BooleanValue<Boolean> killLeaderboard = new BooleanValue<>("Kill Leaderboard", true, this, "Shows Kill Leaderboard");
    public BooleanValue<Boolean> customFontChat = new BooleanValue<>("Chat Font", true, this, true, "Ingame Chat Custom Font");
    public NumberValue<Integer> customChatOpacity = new NumberValue<>("Chat Opacity", 145, 1, 200, this, () -> customFontChat.getValue(), "Chat Background Opacity");
    private BooleanValue<Boolean> arraylist = new BooleanValue<>("Arraylist", true, this, true, "Shows Arraylist");
    private BooleanValue<Boolean> arraylistBackground = new BooleanValue<>("Arraylist Background", true, this, () -> arraylist.getValue(), "Backdrop On Arraylist");
    private NumberValue<Integer> arraylistBackgroundOpacity = new NumberValue<>("Background Opacity", 145, 1, 200, this, () -> arraylistBackground.getValue() && arraylist.getValue(), "Background Opacity");
    private ModeValue<ColorMode> colorMode = new ModeValue<>("Arraylist Color", ColorMode.STATIC, this, true, () -> arraylist.getValue(), "Arraylist Color");

    private NumberValue<Double> rainSpeed = new NumberValue<>("Speed", 3d, 1d, 6d, this, () -> colorMode.getValue().equals(ColorMode.RAINBOW) && arraylist.getValue(), "Rainbow Speed");
    private NumberValue<Double> rainOffset = new NumberValue<>("Offset", 2d, 1d, 6d, this, () -> colorMode.getValue().equals(ColorMode.RAINBOW) && arraylist.getValue(), "Rainbow Offset");
    private NumberValue<Double> saturation = new NumberValue<>("Saturation", 1d, 0d, 1d, this, () -> colorMode.getValue().equals(ColorMode.RAINBOW) && arraylist.getValue(), "Rainbow Saturation");
    private NumberValue<Double> brightness = new NumberValue<>("Brightness", 1d, 0d, 1d, this, () -> colorMode.getValue().equals(ColorMode.RAINBOW) && arraylist.getValue(), "Rainbow Brightness");

    private NumberValue<Integer> red = new NumberValue<>("Red", 255, 0, 255, this, () -> colorMode.getValue().equals(ColorMode.STATIC) && arraylist.getValue(), "RED for Static ArrayList Color");
    private NumberValue<Integer> green = new NumberValue<>("Green", 0, 0, 255, this, () -> colorMode.getValue().equals(ColorMode.STATIC) && arraylist.getValue(), "GREEN for Static ArrayList Color");
    private NumberValue<Integer> blue = new NumberValue<>("Blue", 0, 0, 255, this, () -> colorMode.getValue().equals(ColorMode.STATIC) && arraylist.getValue(), "BLUE for Static ArrayList Color");

    public enum ColorMode {
        STATIC, RAINBOW
    }

    public enum BPSMode {
        BPS, BPERS;
    }

    private int bpsX = 0, bpsY = 440, playerListX = 30, playerListY = 30, pingX = 0, pingY = 425, coordX = 0, coordY= 425, labelX = 2, labelY = 2, buildInfoX = 0, buildInfoY = 400, size = 16, potionsX = 37, potionsY = (new ScaledResolution(Minecraft.getMinecraft()).getScaledHeight() - (230) - size * 2) - 5, moduleListX = new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth(), moduleListY = 0;
    private double lastPosX;
    private double lastPosZ;
    public ArrayList<Double> distances;
    private int y;
    private static TTFFontRenderer fontRender;

    public HUD(String racism) {
        super("HUD", ModuleCategory.RENDER, racism);
        this.lastPosX = Double.NaN;
        this.lastPosZ = Double.NaN;
        this.distances = new ArrayList<Double>();
    }

    public static TTFFontRenderer getFont() {
        if (fontRender == null) {
            fontRender = Eris.INSTANCE.fontManager.getFont("SFUI 18");
        }

        return fontRender;
    }

    int yText = 3;
    int yPos = 3;

    @Override
    public void onEvent(Event e) {
        if(customClientColor.getValue()) {
            Eris.getInstance().setClientColor(new Color(customClientColorRed.getValue(), customClientColorGreen.getValue(), customClientColorBlue.getValue()));
        }
        if (e instanceof EventUpdate) {
            if (!Double.isNaN(this.lastPosX) && !Double.isNaN(this.lastPosZ)) {
                final double differenceX = Math.abs(this.lastPosX - Minecraft.getMinecraft().thePlayer.posX);
                final double differenceZ = Math.abs(this.lastPosZ - Minecraft.getMinecraft().thePlayer.posZ);
                final double distance = Math.sqrt(differenceX * differenceX + differenceZ * differenceZ) * 2.0;
                this.distances.add(distance);
                if (this.distances.size() > 20) {
                    this.distances.remove(0);
                }
            }
            this.lastPosX = Minecraft.getMinecraft().thePlayer.posX;
            this.lastPosZ = Minecraft.getMinecraft().thePlayer.posZ;
        }
        if (e instanceof EventRender2D) {
            if(label.getValue()) {
                renderLabel(labelX, labelY);
            }
            if(arraylist.getValue()) {
                renderModuleList(moduleListX, moduleListY);
            }
            if(coordinates.getValue()) {
                renderCoords(coordX, coordY);
            }
            if(buildInfo.getValue()) {
                renderBuildInfo(buildInfoX, buildInfoY);
            }
            if(potions.getValue()) {
                renderPotions(potionsX, potionsY);
            }
            if (blocksPerSecond.getValue()) {
                renderBPS(bpsX, bpsY);
            }
            if(ping.getValue()) {
                renderPing(pingX, pingY);
            }
            if(killLeaderboard.getValue()) {
                renderPlayerlist(playerListX, playerListY);
            }
        }
    }

    public int[] renderModuleList(int xPos, int yPos) {
        moduleListX = xPos;
        moduleListY = yPos;
        yText = moduleListY;
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        List<Module> modulesForRender = Eris.getInstance().moduleManager.getModulesForRender();
        int width = 0;
        int height = 0;
        modulesForRender.sort((b, a) -> Double.compare(getFont().getStringWidth(a.getFullModuleDisplayName()), getFont().getStringWidth(b.getFullModuleDisplayName())));

        if (!modulesForRender.isEmpty()) {
            width = (int) getFont().getStringWidth(modulesForRender.get(0).getFullModuleDisplayName());
            GlStateManager.pushMatrix();
            GlStateManager.scale(1, 1f, 1);

            y = moduleListY;

            modulesForRender.forEach(mod -> {
                String name = mod.getFullModuleDisplayName();

                double x = moduleListX - getFont().getStringWidth(name);

                if(arraylistBackground.getValue()) {
                    RenderUtilities.drawRectangle(x - 2, y, (double) getFont().getStringWidth(name) + 2, getFont().getHeight(name) + 2, new Color(0, 0, 0, arraylistBackgroundOpacity.getValue().intValue()).getRGB());
                }
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
            height = y;
            GlStateManager.popMatrix();
        }
        return new int[]{width, height};
    }

    public void renderCoords(int x, int y) {
        coordX = x;
        coordY = y;
        String coords = "XYZ" + EnumChatFormatting.GRAY + ": " + Math.round(mc.thePlayer.posX) + EnumChatFormatting.WHITE + ", " + EnumChatFormatting.GRAY + Math.round(mc.thePlayer.posY) + EnumChatFormatting.WHITE + ", " + EnumChatFormatting.GRAY + Math.round(mc.thePlayer.posZ);
        getFont().drawStringWithShadow(coords, coordX, (mc.ingameGUI.getChatGUI().getChatOpen() && MathUtils.isInRange(coordY, 400, 445) ? coordY - 10 : coordY), Eris.getInstance().getClientColor());
    }

    public void renderBPS(int x, int y) {
        bpsX = x;
        bpsY = y;
        String bps = bpsType.getValue().equals(BPSMode.BPS) ? "BPS" + EnumChatFormatting.GRAY + ": " + EnumChatFormatting.RESET + MathUtils.round(PlayerUtils.getDistTraveled(distances), bpsPlaces.getValue() != null ? bpsPlaces.getValue() : 3) : "Blocks per/s" + EnumChatFormatting.GRAY + ": " + EnumChatFormatting.RESET + MathUtils.round(PlayerUtils.getDistTraveled(distances), bpsPlaces.getValue() != null ? bpsPlaces.getValue() : 3);
        getFont().drawStringWithShadow(bps, x, y, Eris.getInstance().getClientColor());
    }

    public void renderPing(int x, int y) {
        pingX = x;
        pingY = y;
        String ping = "Ping" + EnumChatFormatting.GRAY + ": " + EnumChatFormatting.RESET + PlayerUtils.getPlayerPing();
        getFont().drawStringWithShadow(ping, x, y, Eris.getInstance().getClientColor());
    }

    public void renderBuildInfo(int x, int y) {
        buildInfoX = x;
        buildInfoY = y;
        getFont().drawStringWithShadow("Build" + EnumChatFormatting.GRAY + ": " + EnumChatFormatting.RESET + "dev" + EnumChatFormatting.GRAY + "#0001" + EnumChatFormatting.GRAY + " | " + EnumChatFormatting.WHITE + " " + Eris.getInstance().getClientBuildExperimental(), buildInfoX, (mc.ingameGUI.getChatGUI().getChatOpen() && MathUtils.isInRange(coordY, 400, 445) ? buildInfoX - 10 : buildInfoY), Eris.getInstance().getClientColor());
    }

    public void renderLabel(int x, int y) {
        labelX = x;
        labelY = y;
        getFont().drawStringWithShadow((labelTime.getValue() ? Eris.getInstance().getClientName().substring(0, 1) + EnumChatFormatting.WHITE + Eris.getInstance().getClientName().replace(Eris.getInstance().getClientName().substring(0, 1), "") + EnumChatFormatting.GRAY + " " + getTime() : Eris.getInstance().getClientName().substring(0, 1) + EnumChatFormatting.WHITE + Eris.getInstance().getClientName().replace(Eris.getInstance().getClientName().substring(0, 1), "")), labelX, labelY, Eris.getInstance().getClientColor());
    }

    public int[] renderPlayerlist(int x, int y) {
        playerListX = x;
        playerListY = y;
        return new int[]{50, 50};
    }

    public int[] renderPotions(int x, int y) {
        potionsX = x;
        potionsY = y;
        GL11.glPushMatrix();
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
                    getFont().drawStringWithShadow("" + (var7.getDuration() <= 300 ? ChatFormatting.RED : ChatFormatting.WHITE) + Potion.getDurationString(var7), (int) x - Eris.getInstance().getFontRenderer().getStringWidth("" + Potion.getDurationString(var7)) - 5, (int) y - (18 * i) + 6, -1);
                    i++;
                    GL11.glPopMatrix();
                    return new int[]{(int) getFont().getStringWidth("" + Potion.getDurationString(var7)), (int) getFont().getHeight("" + Potion.getDurationString(var7))};

                }
            }
        }
        GL11.glPopMatrix();
        return new int[]{50,50};
    }

    public String getTime() {
        LocalTime localTime = LocalTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
        return localTime.format(dtf);
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