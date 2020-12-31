package me.spec.eris;

import java.awt.Color;

import me.spec.eris.client.integration.playtime.PlaytimeTracker;
import me.spec.eris.client.integration.server.ServerIntegration;
import me.spec.eris.client.managers.*;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.Display;

import libraries.thealtening.service.ServiceSwitcher;
import me.spec.eris.api.config.file.FileManager;
import me.spec.eris.client.ui.click.ClickGui;
import me.spec.eris.client.ui.fonts.FontManager;
import me.spec.eris.client.ui.fonts.TTFFontRenderer;

public class Eris {

	public static Eris INSTANCE;

	/*
	Variables
	 */
	public String alteningAPI;

	/*
	Server integration
	 */
	private ServerIntegration serverIntegration;
	private PlaytimeTracker playtimeTracker;

	/*
	Managers
	 */
	public ModuleManager moduleManager;
	public FileManager fileManager;
	public NotificationManager notificationManager;
	public FontManager fontManager;
	public CommandManager commandManager;
	public FriendManager friendManager;
	public ClickGui clickUI;
	public ConfigManager configManager;
	public ServiceSwitcher serviceSwitcher;

	public void onStart() {
		Display.setTitle(getFormattedClientName());
		this.serverIntegration = new ServerIntegration();
		this.playtimeTracker = new PlaytimeTracker();
		this.serviceSwitcher = new ServiceSwitcher();
		this.fontManager = new FontManager();
		this.notificationManager = new NotificationManager();
		this.moduleManager = new ModuleManager();
		this.fileManager = new FileManager();
		this.friendManager = new FriendManager();
		this.commandManager = new CommandManager();
		this.configManager = new ConfigManager();
		this.clickUI = new ClickGui();
		//new AntiVirus().start();
		//new Connection().start();
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

	public String getClientRelease() {
		return "DEV";
	}

	public String getFormattedClientName() {
		return getClientName() + " v" + getClientBuild() + "b" + " | " + getClientRelease();
	}

	public static Color getClientColor() {
		return new Color(255, 0, 0);
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
}
