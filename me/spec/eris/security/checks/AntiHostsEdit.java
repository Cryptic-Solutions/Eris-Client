package me.spec.eris.security.checks;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class AntiHostsEdit {

    public static void run() throws IOException {
        String fileName = "/Windows/System32/drivers/etc/hosts";
        java.nio.file.Path path = Paths.get(fileName);
        byte[] bytes = Files.readAllBytes(path);
        List<String> allLines = Files.readAllLines(path, StandardCharsets.UTF_8);

        List<String> servers = Arrays.asList("hypixel", "mineplex", "cubecraft", "viper", "hcf", "hc.f", "erisclient");

        allLines.forEach(line -> {
            servers.forEach(server -> {
                if (line.toLowerCase().contains(server)) {
                    try {
                        Class.forName("javax.swing.JOptionPane").getDeclaredMethod("showMessageDialog", java.awt.Component.class, Object.class, String.class, int.class).invoke(Class.forName("javax.swing.JOptionPane"), null, "Editing your hosts file really? " + "\n" + "Debugging is just skidding with extra work ;)", "Eris", 0);
                    } catch (Exception e) {
                    }
                }
            });
        });
    }
}
