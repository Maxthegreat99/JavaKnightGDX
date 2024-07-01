package com.segfault.games.obj.comp;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class MovingComponent implements Component, Pool.Poolable {
    public float dx = 0.0f;
    public float dy = 0.0f;
    @Override
    public void reset() {
        dx = 0.0f;
        dy = 0.0f;
    }
}
