package me.spec.eris.module.modules.client;

import org.lwjgl.input.Keyboard;

import me.spec.eris.Eris;
import me.spec.eris.module.Category;
import me.spec.eris.module.Module;
import me.spec.eris.module.values.valuetypes.NumberValue;

public class ClickGUI extends Module {
	public NumberValue<Float> corners = new NumberValue<Float>("Corners", 2F, 1F, 4F, this, "Changes how round the Clickgui corners are");
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
