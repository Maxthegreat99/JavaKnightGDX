package com.segfault.games.obj.comp;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Pool;
import com.segfault.games.JavaKnight;

/**
 * a Component, holds data for Systems to manipulate,
 * systems manipulate entities based on which components they have
 */
public abstract class Component implements com.badlogic.ashley.core.Component, Pool.Poolable, Json.Serializable {
    /**
     *  called when EntityLisner.entityRemoved() is called, dispose and remove the references
     *  of your values from the engine (from the Rectangle array / the PhysicWorld for instance) here
     */
    public abstract void dispose(JavaKnight instance);
    /**
     * Clones the component to the passed enemies, the caller must still add the cloned component
     * in most cases, if used on normal enemies (AKA not prototypes) be weary that disposing the parent
     * will destroy objects passed by reference, so please make sure to deep clone each value of your components
     */
    public abstract Component Clone(JavaKnight instance, Entity ent);


}
