package me.spec.eris.client.modules.client;

import me.spec.eris.api.module.ModuleCategory;
import org.lwjgl.input.Keyboard;

import me.spec.eris.Eris;
import me.spec.eris.api.module.Module;

public class ClickGUI extends Module {

    public ClickGUI() {
        super("ClickGUI", ModuleCategory.CLIENT);
        this.setKey(Keyboard.KEY_RSHIFT, false);
    }

    @Override
    public void onEnable() {
    	Eris.instance.moduleManager.getModuleByClass(this.getClass()).setToggled(false, false);
        mc.displayGuiScreen(Eris.instance.clickUI);
    }
}