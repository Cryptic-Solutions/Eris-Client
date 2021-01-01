package me.spec.eris.client.ui.hud;

import me.spec.eris.Eris;
import me.spec.eris.client.ui.hud.panel.impl.Coords;
import me.spec.eris.client.ui.hud.panel.impl.Label;
import me.spec.eris.client.ui.hud.panel.impl.ModuleList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;
import java.io.IOException;

public class CustomHUD extends GuiScreen {

    public boolean opened,createdPanels;
    public ScaledResolution scaledResolution;

    public CustomHUD(boolean opened) {
        this.opened = opened;
        scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        Eris.getInstance().customHUDManager.addToManagerArraylist(new Label(2,2, 20 , 20));
        Eris.getInstance().customHUDManager.addToManagerArraylist(new ModuleList(scaledResolution.getScaledWidth(),0, 40 , 20));
        Eris.getInstance().customHUDManager.addToManagerArraylist(new Coords(3,scaledResolution.getScaledHeight() - 15, 20 , 20));
    }
    @Override
    public void onGuiClosed() {
        Eris.getInstance().customHUDManager.clearManagerArraylist();
        super.onGuiClosed();
    }

    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        Gui.drawRect(0,(scaledResolution.getScaledHeight() / 2) - 1, scaledResolution.getScaledWidth(), (scaledResolution.getScaledHeight() / 2) + 1, Eris.getClientColor().getRGB());
        Gui.drawRect((scaledResolution.getScaledWidth() / 2) - 1,0,(scaledResolution.getScaledWidth() / 2) + 1, scaledResolution.getScaledHeight(), Eris.getClientColor().getRGB());
        Gui.drawRect(0,0, scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight(), new Color(0,0,0,135).getRGB());
        Eris.getInstance().customHUDManager.drawScreenForPanels(mouseX, mouseY);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        Eris.getInstance().customHUDManager.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        Eris.getInstance().customHUDManager.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
    }

    public boolean doesGuiPauseGame() {
        return false;
    }
}
