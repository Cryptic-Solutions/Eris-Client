package me.spec.eris.utils;

import java.awt.Color;

import me.spec.eris.event.player.EventUpdate;
import me.spec.eris.module.modules.combat.AntiBot;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;

public class PlayerUtils {

    public static void sendPosition(double x, double y, double z, boolean ground, boolean movement) {
        if (movement) {
            Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueueNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(Minecraft.getMinecraft().thePlayer.posX + x, Minecraft.getMinecraft().thePlayer.posY + y, Minecraft.getMinecraft().thePlayer.posZ + z, EventUpdate.lastYaw, EventUpdate.lastPitch, ground));
        } else {
            Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueueNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(Minecraft.getMinecraft().thePlayer.posX + x, Minecraft.getMinecraft().thePlayer.posY + y, Minecraft.getMinecraft().thePlayer.posZ + z, ground));
        }
    }

    public static boolean isValid(EntityLivingBase entity, double range, boolean invisible, boolean teams, boolean dead, boolean players, boolean animals, boolean monsters) {
        if (entity == Minecraft.getMinecraft().thePlayer)
            return false;


        if (entity instanceof EntityArmorStand)
            return false;
        if (invisible && entity.isInvisible())
            return false;
        if (dead && (entity.isDead || entity.getHealth() <= 0))
            return false;
        if (teams && entity != null && entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            if (isOnSameTeam(player)) return false; 
        }
        return (entity != null) && entity != Minecraft.getMinecraft().thePlayer
                && (entity instanceof EntityPlayer && players || entity instanceof EntityAnimal && animals
                || entity instanceof EntityMob && monsters || entity instanceof EntityVillager && animals)

                && entity.getDistanceToEntity(Minecraft.getMinecraft().thePlayer) <= range
                && !entity.getDisplayName().getFormattedText().toLowerCase().contains("[npc]")
                && !AntiBot.bots.contains(entity);
    }

    public static int getHealthColor(final EntityLivingBase player) {
        final float f = player.getHealth();
        final float f2 = player.getMaxHealth();
        final float f3 = Math.max(0.0f, Math.min(f, f2) / f2);
        return Color.HSBtoRGB(f3 / 3.0f, 1.0f, 0.75f) | 0xFF000000;
    }

    public static void swapBackToItem() {
        Minecraft.getMinecraft().playerController.windowClick(Minecraft.getMinecraft().thePlayer.inventoryContainer.windowId, 9, Minecraft.getMinecraft().thePlayer.inventory.currentItem, 2, Minecraft.getMinecraft().thePlayer);
        Minecraft.getMinecraft().playerController.windowClick(Minecraft.getMinecraft().thePlayer.inventoryContainer.windowId, Minecraft.getMinecraft().thePlayer.inventory.currentItem, 9, 2, Minecraft.getMinecraft().thePlayer);
    }

    public static void swapToItem() {
        Minecraft.getMinecraft().playerController.windowClick(Minecraft.getMinecraft().thePlayer.inventoryContainer.windowId, Minecraft.getMinecraft().thePlayer.inventory.currentItem, 9, 2, Minecraft.getMinecraft().thePlayer);
        Minecraft.getMinecraft().playerController.windowClick(Minecraft.getMinecraft().thePlayer.inventoryContainer.windowId, 9, Minecraft.getMinecraft().thePlayer.inventory.currentItem, 2, Minecraft.getMinecraft().thePlayer);
    }

    public static boolean isOnSameTeam(EntityPlayer entity) {
        if (!(entity.getTeam() != null && Minecraft.getMinecraft().thePlayer.getTeam() != null))
            return false;
        return entity.getDisplayName().getFormattedText().charAt(1) == Minecraft.getMinecraft().thePlayer.getDisplayName().getFormattedText().charAt(1);
    }

    public static boolean isHoldingSword() {
        if (Minecraft.getMinecraft().thePlayer != null && Minecraft.getMinecraft().theWorld != null && Minecraft.getMinecraft().thePlayer.getCurrentEquippedItem() != null && Minecraft.getMinecraft().thePlayer.inventory.getCurrentItem().getItem() instanceof ItemSword) {
            return true;
        }
        return false;
    }

    public static boolean isBad(final ItemStack item) {
        return !(item.getItem() instanceof ItemArmor || item.getItem() instanceof ItemTool || item.getItem() instanceof ItemBlock || item.getItem() instanceof ItemSword || item.getItem() instanceof ItemEnderPearl || item.getItem() instanceof ItemFood || (item.getItem() instanceof ItemPotion && !isBadPotion(item))) && !item.getDisplayName().toLowerCase().contains(EnumChatFormatting.GRAY + "(right click)");
    }

    public static boolean isBadPotion(final ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ItemPotion) {
            final ItemPotion potion = (ItemPotion) stack.getItem();
            if (ItemPotion.isSplash(stack.getItemDamage())) {
                for (final Object o : potion.getEffects(stack)) {
                    final PotionEffect effect = (PotionEffect) o;
                    if (effect.getPotionID() == Potion.poison.getId() || effect.getPotionID() == Potion.harm.getId() || effect.getPotionID() == Potion.moveSlowdown.getId() || effect.getPotionID() == Potion.weakness.getId()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
