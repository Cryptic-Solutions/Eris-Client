package me.spec.eris.module.modules.client;

import org.lwjgl.input.Keyboard;

import me.spec.eris.Eris;
import me.spec.eris.module.Category;
import me.spec.eris.module.Module;

public class ClickGUI extends Module {

    public ClickGUI() {
        super("ClickGUI", Category.CLIENT);
        this.setKey(Keyboard.KEY_RSHIFT, false);
    }

    @Override
    public void onEnable() {
        this.setToggled(false, true);
        mc.displayGuiScreen(Eris.instance.clickUI);
    }
}
