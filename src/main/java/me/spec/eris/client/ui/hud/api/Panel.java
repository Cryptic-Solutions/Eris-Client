package me.spec.eris.client.ui.hud.api;

import me.spec.eris.api.value.Value;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

import java.io.IOException;
import java.util.ArrayList;

public abstract class Panel {

    public Minecraft mc = Minecraft.getMinecraft();
    public int width, height, x, y, xOffset, yOffset;
    public boolean dragging, dragged;
    public Panel(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public int getWidth() { return width; }

    public abstract void drawScreen(int mouseX, int mouseY);

    public abstract void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException;

    public abstract void mouseReleased(int mouseX, int mouseY, int state);

    public abstract void actionPerformed(GuiButton button) throws IOException;

    public abstract void keyTyped(char typedChar, int keyCode) throws IOException;
}
