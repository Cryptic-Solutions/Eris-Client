package net.minecraft.util;

public class MovementInput {
    /**
     * The speed at which the player is strafing. Postive numbers to the left and negative to the right.
     */
    public static float moveStrafe;

    /**
     * The speed at which the player is moving forward. Negative numbers will move backwards.
     */
    public static float moveForward;
    public static boolean jump;
    public static boolean sneak;

    public void updatePlayerMoveState() {
    }

    public double getForward() {
        return moveForward;
    }

    public double getStrafe() {
        return moveStrafe;
    }
}
