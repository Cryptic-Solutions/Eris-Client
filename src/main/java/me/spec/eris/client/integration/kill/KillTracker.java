package me.spec.eris.client.integration.kill;

import me.spec.eris.Eris;
import me.spec.eris.api.event.Event;
import me.spec.eris.api.manager.Manager;
import me.spec.eris.client.events.chat.ChatMessageEvent;
import me.spec.eris.client.events.player.EventUpdate;
import me.spec.eris.client.integration.server.interfaces.Server;
import me.spec.eris.utils.string.StringUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.stats.StatList;

import java.util.ArrayList;
import java.util.HashMap;

public class KillTracker extends Manager<EntityPlayer> {

    private HashMap<EntityPlayer, Integer> playerKills = new HashMap<>();

    public HashMap<EntityPlayer, Integer> getPlayerKills() {
        return playerKills;
    }

    public void addKillToPlayer(EntityPlayer entityPlayer, int kills) {
        if(Minecraft.getMinecraft().theWorld != null) {
            for(Entity entity : Minecraft.getMinecraft().theWorld.loadedEntityList) {
              if(entity instanceof EntityPlayer && entity != null) {
                  EntityPlayer player = (EntityPlayer) entity;
                  getPlayerKills().put(player, getPlayerKills().getOrDefault(player, 0) + 1);
              }
            }
        }
    }
}
