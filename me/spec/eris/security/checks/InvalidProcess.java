package me.spec.eris.security.checks;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class InvalidProcess {

    public static void run() {
        java.util.List<String> invalid = java.util.Arrays.asList(new String[]{
                "fiddler",
                "wireshark",
                "sandboxie"/*,
			"eclipse",
			"intellij"*/
        });
        for (libraries.jprocess.main.model.ProcessInfo pi : libraries.jprocess.main.JProcesses.getProcessList()) {
            for (String str : invalid) {
                if (pi.getName().toLowerCase().contains(str)) {
                    try {
                        Class.forName("javax.swing.JOptionPane").getDeclaredMethod("showMessageDialog", java.awt.Component.class, Object.class, String.class, int.class).invoke(Class.forName("javax.swing.JOptionPane"), null, "Debuggers open... really?" + "\n" + "That's kinda SUS bro", "Eris", 0);
                    } catch (Exception e) {
                    }
                    try {
                        libraries.jprocess.main.JProcesses.killProcess((int) Class.forName("com.sun.jna.platform.win32.Kernel32").getDeclaredField("INSTANCE").get(Class.forName("com.sun.jna.platform.win32.Kernel32")).getClass().getDeclaredMethod("GetCurrentProcessId").invoke(Class.forName("com.sun.jna.platform.win32.Kernel32").getDeclaredField("INSTANCE").get(Class.forName("com.sun.jna.platform.win32.Kernel32"))));
                    } catch (Exception e) {
                    }
                    break;
                }
            }
        }
    }
}
