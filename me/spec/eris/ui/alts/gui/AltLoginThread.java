package me.spec.eris.ui.alts.gui;

import java.net.Proxy;

import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;

import libraries.thealtening.service.AlteningServiceType;
import me.spec.eris.Eris;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Session;

public final class AltLoginThread
        extends Thread {
    private final String password;
    private String status;
    private final String username;
    private Minecraft mc = Minecraft.getMinecraft();
    boolean Mojang;

    public AltLoginThread(String username, String password, boolean mojang) {
        super("Alt Login Thread");
        this.username = username;
        this.password = password;
        Mojang = mojang;
        this.status = (Object) ((Object) EnumChatFormatting.GRAY) + "Waiting...";
    }

    private Session createSession(String username, String password) {
        if (Mojang) {
            Eris.instance.serviceSwitcher.switchToService(AlteningServiceType.MOJANG);
        } else {
            Eris.instance.serviceSwitcher.switchToService(AlteningServiceType.THEALTENING);
        }
        YggdrasilAuthenticationService service = new YggdrasilAuthenticationService(Proxy.NO_PROXY, "");
        YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication) service.createUserAuthentication(Agent.MINECRAFT);
        auth.setUsername(username);
        auth.setPassword(password);
        try {
            auth.logIn();
            return new Session(auth.getSelectedProfile().getName(), auth.getSelectedProfile().getId().toString(), auth.getAuthenticatedToken(), "mojang");
        } catch (AuthenticationException localAuthenticationException) {
            localAuthenticationException.printStackTrace();
        }
        return null;
    }

    public String getStatus() {
        return this.status;
    }

    @Override
    public void run() {
        if (this.password.equals("")) {
            Minecraft.getMinecraft().session = new Session(this.username, "", "", "mojang");
            this.status = (Object) ((Object) EnumChatFormatting.GREEN) + "Logged in. (" + this.username + " - offline name)";
            return;
        }
        this.status = (Object) ((Object) EnumChatFormatting.YELLOW) + "Logging in...";
        Session auth = this.createSession(this.username, this.password);
        if (auth == null) {
            this.status = (Object) ((Object) EnumChatFormatting.RED) + "Login failed!";
        } else {
            this.status = (Object) ((Object) EnumChatFormatting.GREEN) + "Logged in. (" + auth.getUsername() + ")";
            Minecraft.getMinecraft().session = auth;
            //Sight.instance.connection.printUsername();
        }
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

