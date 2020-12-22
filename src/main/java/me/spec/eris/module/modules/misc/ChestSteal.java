package me.spec.eris.module.modules.misc;

import java.util.concurrent.ThreadLocalRandom;

import me.spec.eris.event.Event;
import me.spec.eris.event.player.EventUpdate;
import me.spec.eris.module.Category;
import me.spec.eris.module.Module;
import me.spec.eris.module.values.valuetypes.BooleanValue;
import me.spec.eris.module.values.valuetypes.NumberValue;
import me.spec.eris.utils.PlayerUtils;
import me.spec.eris.utils.TimerUtils;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.ItemStack;

public class ChestSteal extends Module {

    public ChestSteal() {
        super("ChestSteal", Category.MOVEMENT);
    }

    private final TimerUtils timer = new TimerUtils();
    public int delay;
    public BooleanValue<Boolean> ignoreCustomName = new BooleanValue<>("Ignore Custom Name", true, this);
    public BooleanValue<Boolean> autoclose = new BooleanValue<>("Auto Close", true, this);
    public BooleanValue<Boolean> baditems = new BooleanValue<>("Item Filter", true, this);
    public NumberValue<Integer> delaySet = new NumberValue<>("Steal Delay", 120, 50, 1000, this);

    @Override
    public void onEvent(Event e) {
        if (e instanceof EventUpdate) {
            if (((EventUpdate) e).isPre()) {
                if (mc.thePlayer.openContainer != null && mc.thePlayer.openContainer instanceof ContainerChest && !(mc.currentScreen instanceof GuiChat)) {
                    ContainerChest theChest = (ContainerChest) mc.thePlayer.openContainer;
                    if (ignoreCustomName.getValue() && !theChest.getLowerChestInventory().getName().toLowerCase().contains("chest")) {
                        timer.reset();
                        setDelay();
                        return;
                    }

                    if (mc.currentScreen instanceof GuiChest) {
                        final GuiChest chest = (GuiChest) mc.currentScreen;
                        if (timer.hasReached(delay)) {
                            for (int index = 0; index < chest.lowerChestInventory.getSizeInventory(); ++index) {
                                final ItemStack stack = chest.lowerChestInventory.getStackInSlot(index);
                                if (stack != null && timer.hasReached(delay) && (!PlayerUtils.isBad(stack) || !baditems.getValue())) {
                                    mc.playerController.windowClick(chest.inventorySlots.windowId, index, 0, 1, mc.thePlayer);
                                    setDelay();
                                    timer.reset();
                                }
                            }
                            if (isChestEmpty(chest) && autoclose.getValue()) {
                                mc.thePlayer.closeScreen();
                                setDelay();
                                timer.reset();
                                return;
                            }
                        }
                    }
                } else {
                    timer.reset();
                }
            }
        }
    }


    private boolean isChestEmpty(GuiChest chest) {
        int itemsLeft = 0;
        for (int index = 0; index < chest.lowerChestInventory.getSizeInventory(); ++index) {
            final ItemStack stack = chest.lowerChestInventory.getStackInSlot(index);
            if (stack != null && (!PlayerUtils.isBad(stack) || !baditems.getValue())) {
                itemsLeft += 1;
            }
        }
        return itemsLeft <= 0;
    }

    private void setDelay() {
        delay = (int) (delaySet.getValue() + ThreadLocalRandom.current().nextDouble(-10, 10));
    }


    @Override
    public void onEnable() {
        setDelay();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
