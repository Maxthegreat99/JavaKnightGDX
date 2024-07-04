package com.segfault.games.obj.comp;

import com.badlogic.ashley.core.Entity;
import com.segfault.games.JavaKnight;

/**
 * component used to modify the movingComponent
 * based on player input, the component  expects
 * the entity to have a movingComponent
 */
public class MovementInputComponent extends Component {

    /**
     * the squared speed that the entity should
     * move at based on player input.
     */
    public float speed2 = 0f;

    @Override
    public void reset() {
        speed2 = 0f;
    }

    @Override
    public void dispose(JavaKnight instance) {

    }

    @Override
    public Component Clone(JavaKnight instance, Entity ent) {
        MovementInputComponent comp = instance.PooledECS.createComponent(this.getClass());
        comp.speed2 = speed2;
        return comp;
    }
}
