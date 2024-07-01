package com.segfault.games.obj.comp;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class DecelerationComponent implements Component, Pool.Poolable {
    public float decelerationValue = 0.0f;
    public float comparator = 0f;
    @Override
    public void reset() {
        decelerationValue = 0.0f;
        comparator = 0.0f;
    }
}
