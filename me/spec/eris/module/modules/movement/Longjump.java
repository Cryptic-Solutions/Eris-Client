package me.spec.eris.module.modules.movement;

import me.spec.eris.event.Event;
import me.spec.eris.event.player.EventMove;
import me.spec.eris.event.player.EventUpdate;
import me.spec.eris.module.Category;
import me.spec.eris.module.Module;
import me.spec.eris.module.antiflag.prioritization.ModulePrioritizer;
import me.spec.eris.module.antiflag.prioritization.enums.ModulePriority;
import me.spec.eris.module.antiflag.prioritization.enums.ModuleType;

public class Longjump extends Module {

	public Longjump() {
		super("LongJump", Category.MOVEMENT);
		setModuleType(ModuleType.FLAGGABLE);
		setModulePriority(ModulePriority.HIGH);
	}

	@Override
	public void onEvent(Event e) {
		if (!ModulePrioritizer.isModuleUsable(this)) return;

		if (e instanceof EventUpdate) {
			EventUpdate eu = (EventUpdate)e;
		}
		
		if (e instanceof EventMove) {
			EventMove em = (EventMove)e;
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
}
