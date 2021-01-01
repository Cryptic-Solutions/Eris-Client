package me.spec.eris.client.ui.hud;

import me.spec.eris.api.module.Module;
import me.spec.eris.client.ui.hud.api.Panel;
import me.spec.eris.client.ui.hud.impl.panels.Coords;
import me.spec.eris.client.ui.hud.impl.panels.ModuleList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class HUD extends GuiScreen {
    private ArrayList<Panel> panels= new ArrayList<>();
    public boolean opened,createdPanels;
    ScaledResolution scalRes;
    public HUD(boolean opened) {
        this.opened = opened;
        scalRes = new ScaledResolution(Minecraft.getMinecraft());
        panels.add(new Coords(3,scalRes.getScaledHeight() - 15, 20 , 20));
        panels.add(new ModuleList(scalRes.getScaledWidth(),0, 40 , 20));
    }
    @Override
    public void onGuiClosed() {
        panels.clear();
        super.onGuiClosed();
    }

    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        Gui.drawRect(0,(scalRes.getScaledHeight() / 2) - 1,scalRes.getScaledWidth(), (scalRes.getScaledHeight() / 2) + 1, new Color(192,192,192, 190).getRGB());
        Gui.drawRect((scalRes.getScaledWidth() / 2) - 1,0,(scalRes.getScaledWidth() / 2) + 1, scalRes.getScaledHeight(), new Color(192,192,192, 190).getRGB());

        Gui.drawRect(0,0,scalRes.getScaledWidth(), scalRes.getScaledHeight(), new Color(0,0,0,135).getRGB());
        for (Panel pane : panels) {
            pane.drawScreen(mouseX,mouseY);
        }

    }

    public void drawElements() {
        if (opened) return;
        for (Panel pane : panels) {
            pane.drawScreen(0,0);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        for (Panel pane : panels) {
            pane.mouseClicked(mouseX,mouseY, mouseButton);
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        for (Panel pane : panels) {
            pane.mouseReleased(mouseX,mouseY, state);
        }
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException {

        super.keyTyped(typedChar, keyCode);
    }

    public boolean doesGuiPauseGame() {
        return false;
    }
}
