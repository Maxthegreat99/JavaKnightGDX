package com.segfault.games.obj.comp;

import com.badlogic.ashley.core.Entity;
import com.segfault.games.JavaKnight;

public class MovementInputComponent extends Component {
    public float speed = 0f;

    @Override
    public void reset() {
        speed = 0f;
    }

    @Override
    public void dispose(JavaKnight instance) {

    }

    @Override
    public Component Clone(JavaKnight instance, Entity ent) {
        MovementInputComponent comp = instance.PooledECS.createComponent(this.getClass());
        comp.speed = speed;
        return comp;
    }
}
