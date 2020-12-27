package me.spec.eris.event.player;

import me.spec.eris.Eris;
import me.spec.eris.event.Event;
import me.spec.eris.module.modules.combat.Killaura;
import me.spec.eris.utils.math.rotation.RotationUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.potion.Potion;
import net.minecraft.util.MovementInput;

public class EventMove extends Event {

    public static double lastDistance;
    private double x;
    private double y;
    private double z;

    public EventMove(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getMotionY(double mY) {
        if (Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.jump)) {
            mY += (Minecraft.getMinecraft().thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1;
        }
        return mY;
    }

    public double getLegitMotion() {
        return 0.41999998688697815D;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public double getMovementSpeed() {
        double baseSpeed = 0.2873D;
        if (Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.moveSpeed)) {
            int amplifier = Minecraft.getMinecraft().thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
            baseSpeed *= 1.0 + 0.2 * (amplifier + 1);
        }
        return baseSpeed;
    }

    public double getMovementSpeed(double baseSpeed) {
        double speed = baseSpeed;
        if (Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.moveSpeed)) {
            int amplifier = Minecraft.getMinecraft().thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
            return speed *= 1.0 + 0.2 * (amplifier + 1);
        }
        return speed;
    }

    double forward = MovementInput.moveForward, strafe = MovementInput.moveStrafe,
            yaw = Minecraft.getMinecraft().thePlayer.rotationYaw;


    public void setMoveSpeed(double moveSpeed) {
        Minecraft mc = Minecraft.getMinecraft();
        double range = ((Killaura) Eris.getInstance().modules.getModuleByClass(Killaura.class)).range.getValue();
        MovementInput movementInput = mc.thePlayer.movementInput;
        double moveForward = movementInput.getForward();
		/*boolean targetStrafe = TargetStrafe.canStrafe();
		if (targetStrafe) {
			if (mc.thePlayer.getDistanceToEntity(Aura.currentEntity) <= ((TargetStrafe) Eris.getmodulemanager.getModuleByClass(TargetStrafe.class)).distance.getValue()) {
				moveForward = 0;
			} else {*/
      //  moveForward = 1;
        //}
        //}
        double moveStrafe = /*targetStrafe ? TargetStrafe.direction : */movementInput.getStrafe() * 1.0;
        double yaw = /*targetStrafe ? RotationUtils.getNeededRotations(Killaura.currentEntity)[0] :*/ mc.thePlayer.rotationYaw;
        double value = 1;
        if (moveStrafe > 0) {
        	moveStrafe = value;
        } else if (moveStrafe < 0) {
        	moveStrafe = -value;
        }
        if (moveForward != 0.0D) {
        	if (moveStrafe > 0.0D) {
        		yaw += (moveForward > 0.0D ? -45 : 45);
        	} else if (moveStrafe < 0.0D) {
        		yaw += (moveForward > 0.0D ? 45 : -45);
        	}
        	moveStrafe = 0.0D;
        	if (moveForward > 0.0D) {
        		moveForward = value;
        	} else if (moveForward < 0.0D) {
        		moveForward = -value;
        	}
            }
        setX(moveForward * moveSpeed * Math.cos(Math.toRadians(yaw + 90)) + moveStrafe * moveSpeed * Math.sin(Math.toRadians(yaw + 90)));
        setZ(moveForward * moveSpeed * Math.sin(Math.toRadians(yaw + 90)) - moveStrafe * moveSpeed * Math.cos(Math.toRadians(yaw + 90))); 
    }

    public double getJumpBoostModifier(double baseJumpHeight) {
        if (Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.jump)) {
            int amplifier = Minecraft.getMinecraft().thePlayer.getActivePotionEffect(Potion.jump).getAmplifier();
            baseJumpHeight += (float) (amplifier + 1) * 0.1F;
        }

        return baseJumpHeight;
    }
}
