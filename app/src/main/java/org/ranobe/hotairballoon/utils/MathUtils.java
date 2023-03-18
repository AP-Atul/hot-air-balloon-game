package org.ranobe.hotairballoon.utils;

public class MathUtils {
    public static float getXPercentOf(float total, float percent) {
        return (total * percent) / 100;
    }

    public static float getRandom(float min, float max) {
        return (float) (min + Math.random() * (max - min));
    }
}
