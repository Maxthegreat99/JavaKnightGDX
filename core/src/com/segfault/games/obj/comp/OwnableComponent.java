package com.segfault.games.obj.comp;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class OwnableComponent implements Component, Pool.Poolable {
    public Component owner = null;
    @Override
    public void reset() {
        owner = null;
    }
}
