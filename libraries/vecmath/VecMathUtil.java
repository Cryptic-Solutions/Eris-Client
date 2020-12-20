// 
// Decompiled by Procyon v0.5.36
// 

package libraries.vecmath;

class VecMathUtil {
    static int floatToIntBits(final float f) {
        if (f == 0.0f) {
            return 0;
        }
        return Float.floatToIntBits(f);
    }

    static long doubleToLongBits(final double d) {
        if (d == 0.0) {
            return 0L;
        }
        return Double.doubleToLongBits(d);
    }

    private VecMathUtil() {
    }
}
