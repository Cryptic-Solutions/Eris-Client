package me.spec.eris.module.modules.movement;

import org.lwjgl.input.Keyboard;

import me.spec.eris.Eris;
import me.spec.eris.event.Event;
import me.spec.eris.event.player.EventMove;
import me.spec.eris.event.player.EventUpdate;
import me.spec.eris.module.Category;
import me.spec.eris.module.Module;
import me.spec.eris.module.antiflag.prioritization.ModulePrioritizer;
import me.spec.eris.module.antiflag.prioritization.enums.ModulePriority;
import me.spec.eris.module.antiflag.prioritization.enums.ModuleType;
import me.spec.eris.module.values.valuetypes.ModeValue;
import me.spec.eris.module.values.valuetypes.NumberValue;
import me.spec.eris.utils.PlayerUtils;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;

public class Flight extends Module {

	private ModeValue<Mode> mode = new ModeValue<Mode>("Mode", Mode.VANILLA, this);
	private NumberValue<Float> speed = new NumberValue<Float>("Speed", 1F, 0.3F, 3F, this, "Speed");
	
	public Flight() {
		super("Flight", Category.MOVEMENT);
		setModuleType(ModuleType.FLAGGABLE);
		setModulePriority(ModulePriority.HIGHEST);
	}
	
	@Override
	public void onEvent(Event e) {
		setMode(mode.getValue().toString());
		if (ModulePrioritizer.isModuleUsable(this)) return;
		if (e instanceof EventMove) {
			EventMove em = (EventMove)e;
			switch (mode.getValue()) {
			case VANILLA:
				if (mc.gameSettings.keyBindJump.isKeyDown() && !mc.gameSettings.keyBindSneak.isKeyDown()) {
					em.setY(mc.thePlayer.motionY = this.speed.getValue());
				} else if (!mc.gameSettings.keyBindJump.isKeyDown() && mc.gameSettings.keyBindSneak.isKeyDown()) {
					em.setY(mc.thePlayer.motionY = -this.speed.getValue());
				} else {
					em.setY(mc.thePlayer.motionY = 0);
				} 
				em.setMoveSpeed(speed.getValue());
				break;
			}
		} else if (e instanceof EventUpdate) {
			EventUpdate event = (EventUpdate)e;
			switch (mode.getValue()) {
			case VANILLA:
 
				break;
			}
		}
	}
	
	@Override
	public void onEnable() {
		setLastDistance(0.0);
		super.onEnable();
	}
	
	@Override
	public void onDisable() {
		super.onDisable();
	}

	public enum Mode {
		VANILLA
	}
}
