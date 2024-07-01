package com.segfault.games.obj.comp;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class LifetimeComponent implements Component, Pool.Poolable {
    public float lifetime = 0.0f;

    @Override
    public void reset() {
        lifetime = 0.0f;
    }
}
