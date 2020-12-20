package me.spec.eris.module;

import java.util.ArrayList;
import java.util.List;

import me.spec.eris.module.antiflag.detection.FlagDetect;
import me.spec.eris.module.modules.client.AntiCrash;
import me.spec.eris.module.modules.client.ClickGUI;
import me.spec.eris.module.modules.combat.*;
import me.spec.eris.module.modules.misc.ChestSteal;
import me.spec.eris.module.modules.misc.InventoryManager;
import me.spec.eris.module.modules.movement.*;
import me.spec.eris.module.modules.persistance.FlagDetection;
import me.spec.eris.module.modules.persistance.Heartbeat;
import me.spec.eris.module.modules.render.ESP;
import me.spec.eris.module.modules.render.HUD;

public class ModuleManager {

    private ArrayList<Module> modules = new ArrayList<Module>();

    public ModuleManager() {
        // Client
        modules.add(new ClickGUI());
        modules.add(new AntiCrash());

        // Movement
        modules.add(new Step());
        modules.add(new Speed());
        modules.add(new Flight());
        modules.add(new Sprint());
        modules.add(new Scaffold());
        modules.add(new Longjump());

        // Combat
        modules.add(new AntiBot());
        modules.add(new Killaura());
        modules.add(new Velocity());
        modules.add(new Criticals());
        //Misc bullshit
        modules.add(new ChestSteal());
        modules.add(new InventoryManager());

        //Visual
        modules.add(new HUD());
        modules.add(new ESP());
        //Persist

        modules.add(new FlagDetection());
        modules.add(new Heartbeat());
    }

    public void onKey(int key) {
        for (Module m : this.modules) {
            if (m.getKey() == key) {
                m.toggle(true);
            }
        }
    }

    public Module getModuleByName(String name) {
        for (int m = 0; m < modules.size(); m++) {
            Module module = modules.get(m);
            if (module.getName().equalsIgnoreCase(name)) {
                return module;
            }
        }
        return null;
    }

    public ArrayList<Module> getModules() {
        return this.modules;
    }

    public ArrayList<Module> getModulesInCategory(Category category) {
        ArrayList<Module> mods = new ArrayList<Module>();
        for (Module m : this.modules) {
            if (m.getCategory() == category) {
                mods.add(m);
            }
        }
        return mods;
    }

    public boolean isEnabled(Class class1) {
        Module m = this.getModuleByClass(class1);
        if (m != null) {
            return m.isToggled();
        }
        return false;
    }

    public Module getModuleByClass(Class class1) {
        for (int i = 0; i < this.modules.size(); i++) {
            if (this.modules.get(i).getClass() == class1) {
                return this.modules.get(i);
            }
        }
        return null;
    }

    public ArrayList<Module> getModulesForRender() {
        ArrayList<Module> modulesForRender = new ArrayList<>();
        for (Module module : getModules()) {
            if (module.isToggled() && checkVisibility(module)) {
                modulesForRender.add(module);
            }
        }
        return modulesForRender;
    }

    public boolean checkVisibility(Module module) {
        return module != getModuleByClass(HUD.class) && !module.isHidden();
    }
}
