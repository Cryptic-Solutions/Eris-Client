package me.spec.eris.client.integration.discord;

import me.spec.eris.utils.world.TimerUtils;
import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;

public class DiscordIntegration {

    private boolean isRunning = false;
    private TimerUtils timerUtils = new TimerUtils();

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public DiscordIntegration() {
    //loadPresence();
    }

    public void loadPresence() {
        DiscordEventHandlers handlers = new DiscordEventHandlers.Builder().setReadyEventHandler((user) -> {
        }).build();
        DiscordRPC.discordInitialize("", handlers, true);

        new Thread("Discord Rich Presence") {
            @Override
            public void run() {
                callbackRPC();
            }
        }.start();

    }

    public void stopRPC() {
        setRunning(false);
        DiscordRPC.discordShutdown();
    }

    public void callbackRPC() {
        while(isRunning) {
            if(timerUtils.hasReached(50000)) {
                DiscordRPC.discordRunCallbacks();
                timerUtils.reset();
            }
        }
    }

    public void update(String state, String details) {
        if(isRunning) {
            DiscordRichPresence newPresence = new DiscordRichPresence.Builder(state).setDetails(details).setBigImage("rpc-image", "your gay").build();
            DiscordRPC.discordUpdatePresence(newPresence);
        }
    }
}
