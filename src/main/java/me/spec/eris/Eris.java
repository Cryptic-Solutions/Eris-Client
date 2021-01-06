package me.spec.eris;

import java.awt.*;

import me.spec.eris.client.integration.discord.DiscordIntegration;
import me.spec.eris.client.integration.kill.KillTracker;
import me.spec.eris.client.integration.playtime.PlaytimeTracker;
import me.spec.eris.client.integration.server.ServerIntegration;
import me.spec.eris.client.managers.*;
import me.spec.eris.client.ui.hud.CustomHUD;
import org.lwjgl.opengl.Display;

import libraries.thealtening.service.ServiceSwitcher;
import me.spec.eris.api.config.file.FileManager;
import me.spec.eris.client.ui.click.ClickGui;
import me.spec.eris.client.ui.fonts.FontManager;
import me.spec.eris.client.ui.fonts.TTFFontRenderer;

public class Eris {

	public static Eris INSTANCE;
	private Color clientColor = new Color(255, 0, 0);;

	/*
	Variables
	 */
	public String alteningAPI;

	/*
	Server integration
	 */
	public DiscordIntegration discordIntegration;
	public ServerIntegration serverIntegration;
	private PlaytimeTracker playtimeTracker;
	public KillTracker killTracker;

	/*
	Managers
	 */
	public ModuleManager moduleManager;
	public FileManager fileManager;
	public NotificationManager notificationManager;
	public FontManager fontManager;
	public CommandManager commandManager;
	public FriendManager friendManager;
	public CustomHUDManager customHUDManager;
	public ClickGui clickUI;
	public CustomHUD customHud;
	public ConfigManager configManager;
	public ServiceSwitcher serviceSwitcher;

	public void onStart() {
		Display.setTitle(getFormattedClientName());
		this.discordIntegration = new DiscordIntegration();
		this.serverIntegration = new ServerIntegration();
		this.playtimeTracker = new PlaytimeTracker();
		this.killTracker = new KillTracker();
		this.serviceSwitcher = new ServiceSwitcher();
		this.fontManager = new FontManager();
		this.notificationManager = new NotificationManager();
		this.moduleManager = new ModuleManager();
		this.fileManager = new FileManager();
		this.friendManager = new FriendManager();
		this.commandManager = new CommandManager();
		this.configManager = new ConfigManager();
		this.customHUDManager = new CustomHUDManager();
		this.clickUI = new ClickGui();
		this.customHud = new CustomHUD(false);
		//new AntiVirus().start();
		//new Connection().start();
	}

	public void onClose() {
		this.discordIntegration.stopRPC();
	}

	public static Eris getInstance() {
		return INSTANCE;
	}

	public String getClientName() {
		return "Eris";
	}

	public double getClientBuild() {
		return 0.1;
	}

	public String getClientBuildExperimental() {
		return "010221";
	}

	public String getClientRelease() {
		return "DEV";
	}

	public String getCommandPrefix() {
		return "-";
	}

	public String getFormattedClientName() {
		return getClientName() + " v" + getClientBuild() + "b" + " | " + getClientRelease();
	}

	public int getClientColor() {
		return clientColor.getRGB();
	}

	public Color getClientColor2() {
		return clientColor;
	}

	public void setClientColor(Color clientColor) {
		this.clientColor = clientColor;
	}

	public ServerIntegration getServerIntegration() {
		return serverIntegration;
	}

	public PlaytimeTracker getPlaytimeTracker() {
		return playtimeTracker;
	}

	public ModuleManager getModuleManager() {
		return moduleManager;
	}

	public FileManager getFileManager() {
		return fileManager;
	}

	public NotificationManager getNotificationManager() {
		return notificationManager;
	}

	public FontManager getFontManager() {
		return fontManager;
	}

	public CommandManager getCommandManager() {
		return commandManager;
	}

	public FriendManager getFriendManager() {
		return friendManager;
	}

	public ClickGui getClickUI() {
		return clickUI;
	}

	public ConfigManager getConfigManager() {
		return configManager;
	}

	public ServiceSwitcher getServiceSwitcher() {
		return serviceSwitcher;
	}

	public TTFFontRenderer getFontRenderer() {
		return Eris.INSTANCE.fontManager.getFont("SFUI 18");
	}

	public TTFFontRenderer getFontRendererChat() {
		return Eris.INSTANCE.fontManager.getFont("SFUI 16");
	}
}
