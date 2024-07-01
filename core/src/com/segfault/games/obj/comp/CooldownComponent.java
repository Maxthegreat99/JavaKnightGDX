package com.segfault.games.obj.comp;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class CooldownComponent implements Component, Pool.Poolable {
    public float cd = 0.0f;
    public float initialCd = 0.0f;
    @Override
    public void reset() {
        cd = 0.0f;
        initialCd = 0.0f;
    }
}
