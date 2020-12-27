package me.spec.eris.module.modules.movement;

import java.util.Arrays;
import java.util.List;

import me.spec.eris.Eris;
import me.spec.eris.event.Event;
import me.spec.eris.event.player.EventStep;
import me.spec.eris.event.player.EventUpdate;
import me.spec.eris.module.Category;
import me.spec.eris.module.Module;
import me.spec.eris.module.antiflag.prioritization.ModulePrioritizer;
import me.spec.eris.module.antiflag.prioritization.enums.ModulePriority;
import me.spec.eris.module.antiflag.prioritization.enums.ModuleType;
import me.spec.eris.module.modules.combat.Criticals;
import me.spec.eris.module.values.valuetypes.ModeValue;
import me.spec.eris.utils.BlockUtils;
import me.spec.eris.utils.TimerUtils;
import net.minecraft.block.BlockAir;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;

public class Step extends Module {

    public Step() {
        super("Step", Category.MOVEMENT);
        setModuleType(ModuleType.FLAGGABLE);
        setModulePriority(ModulePriority.HIGH);
    }

    public static boolean needStep;
    public static boolean safe;
    private ModeValue<Mode> mode = new ModeValue<Mode>("Mode", Mode.NCP, this);

    private enum Mode {NCP, VANILLA}

    private final TimerUtils stepDelay = new TimerUtils();
    public double height;

    @Override
    public void onEvent(Event e) {
        if (mc.thePlayer == null || mc.theWorld == null) return;
        if (e instanceof EventUpdate) {
            if (needStep && safe) {
                setMode(mode.getValue().toString());
                EventUpdate event = (EventUpdate) e;
                event.setOnGround(mc.thePlayer.onGround);
                event.setY(mc.thePlayer.posY);
            }
        }
        if (e instanceof EventStep) {
            EventStep event = (EventStep) e;
            if (event.isPre()) {
                double radius = 0.50;

                double currentX = mc.thePlayer.posX, currentY = mc.thePlayer.posY, currentZ = mc.thePlayer.posZ;
                boolean isInvalid = false;
                String[] invalidBlocks = {"chest", "slab", "stair", "anvil", "enchant"};

                for (double x = currentX - radius; x <= currentX + radius; x++) {
                    for (double y = currentY - radius; y <= currentY + radius; y++) {
                        for (double z = currentZ - radius; z <= currentZ + radius; z++) {
                            if (!isInvalid) {
                                String blockName = BlockUtils.getBlockAtPos(new BlockPos(x, y, z)).getUnlocalizedName().toLowerCase();
                                for (String s : invalidBlocks) {
                                    if (blockName.contains(s.toLowerCase())) isInvalid = true;
                                }
                            }
                        }
                    }
                }
                if (isInvalid || Eris.instance.modules.isEnabled(Speed.class)) return;
                if (mc.thePlayer.isInWater() || mc.thePlayer.isInLava() || mc.thePlayer.isOnLadder() || ModulePrioritizer.flaggableMovementModules() || BlockUtils.isOnLiquid(mc.thePlayer)) {
                    stepDelay.reset();
                } 
                if (stepDelay.hasReached(250)) {
                    event.setStepHeight(mc.thePlayer.isPotionActive(Potion.jump) ? 1 : 2.0f);
                } else {
                	event.setStepHeight(.626f);
                }
                height = 0;
            } else {
            	height = mc.thePlayer.getEntityBoundingBox().minY - mc.thePlayer.posY;
            	stepDelay.reset();
                needStep = true; 
                if (height > 0) {
    				Criticals crits = ((Criticals)Eris.getInstance().modules.getModuleByClass(Criticals.class));
    				crits.accumulatedFall = 0; 
    				if (crits.airTime > 0) {	
    					sendPosition(0,0,0,true,false);
    					crits.airTime = 0;
    					crits.waitTicks = 3;
    				}
                }
                double posX = mc.thePlayer.posX;
                double posY = mc.thePlayer.posY;
                double posZ = mc.thePlayer.posZ;
                double y = 0;
                if (height <= 1.) {
                    float first = .42f - 4.0e-9f;
                    float second = .753f;
                    if (height != 1) {
                        first *= height;
                        second *= height;
                        if (first > .425f) first = .425f;
                        if (second > .75f) second = .75f;
                        if (second < .5f) second = .5f;
                    }
                    if (first == 0.42f)
                        sendPosition(0, .41999998688698f, 0, !(BlockUtils.getBlockAtPos(new BlockPos(posX, posY + 0.41999998688698f, posZ)) instanceof BlockAir), false);
                    if (posY + second < posY + height)
                        sendPosition(0, second, 0, !(BlockUtils.getBlockAtPos(new BlockPos(posX, posY + second, posZ)) instanceof BlockAir), false);
                    return;
                } else if (height >= 1.5 && height <= 2.01) {
                    List<Float> heights = height <= 1.5 ? Arrays.asList(0.42f, 0.333f, 0.248f, 0.083f, -0.078f) : Arrays.asList(0.4249999f, 0.821001f, 0.699f, 0.598f, 1.02217f, 1.372f, 1.652f, 1.869f);
                    for (int i = 0; i < heights.size(); i++) {
                        sendPosition(0, y + heights.get(i), 0, !(BlockUtils.getBlockAtPos(new BlockPos(posX, y + heights.get(i), posZ)) instanceof BlockAir), false);
                    }
                }
                needStep = false;
                safe = false;
                stepDelay.reset();
            }
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }
}
