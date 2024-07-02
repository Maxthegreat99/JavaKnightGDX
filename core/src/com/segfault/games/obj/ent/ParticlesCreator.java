package com.segfault.games.obj.ent;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;

public class ParticlesCreator {
    private final float minSpeed;
    private final float maxSpeed;

    public ParticlesCreator(Texture t, float minSpeed, float maxSpeed) {

        this.minSpeed = minSpeed;
        this.maxSpeed = maxSpeed;
    }
}
