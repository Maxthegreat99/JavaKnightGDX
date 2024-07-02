package com.segfault.games.obj.comp;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;
import com.segfault.games.obj.Rec;
import com.segfault.games.obj.ent.ParticlesCreator;

public class CollisionDisposeComponent implements Component, Pool.Poolable {
    public ParticlesCreator particles = null;
    public CollisionRelationship relationship = null;
    public Rec rectangle = null;
    public float checkRange = 0f;
    @Override
    public void reset() {
        particles = null;
        rectangle = null;
        relationship = null;
        checkRange = 0f;
    }
}
