package com.segfault.games.obj.comp;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class LifeComponent implements Component, Pool.Poolable {
    public int life = 0;
    @Override
    public void reset() {
        life = 0;
    }
}
