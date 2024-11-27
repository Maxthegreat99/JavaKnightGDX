package com.segfault.games.util;

import com.badlogic.gdx.math.MathUtils;

public class Math {
    public static float AngleBetween(float x1, float y1, float x2, float y2) {
        float deltaX = x2 - x1;
        float deltaY = y2 - y1;
        return MathUtils.atan2Deg(deltaY, deltaX);
    }
}
