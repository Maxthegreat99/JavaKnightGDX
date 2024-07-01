package com.segfault.games.util;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;

public class ParticlesCreator {

    private final Sprite sprite = new Sprite();
    private final float minSpeed;
    private final float maxSpeed;

    public ParticlesCreator(Texture t, float minSpeed, float maxSpeed, float minSize, float maxSize) {
        sprite.setTexture(t);
        float s = MathUtils.random(minSize, maxSize);
        sprite.setSize(s, s);
        this.minSpeed = minSpeed;
        this.maxSpeed = maxSpeed;
    }
}
