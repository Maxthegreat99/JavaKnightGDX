package com.segfault.games.obj.comp;


import com.badlogic.ashley.core.Entity;
import com.segfault.games.JavaKnight;

/**
 * TODO: IMPLEMENT POINTING SYSTEM
 * Component to point an entity to a specified drawable,
 * this component expects the entity to contain a drawable component
 */
public class PointingComponent extends Component {
    /**
     * if this is true the entity will target the cursor, ignoring the target
     */
    public boolean cursor = false;
    /**
     * the target that the entity should be pointing to, leave as null
     * if pointing to the cursor
     */
    public DrawableComponent target = null;

    @Override
    public void reset() {
        cursor = false;
        target = null;
    }


    @Override
    public void dispose(JavaKnight instance) {

    }

    @Override
    public Component Clone(JavaKnight instance, Entity ent) {
        PointingComponent comp = instance.GetEntityManager().GetEngine().createComponent(PointingComponent.class);
        comp.cursor = cursor;
        comp.target = target;
        return comp;
    }
}
