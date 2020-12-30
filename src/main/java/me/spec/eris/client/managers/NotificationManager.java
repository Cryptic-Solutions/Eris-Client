package me.spec.eris.client.managers;

import me.spec.eris.Eris;
import me.spec.eris.api.manager.Manager;
import me.spec.eris.ui.fonts.TTFFontRenderer;
import me.spec.eris.api.notification.Notification;
import me.spec.eris.utils.misc.Helper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;

import java.awt.*;
import java.util.ArrayList;

public class NotificationManager extends Manager<Notification> {

    @Override
    public void loadManager() {

    }

    public void send(Notification notif) {
        addToManagerArraylist(notif);
    }

    public void delete(int index) {
        removeFromManagerArraylistIndex(index);
    }

    public void render() {
        boolean notifications = true;
        ArrayList<Notification> notifs = Eris.instance.notificationManager.getNotifications();
        if (notifs == null || notifs.isEmpty()) {
            return;
        }
        float yPos = -40;
        for (int k = 0; k < notifs.size(); k++) {
            Notification n = notifs.get(k);
            if (n.isFinished() || !notifications) {
                Eris.instance.notificationManager.delete(notifs.indexOf(n));
                if (!notifications) {
                    Helper.sendMessage(n.getDescription());
                }
            } else {
                float duration = (float) ((float) n.getLifeTime() / (float) n.getDuration());
                float fadeIn = 0.1f;
                float fadeOut = 0.85f;

                ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
                float drawX = 0;
                TTFFontRenderer font = Eris.instance.fontManager.getFont("SFUI 18");
                float width = Math.max(Math.max(font.getStringWidth(n.getTitle()), font.getStringWidth(n.getDescription())), 70);

                if (duration < fadeIn) {
                    drawX = Math.min(1, duration / fadeIn);
                } else if (duration >= fadeIn && duration < fadeOut) {
                    drawX = 1;
                } else if (duration >= fadeOut) {
                    float percentage = (duration - fadeOut) / (1 - fadeOut);
                    drawX = 1 - percentage;
                }
                int height = 25;
                float drawXPosition = sr.getScaledWidth() - (width * drawX) - 3;
                Gui.drawRect(drawXPosition, sr.getScaledHeight() + yPos, drawXPosition + width, sr.getScaledHeight() + yPos + height, new Color(0, 0, 0, 100).getRGB());
                font.drawStringWithShadow(EnumChatFormatting.BOLD + n.getTitle(), drawXPosition + 1, sr.getScaledHeight() + yPos + 2, -1);
                font.drawStringWithShadow(n.getDescription(), drawXPosition + 2, sr.getScaledHeight() + yPos + 4 + font.getHeight(n.getTitle()), -1);
                Gui.drawRect(drawXPosition, sr.getScaledHeight() + yPos + height - 2, drawXPosition + (width * (1 - duration)), sr.getScaledHeight() + yPos + height, new Color(255, 255, 255, 100).getRGB());
                yPos -= 35;
            }
        }
    }

    public ArrayList<Notification> getNotifications() {
        return getManagerArraylist();
    }
}