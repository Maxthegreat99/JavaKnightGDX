package com.segfault.games.obj.comp;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

public class ColRelationshipHolderComponent implements Component, Pool.Poolable {
    public CollisionRelationship relationship = null;
    @Override
    public void reset() {
        relationship = null;
    }
}
