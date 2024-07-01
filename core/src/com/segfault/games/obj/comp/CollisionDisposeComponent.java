package com.segfault.games.obj.comp;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;
import com.segfault.games.util.ParticlesCreator;

public class CollisionDisposeComponent implements Component, Pool.Poolable {
    public ParticlesCreator particles = null;
    @Override
    public void reset() {
        particles = null;
    }
}
