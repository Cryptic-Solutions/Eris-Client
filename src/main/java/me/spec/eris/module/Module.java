package me.spec.eris.module;

import java.util.ArrayList;

import me.spec.eris.Eris;
import me.spec.eris.event.Event;
import me.spec.eris.event.player.EventMove;
import me.spec.eris.module.antiflag.prioritization.enums.ModulePriority;
import me.spec.eris.module.antiflag.prioritization.enums.ModuleType;
import me.spec.eris.module.values.Value;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.EnumChatFormatting;

public class Module {

    private String name;
    private String mode = "";
    private Category category;
    private boolean isToggled;
    private int key;
    private boolean hidden;
    private ModuleType moduleType = ModuleType.NONFLAGGABLE;
    private ModulePriority modulePriority = ModulePriority.LOWEST;
    private ArrayList<Value> settings = new ArrayList<Value>();
    public int[] forcedDelay = new int[4];

    protected static final Minecraft mc = Minecraft.getMinecraft();

    public Module(String name, Category category) {
        this.name = name;
        this.category = category;
    }

    public Module(String name, boolean enabled) {
        this.isToggled = enabled;
        this.name = name;
        this.category = null;
    }

    public void addSetting(Value<?> value) {
        this.settings.add(value);
    }

    public ArrayList<Value> getSettings() {
        return this.settings;
    }

    public boolean isToggled() {
        return this.isToggled;
    }

    public void setToggled(boolean toggled, boolean save) {
        if (this.isToggled != toggled) {
            this.toggle(save);
        }
    }

    public void onEvent(Event e) {
    }

    public void toggle(boolean save) {
        this.isToggled = !this.isToggled;
        if (this.isToggled) {
            this.onEnable();
        } else {
            this.onDisable();
        }
        if (save) {
            Eris.instance.configManager.saveDefaultFile();
        }
    }

    public ModuleType getModuleType() {
        return this.moduleType;
    }

    public void setModuleType(ModuleType type) {
        this.moduleType = type;
    }

    public ModulePriority getModulePriority() {
        return this.modulePriority;
    }

    public void setModulePriority(ModulePriority priority) {
        this.modulePriority = priority;
    }

    public int getKey() {
        return this.key;
    }

    public void setKey(int key, boolean save) {
        this.key = key;
        if (save) {
            Eris.instance.fileManager.getBindsFile().save();
        }
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    protected void onEnable() {
    }

    protected void onDisable() {
    }

    public String getName() {
        return this.name;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public Category getCategory() {
        return this.category;
    }

    public double getLastDistance() {
        return EventMove.lastDistance;
    }

    public void setLastDistance(double lastDist) {
        EventMove.lastDistance = lastDist;
    }

    public void sendPosition(double x, double y, double z, boolean ground, boolean movement) {
        if (movement) {
            mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX + x, mc.thePlayer.posY + y, mc.thePlayer.posZ + z, mc.thePlayer.serverSideYaw, mc.thePlayer.serverSidePitch, ground));
        } else {
            mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX + x, mc.thePlayer.posY + y, mc.thePlayer.posZ + z, ground));
        }
    }

    public String getFullModuleDisplayName() {
        return name + EnumChatFormatting.GRAY + (mode.length() > 1 ? " " + mode.substring(0, 1) + mode.replace(mode.substring(0, 1), "").toLowerCase() : "");
    }
}
