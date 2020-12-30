package me.spec.eris.client.managers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
import me.spec.eris.client.security.checks.Heartbeat;

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
        managerArraylist.stream().filter(module -> module.getKey() == key).forEach(module -> module.toggle(true));
    }

    public Module getModuleByName(String name) {
        return managerArraylist.stream().filter(module -> module.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public List<Module> getModules() {
        return getManagerArraylist();
    }

    public List<Module> getModulesInCategory(ModuleCategory moduleCategory) {
        return managerArraylist.stream().filter(module -> module.getCategory() == moduleCategory).collect(Collectors.toList());
    }

    public boolean isEnabled(Class<?> clazz) {
        return getModuleByClass(clazz).isToggled();
    }

    public Module getModuleByClass(Class<?> clazz) {
        return getManagerArraylist().stream().filter(module -> module.getClass().equals(clazz)).findFirst().orElse(null);
    }

    public List<Module> getModulesForRender() {
        return getManagerArraylist().stream().filter(module -> module.isToggled() && checkVisibility(module)).collect(Collectors.toList());
    }

    public boolean checkVisibility(Module module) {
        return module.getClass() != HUD.class && !module.isHidden();
    }
}
