package me.spec.eris.config.files.filetypes;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import me.spec.eris.Eris;
import me.spec.eris.config.files.DataFile;
import me.spec.eris.utils.CryptUtils;
import me.spec.eris.utils.FileUtils;

public class TimePlayedFile extends DataFile {

    private String key;

    public TimePlayedFile() {
        super("TimePlayed.eriscnf");
        this.load();

        this.key = "basicly this is broken as FUCK	";
    }

    @Override
    public void save() {
        try {
            ArrayList<String> lines = FileUtils.getLines(this.file);
            double hours = 0;
            if (!lines.isEmpty() && lines.size() > 0 && !lines.get(0).isEmpty()) {
                hours = Double.parseDouble(CryptUtils.decrypt(key, lines.get(0)));
            }
            long time = System.currentTimeMillis() - Eris.instance.getStartTime();
            double minute = TimeUnit.MILLISECONDS.toMinutes(time);
            double start = minute / 60;
            if (start > 0) {
                hours += start;
                lines.clear();
                String toAdd = CryptUtils.encrypt(key, hours + "");
                lines.add(toAdd);
                System.out.println("Hours Played: " + hours);
                FileUtils.writeToFile(this.file, lines);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void load() {
        ArrayList<String> lines = FileUtils.getLines(this.file);
        if (!lines.isEmpty() && lines.size() > 0 && !lines.get(0).isEmpty()) {
            String encryptedTime = lines.get(0);
            Eris.instance.hoursPlayed = Double.parseDouble(CryptUtils.decrypt(key, encryptedTime));

        }
    }
}
