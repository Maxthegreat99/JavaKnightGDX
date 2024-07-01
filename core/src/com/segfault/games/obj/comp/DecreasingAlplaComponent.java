package com.segfault.games.obj.comp;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class DecreasingAlplaComponent implements Component, Pool.Poolable {
    public float alphaDecrease = 0.0f;
    public float comparator = 0.0f;
    @Override
    public void reset() {
        alphaDecrease = 0.0f;
        comparator = 0.0f;
    }
}
