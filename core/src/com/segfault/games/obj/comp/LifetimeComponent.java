package com.segfault.games.obj.comp;

import com.badlogic.ashley.core.Entity;
import com.segfault.games.JavaKnight;

/**
 * lifetime component, defines a time an object stays alive
 * when the LifetimeSystem sees it reach 0 the object is disposed
 */

public class LifetimeComponent extends Component {
    /**
     * current lifetime, in seconds, the object disposes
     * when this reaches 0 or less
     */
    public float lifetime = 0.0f;

    @Override
    public void reset() {
        lifetime = 0.0f;
    }

    @Override
    public void dispose(JavaKnight instance) {

    }

    @Override
    public Component Clone(JavaKnight instance, Entity ent) {
        LifetimeComponent comp = instance.GetEntityManager().GetEngine().createComponent(this.getClass());
        comp.lifetime = lifetime;
        return comp;
    }
}
