package me.spec.eris.ui.click;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;

import me.spec.eris.Eris;
import me.spec.eris.module.Category;
import me.spec.eris.ui.click.pannels.Panel;
import me.spec.eris.ui.click.pannels.components.Mode;
import me.spec.eris.ui.fonts.TTFFontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

public class ClickGui extends GuiScreen {
    public static String toolTip = "no information to display";
    public static ArrayList<Panel> panels = new ArrayList<Panel>();
    public static boolean dragging = false;
    public boolean createdPanels;
    public Mode mode;

    public void reload(boolean reloadUserInterface) {
        for (Panel p : ClickGui.panels) {
            p.reload();
        }
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
    }

    @Override
    public void initGui() {
        super.initGui();
    }

    public ClickGui() {
        int x = 3;
        int y = 5;
        int count = 0;
        if (!createdPanels && panels.size() != Category.values().length) {
            for (Category c : Category.values()) {
                Panel p = new Panel(x, y, c);
                panels.add(p);
                x += p.getWidth() + 5;
                count++;
                if (count % 3 == 0) {
                    y += 50;
                    x = 3;
                }
                createdPanels = (panels.size() == Category.values().length);
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        ScaledResolution scalRes = new ScaledResolution(Minecraft.getMinecraft());
        Minecraft.getMinecraft().fontRendererObj.drawString(toolTip, scalRes.getScaledWidth() / 2, scalRes.getScaledHeight() / 2 + 300, new Color(255, 255, 255).getRGB());

        Eris.getInstance();
		drawGradientRect(0, 0, scalRes.getScaledWidth(), scalRes.getScaledHeight(), 0x00001215, new Color(0,0,0).getRGB());

        panels.sort((a, b) -> Double.compare(a.lastClickedMs, b.lastClickedMs));
        for (int i = 0; i < panels.size(); i++) {
            panels.get(i).onTop = i == 0;
            panels.get(i).drawScreen(mouseX, mouseY);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        for (Panel p : ClickGui.panels) {
            p.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        for (Panel p : ClickGui.panels) {
            p.mouseReleased(mouseX, mouseY, state);
        }
    }

    public boolean doesGuiPauseGame() {
        return false;
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        for (Panel p : ClickGui.panels) {
            p.keyTyped(typedChar, keyCode);
        }
        super.keyTyped(typedChar, keyCode);
    }

    public static Color getSecondaryColor(boolean setting) {
        return setting ? new Color(0, 0, 0, 200) : new Color(25, 25, 25, 200);
    }

    private static TTFFontRenderer fontRender;

    public static TTFFontRenderer getFont() {
        if (fontRender == null) {
            fontRender = Eris.instance.fontManager.getFont("SFUI 18");
        }
        return fontRender;
    }

    public static Color getPrimaryColor() {
        return Eris.getClientColor();
    }
}
