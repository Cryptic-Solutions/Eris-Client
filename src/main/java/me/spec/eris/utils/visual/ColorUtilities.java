package me.spec.eris.utils.visual;

import java.awt.*;
import java.util.Random;

public class ColorUtilities {

    public static int createGermanColor() {
        Random random = new Random();
        return Color.getHSBColor(random.nextFloat(), (random.nextInt(2000) + 4000) / 10000f, 1.0f).getRGB();
    }
}
