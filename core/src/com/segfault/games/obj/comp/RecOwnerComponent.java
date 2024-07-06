package com.segfault.games.obj.comp;

import com.badlogic.ashley.core.Entity;
import com.segfault.games.JavaKnight;
import com.segfault.games.obj.Rec;

/**
 * component specifying that the entity owns
 * a rectangle, used for specific events on collisions
 * depending of the components the entity has
 */
public class RecOwnerComponent extends Component {
    /**
     * the rectangle of the entity
     */
    public Rec rectangle = null;
    @Override
    public void reset() {
        rectangle = null;
    }

    @Override
    public void dispose(JavaKnight instance) {
        instance.GetRectangles().removeValue(rectangle, true);
    }

    @Override
    public Component Clone(JavaKnight instance, Entity ent) {
        RecOwnerComponent comp = instance.GetEntityManager().GetEngine().createComponent(RecOwnerComponent.class);
        comp.rectangle = new Rec(rectangle.X, rectangle.Y, rectangle.Width, rectangle.Height);
        if (rectangle.angle != 0) comp.rectangle.Rotate(rectangle.angle, rectangle.OriginX, rectangle.OriginY);
        instance.GetRectangles().add(comp.rectangle);
        return comp;
    }
}
