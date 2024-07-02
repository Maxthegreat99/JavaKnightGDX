package com.segfault.games.obj.comp;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool;
import com.segfault.games.JavaKnight;

public abstract class Component implements com.badlogic.ashley.core.Component, Pool.Poolable {
    public abstract void dispose(JavaKnight instance);
    /**
     * if used on normal enemies (AKA not prototypes) be weary that disposing the parent will
     * destroy objects passed by reference
     */
    public abstract Component Clone(JavaKnight instance, Entity ent);
}
