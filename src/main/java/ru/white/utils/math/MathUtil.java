package ru.white.utils.math;

import java.util.concurrent.ThreadLocalRandom;

public final class MathUtil {
    private MathUtil() {}

    public static float random(float min, float max) {
        if (min == max) return min;
        float lo = Math.min(min, max);
        float hi = Math.max(min, max);
        return lo + ThreadLocalRandom.current().nextFloat() * (hi - lo);
    }

    public static float randomLerp(float min, float max) {
        return random(min, max);
    }
}
