package com.segfault.games.obj.comp;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector4;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
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
    public Rec rectangle = new Rec(0,0,0,0);
    /**
     * if the component has an event component, used to replace hashmap lookups for useless checks
     */
    public boolean hasEventComponent = false;
    @Override
    public void reset() {
        hasEventComponent = false;
    }

    @Override
    public void dispose(JavaKnight instance) {
        instance.GetRectangles().removeValue(rectangle, true);
    }

    @Override
    public Component Clone(JavaKnight instance, Entity ent, Vector4 pol, JsonValue properties) {
        RecOwnerComponent comp = instance.GetEntityManager().GetEngine().createComponent(RecOwnerComponent.class);
        comp.rectangle.set(pol.x + pol.z / 2, pol.y + pol.w / 2, pol.z, pol.w);
        if (rectangle != null && rectangle.angle != 0) comp.rectangle.Rotate(rectangle.angle, rectangle.OriginX, rectangle.OriginY);
        instance.GetRectangles().add(comp.rectangle);
        comp.hasEventComponent = hasEventComponent;
        return comp;
    }

    @Override
    public void write(Json json) {
    }
    @Override
    public void read(JsonValue jsonValue, JavaKnight instance) {
        hasEventComponent = jsonValue.getBoolean("hasEventComponent");
    }

}
