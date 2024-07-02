package com.segfault.games.obj.comp;

import com.badlogic.ashley.core.Entity;
import com.segfault.games.JavaKnight;

public class MovingComponent extends Component {
    public float dx = 0.0f;
    public float dy = 0.0f;
    @Override
    public void reset() {
        dx = 0.0f;
        dy = 0.0f;
    }

    @Override
    public void dispose(JavaKnight instance) {

    }

    @Override
    public Component Clone(JavaKnight instance, Entity ent) {
        MovingComponent comp = instance.PooledECS.createComponent(this.getClass());
        comp.dy = dy;
        comp.dx = dx;
        return comp;
    }
}
