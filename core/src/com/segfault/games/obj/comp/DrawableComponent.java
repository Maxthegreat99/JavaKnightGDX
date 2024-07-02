package com.segfault.games.obj.comp;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Pool;

public class DrawableComponent implements Component, Pool.Poolable {
    public Sprite sprite = null;  // Image of the object
    public float alpha = 0.0f;
    public boolean blending = true;
    public int order = 0;
    @Override
    public void reset() {
        sprite.setOriginCenter();
        sprite = null;
        alpha = 0.0f;
        blending = true;
        order = 0;

    }
}
