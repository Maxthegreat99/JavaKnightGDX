package com.segfault.games.obj.comp;

import com.badlogic.ashley.core.Entity;
import com.segfault.games.JavaKnight;

public class LifetimeComponent extends Component {
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
        LifetimeComponent comp = instance.PooledECS.createComponent(this.getClass());
        comp.lifetime = lifetime;
        return comp;
    }
}
