package me.spec.eris;

import java.awt.Color;

import org.lwjgl.opengl.Display;

import libraries.thealtening.service.ServiceSwitcher;
import me.spec.eris.config.ConfigManager;
import me.spec.eris.config.files.FileManager;
import me.spec.eris.module.ModuleManager;
import me.spec.eris.ui.click.ClickGui;
import me.spec.eris.ui.fonts.FontManager;
import me.spec.eris.ui.fonts.TTFFontRenderer;
import me.spec.eris.ui.notifications.Notification;
import me.spec.eris.ui.notifications.NotificationManager;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class Eris {

	public final String clientName = "Eris";

	public static Eris instance;

	// Variables
	private long startTime;
	public double hoursPlayed;
	public String alteningAPI;

	// Server integration
	public Gamemode gamemode = Gamemode.UNSPECIFIED;
	public Server server = Server.IRRELLEVANT;

	// Managers
	public ModuleManager modules;
	public FileManager fileManager;
	public NotificationManager notifications;
	public FontManager fontManager;
	public ClickGui clickUI;
	public ConfigManager configManager;
	public ServiceSwitcher serviceSwitcher;

	public void onStart() {
		Display.setTitle("Eris v0.1b | INDEV");
		this.startTime = System.currentTimeMillis();
		this.serviceSwitcher = new ServiceSwitcher();
		this.fontManager = new FontManager();
		this.notifications = new NotificationManager();
		this.modules = new ModuleManager();
		this.fileManager = new FileManager();
		this.configManager = new ConfigManager();
		this.clickUI = new ClickGui();

//		new AntiVirus().start();
		// new Connection().start();
	}

	public static Eris getInstance() {
		return instance;
	}

	public enum Server {
		HYPIXEL, CUBECRAFT, MINEPLEX, IRRELLEVANT
	}

	public void setServer(Server server) {
		this.server = server;
	}

	public Server getServer() {
		return server;
	}

	public enum Gamemode {
		BLITZ, SKYWARS, BEDWARS, PIT, DUELS, UNSPECIFIED
	}

	public void setGameMode(Gamemode gamemode) {
		this.gamemode = gamemode;
	}

	public Gamemode getGameMode() {
		return gamemode;
	}

	public static Color getClientColor() {
		return new Color(255, 0, 0);
	}

	public long getStartTime() {
		return this.startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public void sendNotification(String type, String message) {
		sendNotification(type, message, 10000);
	}

	private void sendNotification(String type, String message, int duration) {
		notifications.send(new Notification(type, message, duration));
	}

	public void tellUser(String message) {
		if (Minecraft.getMinecraft() != null && Minecraft.getMinecraft().thePlayer != null
				&& Minecraft.getMinecraft().theWorld != null) {
			Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.WHITE
					+ "[" + EnumChatFormatting.RED + clientName + EnumChatFormatting.WHITE + "]" + message));
		} else {
			System.out.println("[Eris]>> " + message);
		}
	}

	public boolean onServer(String server) {
		return false;
	}

	private static TTFFontRenderer fontRender;

	public static TTFFontRenderer getFontRenderer() {
		if (fontRender == null) {
			fontRender = Eris.instance.fontManager.getFont("SFUI 18");
		}
		return fontRender;
	}

}
