package com.segfault.games.obj.comp;

import com.badlogic.ashley.core.Entity;
import com.segfault.games.JavaKnight;

/**
 * Component used for defining movement in an entity,
 * used by MovementSystem to add velocity to the entity's position.
 * this component expects entities to have the Drawable component
 */
public class MovingComponent extends Component {
    /**
     * velocity x, defines velocity in the x axis
     */
    public float dx = 0.0f;
    /**
     * velocity y, defines velocity in the y axis
     */
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
        MovingComponent comp = instance.GetEntityManager().GetEngine().createComponent(this.getClass());
        comp.dy = dy;
        comp.dx = dx;
        return comp;
    }
}
