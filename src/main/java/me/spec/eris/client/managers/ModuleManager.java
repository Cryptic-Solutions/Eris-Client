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
        addToManagerArraylist(new AntiBot ("GhostNiggers"));
        addToManagerArraylist(new Killaura("BeanerBeater"));
        addToManagerArraylist(new Velocity("FatRomanian"));
        addToManagerArraylist(new Criticals("BalkinHardHitter"));

        /*
        Movement
         */
        addToManagerArraylist(new Step("WhitesCantJump"));
        addToManagerArraylist(new Speed("ChasedByCops"));
        addToManagerArraylist(new Flight("SpiritAirlines"));
        addToManagerArraylist(new Sprint("AirforceOnes"));
        addToManagerArraylist(new GuiMove("T-Mobile"));
        addToManagerArraylist(new Scaffold("StevePlaceBlockForYou"));
        addToManagerArraylist(new Longjump("NiggerJump"));
        addToManagerArraylist(new NoSlowDown("Tredmill"));

        /*
        Misc
         */
        addToManagerArraylist(new Disabler("AnticheatAutism"));
        addToManagerArraylist(new ChestSteal("AutoNigger"));
        addToManagerArraylist(new InventoryManager("HomelessShoppingkart"));
        addToManagerArraylist(new AntiDesync("StopStealingBlocks"));

        /*
        Player
         */
        addToManagerArraylist(new Phase("GoThroughBlocks"));
        addToManagerArraylist(new NoFall("AirforceActivity"));
        addToManagerArraylist(new AntiVoid("ConsutrctionWorker"));
        addToManagerArraylist(new NoRotate("AntiBackhand"));

        /*
        Visual
         */
        addToManagerArraylist(new HUD("SeeShitAppear"));
        addToManagerArraylist(new ESP("NiggerFinder"));

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
 
    public ArrayList<Module> getModulesInCategory(ModuleCategory moduleCategory) {
        ArrayList<Module> mods = new ArrayList<Module>();
        for (Module m : getManagerArraylist()) {
            if (m.getCategory() == moduleCategory)  mods.add(m);
        }
        return mods;
    }

    public boolean isEnabled(Class<?> class1) {
        return getModuleByClass(class1).isToggled();
    }

    public Module getModuleByClass(Class<?> class1) {
        for (int i = 0; i < getManagerArraylist().size(); i++) {
            if (getManagerArraylist().get(i).getClass() == class1)  return getManagerArraylist().get(i);
        }
        return null;
    }

    public ArrayList<Module> getModulesForRender() {
        ArrayList<Module> modulesForRender = new ArrayList<>();
        for (Module module : getModules()) {
            if (module.isToggled() && checkVisibility(module)) modulesForRender.add(module);
        }
        return modulesForRender;
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
