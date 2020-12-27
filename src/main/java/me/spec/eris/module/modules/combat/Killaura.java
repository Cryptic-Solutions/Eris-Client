package me.spec.eris.module.modules.combat;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import me.spec.eris.Eris;
import me.spec.eris.event.Event;
import me.spec.eris.event.player.EventUpdate;
import me.spec.eris.event.render.EventRender2D;
import me.spec.eris.module.Category;
import me.spec.eris.module.Module;
import me.spec.eris.module.modules.movement.Flight;
import me.spec.eris.module.modules.movement.Longjump;
import me.spec.eris.module.modules.movement.Scaffold;
import me.spec.eris.module.modules.movement.Speed;
import me.spec.eris.module.values.valuetypes.BooleanValue;
import me.spec.eris.module.values.valuetypes.ModeValue;
import me.spec.eris.module.values.valuetypes.NumberValue;
import me.spec.eris.ui.fonts.TTFFontRenderer;
import me.spec.eris.utils.PlayerUtils;
import me.spec.eris.utils.RenderUtilities;
import me.spec.eris.utils.ServerUtils;
import me.spec.eris.utils.TimerUtils;
import me.spec.eris.utils.math.MathUtils;
import me.spec.eris.utils.math.rotation.AngleUtility;
import me.spec.eris.utils.math.rotation.RotationUtils;
import me.spec.eris.utils.math.vec.Vector;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StringUtils;

public class Killaura extends Module {

    // finals mom is a hotty
    public Criticals crits;
    public TimerUtils critStopwatch;
    public ModeValue<Mode> modeValue = new ModeValue<>("Mode", Mode.SWITCH, this);
    public BooleanValue<Boolean> attackSettings = new BooleanValue<>("Attack settings", false, this, "Display settings for attacking");
    public BooleanValue<Boolean> aimingSettings = new BooleanValue<>("Aiming settings", false, this, "Display settings for aiming");
    public BooleanValue<Boolean> targetHUDSettings = new BooleanValue<>("Target HUD settings", false, this, "Display settings for target hud");
    public BooleanValue<Boolean> targettingSettings = new BooleanValue<>("Targetting settings", false, this, "Display settings for attacking");

    /*Attack settings*/
    public ModeValue<BlockMode> autoBlock = new ModeValue<>("Autoblock", BlockMode.OFF, this, () -> attackSettings.getValue(), "Autoblock modes");
    public NumberValue<Integer> clicksPerSecond = new NumberValue<Integer>("CPS", 15, 1, 20, this, () -> attackSettings.getValue(), "Clicks per second");
    public NumberValue<Integer> clicksPerSecondRandom = new NumberValue<Integer>("CPS Randomization", 0, 0, 5, this, () -> attackSettings.getValue(), "Dynamic randomization range");
    public NumberValue<Double> targetingDist = new NumberValue<Double>("Targeting Distance", 4.25, 2.0, 10.0, this, () -> attackSettings.getValue(), "Range at which the killaura aims and blocks");
    public NumberValue<Double> range = new NumberValue<Double>("Max Reach", 4.25, 2.0, 6.0, this, () -> attackSettings.getValue(), "Maximum range at which the killaura attacks");
    public NumberValue<Double> minrange = new NumberValue<Double>("Min Reach", 4.25, 2.0, 6.0, this, () -> attackSettings.getValue(), "Minimum range at which the killaura attacks");
    public NumberValue<Integer> abusiveAura = new NumberValue<Integer>("Reach VL", 0, 0, 6, this, () -> attackSettings.getValue(), "Advanced setting to exploit VL in reach checks, keep at 0 if unknown");
    public BooleanValue<Boolean> attackTarget = new BooleanValue<>("Attack Target", true, this, () -> attackSettings.getValue(), "Should the killaura attack, or just aim and block?");
    public BooleanValue<Boolean> dynamicAttack = new BooleanValue<>("Dynamic Attacks", true, this, () -> attackSettings.getValue(), "Optimize the speed at which the killaura attacks, great for hvh and hypixel");
    public BooleanValue<Boolean> armorBreak = new BooleanValue<>("Armor Breaker", false, this, () -> attackSettings.getValue(), "Send change item packets to increase the amount of armor broken, flags Nc+");
    public BooleanValue<Boolean> hitbox = new BooleanValue<>("Hitbox Checks", false, this, () -> attackSettings.getValue(), "Properly check if the killaura is aiming at the target before actually attacking");

    /*Aim settings*/
    public ModeValue<AimMode> aimMode = new ModeValue<>("Aim Mode", AimMode.BASIC, this, () -> aimingSettings.getValue(), "The mode it aims at - basic for NC+, assist is literal aim assist");
    public BooleanValue<Boolean> sprint = new BooleanValue<>("Sprint Checks", false, this, () -> aimingSettings.getValue(), "Select if the anticheat checks for sprint speed like cowards");
    public BooleanValue<Boolean> lockView = new BooleanValue<>("Silent Aiming", true, this, () -> aimingSettings.getValue(), "Aim silently, do not force player to change");

    public ModeValue<LockMode> lockviewMode = new ModeValue<>("Lockview Mode", LockMode.BOTH, this, () -> aimingSettings.getValue() && !lockView.getValue(), "Change the axis that aim is forced on (yaw/pitch etc)");
    public NumberValue<Integer> angleSmoothing = new NumberValue<Integer>("Smoothing", 20, 20, 100, this, () -> aimingSettings.getValue(), "How smooth is the aura");

    /*Targetting settings*/
    public BooleanValue<Boolean> invisibles = new BooleanValue<>("Invisibles", false, this, () -> targettingSettings.getValue(), "Attack invisibles");
    public BooleanValue<Boolean> animals = new BooleanValue<>("Animals", true, this, () -> targettingSettings.getValue(), "Attack animals");
    public BooleanValue<Boolean> players = new BooleanValue<>("Players", true, this, () -> targettingSettings.getValue(), "Attack players");
    public BooleanValue<Boolean> dead = new BooleanValue<>("Deads", true, this, () -> targettingSettings.getValue(), "Attack dead niggas");
    public BooleanValue<Boolean> mobs = new BooleanValue<>("Mobs", false, this, () -> targettingSettings.getValue(), "Attack monsters");
    public BooleanValue<Boolean> teams = new BooleanValue<>("Teams", false, this, () -> targettingSettings.getValue(), "Ignore team members");

    /*TargetHUD settings*/
    public ModeValue<TargetUIMode> targetHUDModeValue = new ModeValue<>("TargetHUD Mode", TargetUIMode.ICE, this, () -> targetHUDSettings.getValue(), "Change target hud mode");
    public BooleanValue<Boolean> syncOpacityValue = new BooleanValue<>("TargetHUD Opacity", true, this, () -> targetHUDSettings.getValue(), "How clear is targethud background");
    public BooleanValue<Boolean> targetHUDValue = new BooleanValue<>("TargetHUD", false, this, () -> targetHUDSettings.getValue(), "Display hud information on your target");

    public boolean shouldCritical;
    public double targetedarea;
    public boolean changingArea, blocking, reverse;
    public int delay, index, maxYaw, reachVL, hitCounter, maxPitch, targetIndex, rotationSwap, timesAttacked, offset, waitTicks;
    public float currentYaw, currentPitch, pitchincrease, animated = 20F, blockPosValue;
    public static Entity lastAimedTarget;
    public static EntityLivingBase target, currentEntity;
    public TimerUtils clientRaper;
    public TimerUtils clickStopwatch;
    public ArrayList<EntityLivingBase> targetList;
    public List<EntityLivingBase> targets = new ArrayList<>();

    public AngleUtility angleUtility;

    public enum Mode {SWITCH, MULTI}

    public enum TargetHudMode {OLD, NEW, OFF}

    public enum AimMode {ASSIST, ADVANCED, BASIC}

    public enum LockMode {YAW, PITCH, BOTH}

    public enum TargetUIMode {OLD, ICE, NOVOLINE;}

    public enum BlockMode {OFF, NCP, OFFSET, FALCON, FAKE}

    public Killaura() {
        super("Killaura", Category.COMBAT);
        angleUtility = new AngleUtility(70, 250, 70, 200);
        clickStopwatch = new TimerUtils();
        clientRaper = new TimerUtils();
        targetList = new ArrayList<>();
        critStopwatch = new TimerUtils();
    }

    @Override
    public void onEnable() {
        blockPosValue = 1;
        waitTicks = 0;
        delay = 73;
        crits = (Criticals) Eris.instance.modules.getModuleByClass(Criticals.class);
        unBlock();
        critStopwatch.reset();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        unBlock();
        super.onDisable();
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof EventUpdate) {
            EventUpdate eu = (EventUpdate) e;
            if (!PlayerUtils.isHoldingSword() && this.blocking) {
                this.blocking = false;
            }
            if (sprint.getValue() && Math.abs(mc.thePlayer.rotationYaw - eu.getYaw()) > 20 && mc.thePlayer.isSprinting()) {
                mc.thePlayer.setSprinting(false);
            }
            if (/*AutoUse.speedThrow || */Eris.getInstance().modules.isEnabled(Longjump.class)) {
                waitTicks = 3;
                return;
            }
            if (waitTicks > 0) {
                waitTicks--;
                return;
            }
            boolean scaffoldCheck = Eris.getInstance().modules.getModuleByClass(Scaffold.class).isToggled();
            if (scaffoldCheck) {
            	unBlock();
            	return;
            }
            if (modeValue.getValue() == Mode.SWITCH) {

                updateTargetList();
                if (targetList.isEmpty() || targetList.size() - 1 < targetIndex) {
                    reset(-1, eu);
                    return;
                }

                if (targetIndex == -1) {
                    reset(0, eu);
                    return;
                }
                if (!PlayerUtils.isValid(targetList.get(targetIndex), targetingDist.getValue(), invisibles.getValue(), teams.getValue(), dead.getValue(), players.getValue(), animals.getValue(), mobs.getValue())) {
                    reset(-1, eu);
                    return;
                }

                target = currentEntity = targetList.get(targetIndex); 
                shouldCritical = crits.waitTicks == 0 && mc.thePlayer.isCollidedVertically && mc.thePlayer.onGround && crits.isToggled()
                        && critStopwatch.hasReached(200) && !Eris.getInstance().modules.getModuleByClass(Speed.class).isToggled() && !Eris.getInstance().modules.getModuleByClass(Flight.class).isToggled()
                        && target.hurtResistantTime <= 15 && (crits.modeValue.getValue().equals(Criticals.Mode.WATCHDOG) && !mc.thePlayer.isMoving());
                if (eu.isPre()) {
                    if (Eris.getInstance().modules.getModuleByClass(Scaffold.class).isToggled()) {
                        index = 3;
                    } 
                    aim(eu);
                    unBlock();
                    prepareAttack(eu, scaffoldCheck); 
                    if (crits.isToggled()) {
                        crits.doUpdate(eu);
                    }      
                } else if (!eu.isPre()) {
                    if (!scaffoldCheck) {
                        block();
                    }
                }
            }
            if (modeValue.getValue() == Mode.MULTI) {
                for (EntityPlayer entity : mc.theWorld.playerEntities) {
                    if (entity.getDistanceSqToEntity(mc.thePlayer) <= range.getValue()) {
                        // TODO: stuff
                    }
                }
            }
        } else if (e instanceof EventRender2D) {
            if (targetHUDValue.getValue()) {
                if (font == null) {
                    font = Eris.getInstance().fontManager.getFont("SFUI 18");
                }
                switch (targetHUDModeValue.getValue()) {
                    case ICE: {
                        ScaledResolution rolf = new ScaledResolution(mc);
                        float xNigga = (rolf.getScaledWidth() / 2) + 80;
                        float yNigga = (rolf.getScaledHeight() / 2) + 120;
                        if (Minecraft.getMinecraft().thePlayer != null && currentEntity instanceof EntityPlayer) {
                            String playerName = StringUtils.stripControlCodes(currentEntity.getName()); 
                            int maxX2 = 30;
                            float maxX = Math.max(maxX2, mc.fontRendererObj.getStringWidth(playerName) + 47);
                            // RenderUtilities.drawRectangle(xNigga - 1, yNigga - 1, 142F, 44F, new Color(0,
                            // 0, 0, 150).getRGB());
                            RenderUtilities.drawRectangle(xNigga, yNigga, (20 + maxX) / 2 + 45, 35f,
                                    new Color(0, 0, 0, 90).getRGB());
                            // RenderUtilities.drawRectangle(xNigga, yNigga + 40, 140, 2, new Color(0, 0,
                            // 0).getRGB());
                            font.drawStringWithShadow(playerName, xNigga + 20F, yNigga + 6F,
                                    new Color(200, 200, 200, 255).getRGB());
                            RenderUtilities.drawEntityOnScreen((int) xNigga + 9, (int) yNigga + 28, 12, 270, 0, currentEntity);
                            float xSpeed = 133f / (Minecraft.getDebugFPS() * 1.05f);
                            float desiredWidth = ((maxX - maxX2 - 2) / currentEntity.getMaxHealth())
                                    * Math.min(currentEntity.getHealth(), currentEntity.getMaxHealth());
                            if (desiredWidth < animated || desiredWidth > animated) {
                                if (Math.abs(desiredWidth - animated) <= xSpeed) {
                                    animated = desiredWidth;
                                } else {
                                    animated += (animated < desiredWidth ? xSpeed * 3 : -xSpeed);
                                }
                            }
                            RenderUtilities.drawRectangle(xNigga + 20, yNigga + 18F, animated, 10F,
                                    PlayerUtils.getHealthColor(currentEntity));
                            if (currentEntity.getHealth() != 0) {
                                font.drawStringWithShadow(
                                        String.valueOf(Math.round(currentEntity.getHealth())), xNigga + 56.5F - (font.getStringWidth(String.valueOf(Math.round(currentEntity.getHealth()))) / 2), yNigga + 19F,
                                        -1);
                            }

                        }
                        break;
                    }
                    case OLD: {
                        ScaledResolution rolf = new ScaledResolution(mc);
                        float xNigga = (rolf.getScaledWidth() / 2) + 150;
                        float yNigga = (rolf.getScaledHeight() / 2) + 120;
                        if (Minecraft.getMinecraft().thePlayer != null && currentEntity instanceof EntityPlayer) {
                            String playerName = "Name: " + StringUtils.stripControlCodes(currentEntity.getName());
                            int distance = (int) ((mc.thePlayer.getDistanceToEntity(currentEntity)));
                            RenderUtilities.drawRectangle(xNigga, yNigga, 140F, 40F, new Color(0, 0, 0, 90).getRGB());
                            RenderUtilities.drawRectangle(xNigga, yNigga + 40, 140, 2, new Color(0, 0, 0).getRGB());
                            if (currentEntity.getName().length() > 15)
                                playerName = "Name: LongNameNigga";
                            font.drawStringWithShadow(playerName, xNigga + 25.5F, yNigga + 4F,
                                    new Color(200, 200, 200, 255).getRGB());
                            font.drawStringWithShadow(
                                    "Distance: " + Integer.toString(distance) + "m", xNigga + 25.5F, yNigga + 15F,
                                    new Color(200, 200, 200, 255).getRGB());
                            font.drawStringWithShadow(
                                    "Armor: " + Math.round(currentEntity.getTotalArmorValue()), xNigga + 25.5F, yNigga + 25F,
                                    new Color(200, 200, 200, 255).getRGB());
                            RenderUtilities.drawEntityOnScreen((int) xNigga + 12, (int) yNigga + 31, 13,
                                    currentEntity.rotationYaw, -currentEntity.rotationPitch, currentEntity);
                            float xSpeed = 133f / (Minecraft.getDebugFPS() * 1.05f);
                            float desiredWidth = (140F / currentEntity.getMaxHealth())
                                    * Math.min(currentEntity.getHealth(), currentEntity.getMaxHealth());
                            if (desiredWidth < animated || desiredWidth > animated) {
                                if (Math.abs(desiredWidth - animated) <= xSpeed) {
                                    animated = desiredWidth;
                                } else {
                                    animated += (animated < desiredWidth ? xSpeed * 3 : -xSpeed);
                                }
                            }
                            RenderUtilities.drawRectangle(xNigga, yNigga + 40F, animated, 2F,
                                    PlayerUtils.getHealthColor(currentEntity));

                        }
                        break;
                    }
                    case NOVOLINE: {
                        if (currentEntity == null)
                            return;
                        ScaledResolution sr = new ScaledResolution(mc);
                        String name = StringUtils.stripControlCodes(currentEntity.getName());
                        float startX = 20;
                        float renderX = (sr.getScaledWidth() / 2) + startX;
                        float renderY = (sr.getScaledHeight() / 2) + 10;
                        int maxX2 = 30;
                        float healthPercentage = currentEntity.getHealth() / currentEntity.getMaxHealth();
                        if (currentEntity.getCurrentArmor(3) != null) {
                            maxX2 += 15;
                        }
                        if (currentEntity.getCurrentArmor(2) != null) {
                            maxX2 += 15;
                        }
                        if (currentEntity.getCurrentArmor(1) != null) {
                            maxX2 += 15;
                        }
                        if (currentEntity.getCurrentArmor(0) != null) {
                            maxX2 += 15;
                        }
                        if (currentEntity.getHeldItem() != null) {
                            maxX2 += 15;
                        }

                        float maxX = Math.max(maxX2, mc.fontRendererObj.getStringWidth(name) + 30);
                        Gui.drawRect(renderX, renderY, renderX + maxX, renderY + 40, new Color(0, 0, 0, 0.6f).getRGB());
                        Gui.drawRect(renderX, renderY + 38, renderX + (maxX * healthPercentage), renderY + 40,
                                PlayerUtils.getHealthColor(currentEntity));
                        mc.fontRendererObj.drawStringWithShadow(name, renderX + 25, renderY + 7, -1);
                        int xAdd = 0;
                        double multiplier = 0.85;
                        GlStateManager.pushMatrix();
                        GlStateManager.scale(multiplier, multiplier, multiplier);
                        if (currentEntity.getCurrentArmor(3) != null) {
                            mc.getRenderItem().renderItemAndEffectIntoGUI(currentEntity.getCurrentArmor(3),
                                    (int) ((((sr.getScaledWidth() / 2) + startX + 23) + xAdd) / multiplier),
                                    (int) (((sr.getScaledHeight() / 2) + 28) / multiplier));
                            xAdd += 15;
                        }
                        if (currentEntity.getCurrentArmor(2) != null) {
                            mc.getRenderItem().renderItemAndEffectIntoGUI(currentEntity.getCurrentArmor(2),
                                    (int) ((((sr.getScaledWidth() / 2) + startX + 23) + xAdd) / multiplier),
                                    (int) (((sr.getScaledHeight() / 2) + 28) / multiplier));
                            xAdd += 15;
                        }
                        if (currentEntity.getCurrentArmor(1) != null) {
                            mc.getRenderItem().renderItemAndEffectIntoGUI(currentEntity.getCurrentArmor(1),
                                    (int) ((((sr.getScaledWidth() / 2) + startX + 23) + xAdd) / multiplier),
                                    (int) (((sr.getScaledHeight() / 2) + 28) / multiplier));
                            xAdd += 15;
                        }
                        if (currentEntity.getCurrentArmor(0) != null) {
                            mc.getRenderItem().renderItemAndEffectIntoGUI(currentEntity.getCurrentArmor(0),
                                    (int) ((((sr.getScaledWidth() / 2) + startX + 23) + xAdd) / multiplier),
                                    (int) (((sr.getScaledHeight() / 2) + 28) / multiplier));
                            xAdd += 15;
                        }
                        if (currentEntity.getHeldItem() != null) {
                            mc.getRenderItem().renderItemAndEffectIntoGUI(currentEntity.getHeldItem(),
                                    (int) ((((sr.getScaledWidth() / 2) + startX + 23) + xAdd) / multiplier),
                                    (int) (((sr.getScaledHeight() / 2) + 28) / multiplier));
                        }
                        GlStateManager.popMatrix();
                        GuiInventory.drawEntityOnScreen((int) renderX + 12, (int) renderY + 33, 15, currentEntity.rotationYaw,
                                currentEntity.rotationPitch, currentEntity);

                        break;
                    }
                }
            }
        }
    }

    private TTFFontRenderer font;

    public void aim(EventUpdate e) {
        Vector.Vector3<Double> enemyCoords = new Vector.Vector3<>(
                target.getEntityBoundingBox().minX
                        + (target.getEntityBoundingBox().maxX - target.getEntityBoundingBox().minX) / 2,
                target.getEntityBoundingBox().minY - pitchincrease,
                target.getEntityBoundingBox().minZ
                        + (target.getEntityBoundingBox().maxZ - target.getEntityBoundingBox().minZ) / 2);
        Vector.Vector3<Double> myCoords = new Vector.Vector3<>(mc.thePlayer.getEntityBoundingBox().minX
                + (mc.thePlayer.getEntityBoundingBox().maxX - mc.thePlayer.getEntityBoundingBox().minX) / 2,
                mc.thePlayer.posY,
                mc.thePlayer.getEntityBoundingBox().minZ
                        + (mc.thePlayer.getEntityBoundingBox().maxZ - mc.thePlayer.getEntityBoundingBox().minZ)
                        / 2);
        AngleUtility.Angle srcAngle = new AngleUtility.Angle(!lockView.getValue() ? mc.thePlayer.rotationYaw : e.getYaw(), !lockView.getValue() ? mc.thePlayer.rotationPitch : e.getPitch());
        AngleUtility.Angle dstAngle = angleUtility.calculateAngle(enemyCoords, myCoords, target, rotationSwap);
        AngleUtility.Angle newSmoothing = angleUtility.smoothAngle(dstAngle, srcAngle, 300, 40 * 30);

        double x = target.posX - mc.thePlayer.posX + (target.lastTickPosX - target.posX) / 2;
        double z = target.posZ - mc.thePlayer.posZ + (target.lastTickPosZ - target.posZ) / 2;
        float destinationYaw = 0;
        if (lastAimedTarget != target) {
            index = 3;
            changingArea = false;
            targetedarea = rotationSwap = 0;
        }

        lastAimedTarget = target;
        if (!lockView.getValue()) {
            currentYaw = mc.thePlayer.rotationYaw;
        }
        double smooth = (1 + ((angleSmoothing.getValue()) * .035));
        destinationYaw = RotationUtils.constrainAngle(currentYaw - (float) -(Math.atan2(x, z) * (58 + targetedarea)));
        float pitch = newSmoothing.getPitch();
        if (pitch > 90f) {
            pitch = 90f;
        } else if (pitch < -90.0f) {
            pitch = -90.0f;
        }
        smooth = smooth + (mc.thePlayer.getDistanceToEntity(target) * .1 + (mc.thePlayer.ticksExisted % 4 == 0 ? 40 * .001 : 0));
        destinationYaw = (float) (currentYaw - destinationYaw / smooth);
        boolean ticks = mc.thePlayer.ticksExisted % 20 == 0;
        if (mc.thePlayer.ticksExisted % 15 == 0) {
            if (rotationSwap++ >= 3) {
                rotationSwap = 0;
            }
            pitchincrease += changingArea ? MathUtils.getRandomInRange(-.055, -.075) : MathUtils.getRandomInRange(.055, .075);
        }
        if (pitchincrease >= .9) {
            changingArea = true;
        }
        if (pitchincrease <= -.15) {
            changingArea = false;
        }
        if (aimMode.getValue().equals(AimMode.ASSIST)) {
            float playerYaw = mc.thePlayer.rotationYaw;
            float playerPitch = mc.thePlayer.rotationPitch;
            float f = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
            float f1 = f * f * f * 8.0F;
            float f2 = (float) maxYaw * f1;
            float f3 = (float) maxPitch * f1;
            if (Math.abs(playerYaw - destinationYaw) > 2) {
                if (RotationUtils.rayCast(playerYaw, playerPitch, range.getValue()) == null) {
                    if (playerYaw > destinationYaw) {
                        maxYaw -= MathUtils.getRandomInRange(5, 7);
                    } else {
                        maxYaw += MathUtils.getRandomInRange(5, 7);
                    }
                } else {
                    maxYaw *= .5;
                }
            } else {
                maxYaw *= .5;
            }
            if (Math.abs(playerPitch - AngleUtility.getRotations(target)[1]) > 2) {
                if (RotationUtils.rayCast(playerYaw, playerPitch, range.getValue()) == null) {
                    if (playerPitch > AngleUtility.getRotations(target)[1]) {
                        maxPitch += MathUtils.getRandomInRange(1, 3);
                    } else {
                        maxPitch -= MathUtils.getRandomInRange(1, 3);
                    }
                } else {
                    maxPitch *= .5;
                }
            } else {
                maxPitch *= .5;
            }
            
            mc.thePlayer.rotationPitch = MathHelper.clamp_float((float) ((double) playerPitch - (double) f3 * 0.15D), -90.0F, 90.0F);
            mc.thePlayer.rotationYaw = (float) ((double) playerYaw + (double) f2 * 0.15D);
        } else if (aimMode.getValue().equals(AimMode.ADVANCED)) {
            float theYaw = (float) MathUtils.preciseRound(destinationYaw, 1) + (ticks ? .243437f : .14357f);
            float thePitch = (float) MathUtils.preciseRound(newSmoothing.getPitch(), 1) + (ticks ? .1335f : .13351f);
            if (!lockView.getValue()) {
                switch (lockviewMode.getValue()) {
                    case PITCH:
                        mc.thePlayer.rotationPitch = thePitch;
                        e.setYaw(theYaw);
                    case YAW:
                        mc.thePlayer.rotationYaw = theYaw;
                        e.setPitch(thePitch);
                        break;
                    case BOTH:
                        mc.thePlayer.rotationYaw = theYaw;
                        mc.thePlayer.rotationPitch = thePitch;
                        break;
                }
            } else {
                e.setPitch(thePitch);
                e.setYaw(theYaw);
            }
        } else if (aimMode.getValue().equals(AimMode.BASIC)) {
            float theYaw = (float) MathUtils.preciseRound(AngleUtility.getRotations(target)[0], 1) + (ticks ? .243437f : .14357f);
            float thePitch = (float) MathUtils.preciseRound(AngleUtility.getRotations(target)[1], 1) + (ticks ? .1335f : .13351f);
            if (!lockView.getValue()) {
                switch (lockviewMode.getValue()) {
                    case PITCH:
                        mc.thePlayer.rotationPitch = thePitch;
                        e.setYaw(theYaw);
                    case YAW:
                        mc.thePlayer.rotationYaw = theYaw;
                        e.setPitch(thePitch);
                        break;
                    case BOTH:
                        mc.thePlayer.rotationYaw = theYaw;
                        mc.thePlayer.rotationPitch = thePitch;
                        break;

                }
            } else {
                e.setPitch(thePitch);
                e.setYaw(theYaw);
            }
        }

        currentPitch = e.getPitch();
        currentYaw = e.getYaw();
    }

    public void prepareAttack(EventUpdate e, boolean scaffoldCheck) {
        if (scaffoldCheck) return;
        Criticals crits = ((Criticals) Eris.getInstance().modules.getModuleByClass(Criticals.class));
        if (autoBlock.getValue().equals(BlockMode.FALCON) && PlayerUtils.isHoldingSword()) {
            if (mc.thePlayer.ticksExisted % 6 == 0) {
                if (blocking) {
                    mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                    blocking = false;
                } else {
                    mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C08PacketPlayerBlockPlacement(new BlockPos(blockPosValue, -1, blockPosValue), 255, mc.thePlayer.getHeldItem(), 0, 0, 0));
                    blocking = true;
                }
            }
            if (clickStopwatch.hasReached(delay)) {
                if (!blocking) {
                    attackPrepare(e);
                    delay = Math.max(50, (1000 / clicksPerSecond.getValue()) + offset);
                    offset += reverse ? -MathUtils.getRandomInRange(1, 3) : -MathUtils.getRandomInRange(1, 3);
                    if (offset > clicksPerSecondRandom.getValue()) {
                        reverse = true;
                    } else if (offset <= -clicksPerSecondRandom.getValue()) {
                        reverse = false;
                    }
                    clickStopwatch.reset();
                } else {
                    delay = 51;
                    clickStopwatch.reset();
                }
            }
        } else {
            if (clickStopwatch.hasReached(dynamicAttack.getValue() ? index > 0 ? 60 : (crits.airTime > 1 || mc.thePlayer.fallDistance >= .9 && mc.thePlayer.ticksExisted % 2 == 0) ? 50 : 50 + target.hurtTime / 2 : delay)) {
                attackPrepare(e);
                clickStopwatch.reset();
                delay = Math.max(50, (1000 / clicksPerSecond.getValue()) + offset);
                offset += reverse ? -MathUtils.getRandomInRange(1, 3) : -MathUtils.getRandomInRange(1, 3);
                if (offset > clicksPerSecondRandom.getValue()) {
                    reverse = true;
                } else if (offset <= -clicksPerSecondRandom.getValue()) {
                    reverse = false;
                }
            }
        }
    }

    public void attackPrepare(EventUpdate e) {
        boolean armorBreaker = armorBreak.getValue() && mc.thePlayer.ticksExisted % 4 == 0;
        if (armorBreaker) PlayerUtils.swapToItem();

        if (hitbox.getValue() || index > 0) {
            if (RotationUtils.rayCast(e.getYaw(), e.getPitch(), range.getValue()) != null) {
                attackExecutre(e, RotationUtils.rayCast(e.getYaw(), e.getPitch(), range.getValue()), reachVL > 0 ? range.getValue() + 1 : range.getValue() + 1, targetingDist.getValue(), !attackTarget.getValue());
            } else {
                mc.thePlayer.swingItem();
            }
            index--;
        } else {
            attackExecutre(e, target, reachVL > 0 ? range.getValue() + 1 : range.getValue(), targetingDist.getValue(), !attackTarget.getValue());
        }
        if (MathUtils.round((float) mc.thePlayer.getDistanceToEntity(target), 4f) <= range.getValue() + 1) {
            if (reachVL > 0) reachVL--;
        } else {
            reachVL = abusiveAura.getValue();
        }
        if (armorBreaker) PlayerUtils.swapBackToItem();
    }

    public void attackExecutre(EventUpdate e, EntityLivingBase target, double range, double targetRange, boolean dontAttack) {
        if (dontAttack) return;

        Criticals criticalsMod = (Criticals) Eris.getInstance().modules.getModuleByClass(Criticals.class);


        if (shouldCritical) {
            for (double offset : criticalsMod.getOffsets()) {
                mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY + offset, mc.thePlayer.posZ, false));
            }
            critStopwatch.reset();
        }

        if (target.getDistanceToEntity(mc.thePlayer) <= range) {
            boolean flag = mc.thePlayer.fallDistance > 0.0F && !mc.thePlayer.onGround && !mc.thePlayer.isOnLadder() && !mc.thePlayer.isInWater() && !mc.thePlayer.isPotionActive(Potion.blindness) && mc.thePlayer.ridingEntity == null && target instanceof EntityLivingBase;
            float f = (float) mc.thePlayer.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
            float f1 = 0.0F;
            if (target instanceof EntityLivingBase) {
                f1 = EnchantmentHelper.func_152377_a(mc.thePlayer.getHeldItem(), ((EntityLivingBase) target).getCreatureAttribute());
            } else {
                f1 = EnchantmentHelper.func_152377_a(mc.thePlayer.getHeldItem(), EnumCreatureAttribute.UNDEFINED);
            }
            if (f1 > 0.0F) {
                mc.thePlayer.onEnchantmentCritical(target);
            }
            mc.thePlayer.swingItem();
            mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C02PacketUseEntity(target, C02PacketUseEntity.Action.ATTACK));
        }

        timesAttacked += 1;
    }

    public void unBlock() {
        if (!PlayerUtils.isHoldingSword() || !blocking || autoBlock.getValue().equals(BlockMode.FALCON) || autoBlock.getValue().equals(BlockMode.OFF))
            return;

        if (autoBlock.getValue().equals(BlockMode.NCP) || autoBlock.getValue().equals(BlockMode.OFFSET)) {

            double value = autoBlock.getValue().equals(BlockMode.OFFSET) ? -(.133769420) : -1; 
            mc.getNetHandler().addToSendQueueNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(value, -1, value), EnumFacing.DOWN));
        }
        blocking = false;
    }

    public void block() { 
        if (!PlayerUtils.isHoldingSword() || blocking || autoBlock.getValue().equals(BlockMode.FALCON) || autoBlock.getValue().equals(BlockMode.OFF))
            return;

        if (autoBlock.getValue().equals(BlockMode.NCP) || autoBlock.getValue().equals(BlockMode.OFFSET)) {
            double value = autoBlock.getValue().equals(BlockMode.OFFSET) ? -(.133769420) : -1; 
            mc.getNetHandler().addToSendQueueNoEvent(new C08PacketPlayerBlockPlacement(new BlockPos(value, -1, value), 255, mc.thePlayer.inventory.getCurrentItem(), 0, 0, 0));
        }
        blocking = true;
    }

    void reset(int i, EventUpdate event) {
        unBlock();
        index = 0;
        currentEntity = null;
        targetIndex = i;
    }

    private void updateTargetList() {
        target = null;
        targetList.clear();
        mc.theWorld.getLoadedEntityList().forEach(entity -> {
            if (entity != null && entity instanceof EntityLivingBase) {
                if (PlayerUtils.isValid((EntityLivingBase) entity, targetingDist.getValue(), invisibles.getValue(), teams.getValue(), dead.getValue(), players.getValue(), animals.getValue(), mobs.getValue())) {
                    targetList.add((EntityLivingBase) entity);
                } else if (targetList.contains(entity)) {
                    targetList.remove(entity);
                }
            }
        });
        if (targetList.size() > 1) {
            targetList.sort(Comparator.comparingDouble(mc.thePlayer::getDistanceToEntity));
            targetList.sort((e1, e2) -> Boolean.compare(e2 instanceof EntityPlayer, e1 instanceof EntityPlayer));
        }
    }

}
