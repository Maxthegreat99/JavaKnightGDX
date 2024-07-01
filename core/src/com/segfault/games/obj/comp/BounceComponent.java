package com.segfault.games.obj.comp;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class BounceComponent implements Component, Pool.Poolable {
    public int maxBounces = 0;
    @Override
    public void reset() {
        maxBounces = 0;
    }
}
