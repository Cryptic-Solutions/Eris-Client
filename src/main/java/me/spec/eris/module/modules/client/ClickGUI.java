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
    	Eris.instance.modules.getModuleByClass(this.getClass()).setToggled(false, false); 
        mc.displayGuiScreen(Eris.instance.clickUI);
    }
}
