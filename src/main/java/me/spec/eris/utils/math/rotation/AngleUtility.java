package me.spec.eris.utils.math.rotation;

import java.util.Random;

import me.spec.eris.utils.math.MathUtils;
import me.spec.eris.utils.math.vec.Vector;
import me.spec.eris.utils.math.vec.vectors.Vec2f;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class AngleUtility {
    private float minYawSmoothing, maxYawSmoothing, minPitchSmoothing, maxPitchSmoothing;
    private Vector.Vector3<Float> delta;
    private Angle smoothedAngle;
    private final Random random;
    private float height = MathUtils.getRandomInRange(1.1F, 1.8F);
    private static Minecraft minecraft = Minecraft.getMinecraft();

    public AngleUtility(float minYawSmoothing, float maxYawSmoothing, float minPitchSmoothing, float maxPitchSmoothing) {
        this.minYawSmoothing = minYawSmoothing;
        this.maxYawSmoothing = maxYawSmoothing;
        this.minPitchSmoothing = minPitchSmoothing;
        this.maxPitchSmoothing = maxPitchSmoothing;
        random = new Random();
        delta = new Vector.Vector3<>(0F, 0F, 0F);
        smoothedAngle = new Angle(0F, 0F);
    }

    public static Vec2f getRotations(Vec3 origin, Vec3 position) {
        Vec3 org = new Vec3(origin.xCoord, origin.yCoord, origin.zCoord);
        Vec3 difference = position.subtract(org);
        double distance = difference.flat().lengthVector();
        float yaw = ((float) Math.toDegrees(Math.atan2(difference.zCoord, difference.xCoord)) - 90.0F);
        float pitch = (float) (-Math.toDegrees(Math.atan2(difference.yCoord, distance)));
        return new Vec2f(yaw, pitch);
    }

    public static Vec2f getRotations(Vec3 position) {
        return getRotations(minecraft.thePlayer.getPositionVector().addVector(0.0D, (double) minecraft.thePlayer.getEyeHeight(), 0.0D), position);
    }

    public static Vec2f getRotations(Entity entity) {
        return getRotations(minecraft.thePlayer.getPositionVector().addVector(0.0D, (double) minecraft.thePlayer.getEyeHeight(), 0.0D), entity.getPositionVector().addVector(0.0D, (double) (entity.getEyeHeight() / 2) / Minecraft.getMinecraft().thePlayer.getDistanceToEntity(entity), 0.0D));
    }

    public static float[] getRotations(EntityLivingBase ent) {
        double x = ent.posX;
        double z = ent.posZ;
        double y = ent.posY + ent.getEyeHeight() / 2.0F;
        return getRotationFromPosition(x, z, y);
    }

    public static float[] getRotationFromPosition(double x, double z, double y) {
        double xDiff = x - Minecraft.getMinecraft().thePlayer.posX;
        double zDiff = z - Minecraft.getMinecraft().thePlayer.posZ;
        double yDiff = y - Minecraft.getMinecraft().thePlayer.posY - 1.2;

        double dist = MathHelper.sqrt_double(xDiff * xDiff + zDiff * zDiff);
        float yaw = (float) (Math.atan2(zDiff, xDiff) * 180.0D / 3.141592653589793D) - 90.0F;
        float pitch = (float) -(Math.atan2(yDiff, dist) * 180.0D / 3.141592653589793D);
        return new float[]{yaw, pitch};
    }

    public float randomFloat(float min, float max) {
        return min + (random.nextFloat() * (max - min));
    }

    public Angle calculateAngle(Vector.Vector3<Double> destination, Vector.Vector3<Double> source, EntityLivingBase ent, int mode) {

        Angle angles = new Angle(0F, 0F);
        delta.setX(destination.getX().floatValue() - source.getX().floatValue())
                .setY((destination.getY().floatValue() + height) - (source.getY().floatValue() + height))
                .setZ(destination.getZ().floatValue() - source.getZ().floatValue());

        double hypotenuse = Math.hypot(delta.getX().doubleValue(), delta.getZ().doubleValue());
        float yawAtan = ((float) Math.atan2(delta.getZ().floatValue(), delta.getX().floatValue()));
        float pitchAtan = ((float) Math.atan2(delta.getY().floatValue(), hypotenuse));
        float deg = ((float) (180 / Math.PI));
        float yaw = ((yawAtan * deg) - 90F);
        float pitch = -(pitchAtan * deg);

        return angles.setYaw(yaw).setPitch(pitch).constrantAngle();
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public Angle smoothAngle(Angle destination, Angle source, float pitch, float yaw) {
        return smoothedAngle
                .setYaw(source.getYaw() - destination.getYaw() - (Math.abs(source.getYaw() - destination.getYaw()) > 5 ? Math.abs(source.getYaw() - destination.getYaw()) / Math.abs(source.getYaw() - destination.getYaw()) * 2 / 2 : 0))
                .setPitch(source.getPitch() - destination.getPitch())
                .constrantAngle()
                .setYaw(source.getYaw() - smoothedAngle.getYaw() / yaw * randomFloat(minYawSmoothing, maxYawSmoothing))
                .constrantAngle()
                .setPitch(source.getPitch() - smoothedAngle.getPitch() / pitch * randomFloat(minPitchSmoothing, maxPitchSmoothing))
                .constrantAngle();
    }

    public static class Angle extends Vector.Vector2<Float> {
        public int calls;
        public int requests;
        public float lastPitch;

        public Angle(Float x, Float y) {
            super(x, y);
        }

        public Angle setYaw(Float yaw) {
            setX(yaw);
            return this;
        }

        public Angle setPitch(Float pitch) {
            setY(pitch);
            return this;
        }

        public Float getYaw() {
            return getX().floatValue();
        }

        public Float getPitch() {
            return (lastPitch = getY().floatValue());

        }

        public Angle constrantAngle() {

            this.setYaw(this.getYaw() % 360F);
            this.setPitch(this.getPitch() % 360F);

            while (this.getYaw() <= -180F) {
                this.setYaw(this.getYaw() + 360F);
            }

            while (this.getPitch() <= -180F) {
                this.setPitch(this.getPitch() + 360F);
            }

            while (this.getYaw() > 180F) {
                this.setYaw(this.getYaw() - 360F);
            }

            while (this.getPitch() > 180F) {
                this.setPitch(this.getPitch() - 360F);
            }

            return this;
        }
    }
}