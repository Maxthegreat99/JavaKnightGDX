package com.segfault.games.obj.comp;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;
import com.segfault.games.obj.Rec;

import java.util.function.Supplier;

public class RectangleCollisionComponent implements Component, Pool.Poolable {
    public Rec targetRectangle = null;
    public float checkRange = 0f;
    @Override
    public void reset() {
        targetRectangle = null;
        checkRange = 0f;

    }
}
