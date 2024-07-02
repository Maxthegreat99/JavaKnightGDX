package com.segfault.games.obj.comp;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class PointingComponent implements Component, Pool.Poolable {
    public boolean cursor = false;
    public DrawableComponent target = null;

    @Override
    public void reset() {
        cursor = false;
        target = null;
    }
}
