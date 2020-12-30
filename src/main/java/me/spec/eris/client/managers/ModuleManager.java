package me.spec.eris.client.managers;

import java.util.ArrayList;

import me.spec.eris.api.manager.Manager;
import me.spec.eris.api.module.ModuleCategory;
import me.spec.eris.api.module.Module;
import me.spec.eris.client.modules.client.AntiCrash;
import me.spec.eris.client.modules.client.ClickGUI;
import me.spec.eris.client.modules.client.Disabler;
import me.spec.eris.client.modules.combat.AntiBot;
import me.spec.eris.client.modules.combat.Criticals;
import me.spec.eris.client.modules.combat.Killaura;
import me.spec.eris.client.modules.combat.Velocity;
import me.spec.eris.client.modules.misc.AntiDesync;
import me.spec.eris.client.modules.movement.*;
import me.spec.eris.client.modules.persistance.FlagDetection;
import me.spec.eris.client.modules.player.AntiVoid;
import me.spec.eris.client.modules.player.ChestSteal;
import me.spec.eris.client.modules.player.InventoryManager;
import me.spec.eris.client.modules.player.NoFall;
import me.spec.eris.client.modules.player.NoRotate;
import me.spec.eris.client.modules.player.Phase;
import me.spec.eris.client.modules.render.ESP;
import me.spec.eris.client.modules.render.HUD;
import me.spec.eris.security.checks.Heartbeat;

public class ModuleManager extends Manager<Module> {

    @Override
    public void loadManager() {
        /*
        Combat
         */
        addToManagerArraylist(new AntiBot());
        addToManagerArraylist(new Killaura());
        addToManagerArraylist(new Velocity());
        addToManagerArraylist(new Criticals());

        /*
        Movement
         */
        addToManagerArraylist(new Step());
        addToManagerArraylist(new Speed());
        addToManagerArraylist(new Flight());
        addToManagerArraylist(new Sprint());
        addToManagerArraylist(new GuiMove());
        addToManagerArraylist(new Scaffold());
        addToManagerArraylist(new Longjump());
        addToManagerArraylist(new NoSlowDown());

        /*
        Misc
         */
        addToManagerArraylist(new Disabler());
        addToManagerArraylist(new ChestSteal());
        addToManagerArraylist(new InventoryManager());
        addToManagerArraylist(new AntiDesync());

        /*
        Player
         */
        addToManagerArraylist(new Phase());
        addToManagerArraylist(new NoFall());
        addToManagerArraylist(new AntiVoid());
        addToManagerArraylist(new NoRotate());

        /*
        Visual
         */
        addToManagerArraylist(new HUD());
        addToManagerArraylist(new ESP());

        /*
        Client
        */
        addToManagerArraylist(new ClickGUI());
        addToManagerArraylist(new AntiCrash());

        /*
        Persist
         */
        addToManagerArraylist(new FlagDetection());
        addToManagerArraylist(new Heartbeat());
    }

    public void onKey(int key) {
        for (Module m : getManagerArraylist()) {
            if (m != null) {
                if (m.getKey() == key) {
                    m.toggle(true);
                }
            }
        }
    }

    public Module getModuleByName(String name) {
        for (int m = 0; m < getManagerArraylist().size(); m++) {
            Module module = getManagerArraylist().get(m);
            if (module.getName().equalsIgnoreCase(name)) {
                return module;
            }
        }
        return null;
    }

    public ArrayList<Module> getModules() {
        return getManagerArraylist();
    }

    public ArrayList<Module> getModulesInCategory(ModuleCategory moduleCategory) {
        ArrayList<Module> mods = new ArrayList<Module>();
        for (Module m : getManagerArraylist()) {
            if (m.getCategory() == moduleCategory) {
                mods.add(m);
            }
        }
        return mods;
    }

    public boolean isEnabled(Class<?> class1) {
        Module m = this.getModuleByClass(class1);
        if (m != null) {
            return m.isToggled();
        }
        return false;
    }

    public Module getModuleByClass(Class<?> class1) {
        for (int i = 0; i < getManagerArraylist().size(); i++) {
            if (getManagerArraylist().get(i).getClass() == class1) {
                return getManagerArraylist().get(i);
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
