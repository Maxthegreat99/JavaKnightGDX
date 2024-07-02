package com.segfault.games.obj.comp;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class TrailComponent implements Component, Pool.Poolable {
    public float trailCooldown = 0f;
    public float trailInitialCooldown = 0f;
    public float trailIninitalAlpha = 0f;
    public float trailAlphaDecrease = 0f;
    public float alphaComparator = 0f;
    @Override
    public void reset() {
        trailCooldown = 0f;
        trailInitialCooldown = 0f;
        trailIninitalAlpha = 0f;
        trailAlphaDecrease = 0f;
        alphaComparator = 0f;
    }
}
