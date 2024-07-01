package com.segfault.games.obj.comp;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class DamageComponent implements Component, Pool.Poolable {
    public int damage = 0;
    public LifeComponent target = null;
    @Override
    public void reset() {
        damage = 0;
        target = null;
    }
}
