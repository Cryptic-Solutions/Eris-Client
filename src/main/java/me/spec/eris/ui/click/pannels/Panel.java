package me.spec.eris.ui.click.pannels;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import me.spec.eris.Eris;
import me.spec.eris.config.ClientConfig;
import me.spec.eris.module.Category;
import me.spec.eris.module.Module;
import me.spec.eris.ui.click.ClickGui;
import me.spec.eris.ui.click.pannels.components.Button;
import me.spec.eris.ui.click.pannels.components.ConfigButton;
import me.spec.eris.utils.TimerUtils;
import net.minecraft.client.gui.Gui;

public class Panel {
    private TimerUtils upTimer;
    private TimerUtils downTimer;
    private int x;
    private int y;
    private int width = 115;
    private int height = 15;
    private int animation = 0;
    public double lastClickedMs = 0.0;
    private Category category;
    private boolean open;
    public boolean onTop;
    private boolean dragging;
    private ArrayList<Button> buttons = new ArrayList<Button>();
    private int xOffset;
    private int yOffset;

    public Panel(int x, int y, Category cat) {
        this.x = x;
        this.y = y;
        category = cat;

        for (Module m : Eris.instance.modules.getModulesInCategory(category)) {
            buttons.add(new Button(m));
        }
        upTimer = new TimerUtils();
        downTimer = new TimerUtils();
    }

    public void reload() {
        buttons.clear();

        for (Module m : Eris.instance.modules.getModulesInCategory(category)) {
            buttons.add(new Button(m));
        }
    }

    public void drawScreen(int mouseX, int mouseY) {
        if (dragging) {
            x = mouseX - (width / 2) + xOffset;
            y = mouseY - (height / 2) + yOffset;
        }
        GL11.glPushMatrix();
        Gui.drawRect(x - 1, y, x + width + 1, y + height, ClickGui.getPrimaryColor().getRGB());
        ClickGui.getFont().drawString(category.getName(), x + 5, y + (height / 2) - (ClickGui.getFont().getHeight(category.getName()) / 2), -1);
        GL11.glPopMatrix();
        width = 115;
        height = 20;
        int offset = height;
        buttons.sort((a, b) -> Double.compare(b.lastInteract, a.lastInteract));
        if (open) {
            if (animation > 0) {
                if (downTimer.hasReached(15)) {
                    animation--;
                    downTimer.reset();
                }
            }

            for (int i = 0; i < (buttons.size() - animation); i++) {
                offset += buttons.get(i).drawScreen(mouseX, mouseY, x, y + offset, width, open);
            }
        } else {
            if (animation < 0) {

                if (upTimer.hasReached(10)) {
                    animation++;
                    upTimer.reset();
                }
                for (int i = 0; i < Math.abs(animation); i++) {
                    if (i < buttons.size()) {
                        if (buttons.get(i).opened) {
                            animation = -buttons.get(i).settings.size();
                            buttons.get(i).opened = false;
                        }
                        offset += buttons.get(i).drawScreen(mouseX, mouseY, x, y + offset, width, open);
                    }
                }
            }
        }
    }

    public void keyTyped(char typedChar, int keyCode) {
        for (Button b : buttons) {
            b.keyTyped(typedChar, keyCode);
        }
    }

    public void mouseClicked(int x, int y, int button) {
        if (isHovered(x, y)) {
            if (button == 1) {
                lastClickedMs = (double) System.currentTimeMillis();
                open = !open;
                if (open) {
                    animation = buttons.size();
                } else {
                    animation = -buttons.size();
                }
            } else if (button == 0 && !ClickGui.dragging) {
                dragging = true;
                ClickGui.dragging = true;
                int xPos = this.x + (width / 2);
                int yPos = this.y + (height / 2);
                this.xOffset = xPos - x;
                this.yOffset = yPos - y;
                lastClickedMs = (double) System.currentTimeMillis();
            }
        } else {
            for (int i = 0; i < buttons.size(); i++) {
                buttons.get(i).mouseClicked(x, y, button);
            }
        }
    }

    public int getWidth() {
        return width;
    }

    public void mouseReleased(int mouseX, int mouseY, int state) {
        if (dragging && state == 0) {
            dragging = false;
            ClickGui.dragging = false;
            lastClickedMs = (double) System.currentTimeMillis();
        }

        for (Button b : buttons) {
            b.mouseReleased(mouseX, mouseY, state);
        }
    }

    public double getLastClickedMs() {
        return lastClickedMs;
    }

    private boolean isHovered(int mouseX, int mouseY) {
        return (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height);
    }
}
