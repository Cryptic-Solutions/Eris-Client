package me.spec.eris.client.ui.hud.panel.impl;


import me.spec.eris.Eris;
import me.spec.eris.client.modules.render.HUD;
import me.spec.eris.client.ui.fonts.TTFFontRenderer;
import me.spec.eris.client.ui.hud.panel.Panel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;

import java.io.IOException;

public class Label extends Panel {

    public Label(int x, int y,int width, int height) {
        super(x, y, width, height);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        if (dragging) {
            ScaledResolution scalRes = new ScaledResolution(Minecraft.getMinecraft());
            int predictX = mouseX - (width / 2) + xOffset;
            int predictY = mouseY - (height / 2) + yOffset;
            if (predictX > 0 && predictX < scalRes.getScaledWidth() - width + 1) {
                x = predictX;
            }
            if (predictY > 0 && predictY < scalRes.getScaledHeight() - 5) {
                y = predictY;
            }
        }
        String name = Eris.getInstance().getClientName();

        width = (int) getFont().getStringWidth(name);
        height = (int) getFont().getHeight(name);
        HUD hud = ((HUD)Eris.getInstance().getModuleManager().getModuleByClass(HUD.class));
        hud.renderLabel(x,y);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (isHovered(mouseX, mouseY)) {
            if (mouseButton == 0 && !dragged) {
                dragging = true;
                dragged = true;
                int xPos = this.x + (width / 2);
                int yPos = this.y + (height / 2);
                this.xOffset = xPos - x;
                this.yOffset = yPos - y;
            }
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        if (dragging && state == 0) {
            dragging = false;
            dragged = false;
        }
    }

    @Override
    public void actionPerformed(GuiButton button) throws IOException {

    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {

    }

    private boolean isHovered(int mouseX, int mouseY) {
        return (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height);
    }
    private static TTFFontRenderer fontRender;
    public static TTFFontRenderer getFont() {
        if (fontRender == null) {
            fontRender = Eris.INSTANCE.fontManager.getFont("SFUI 18");
        }

        return fontRender;
    }
}
