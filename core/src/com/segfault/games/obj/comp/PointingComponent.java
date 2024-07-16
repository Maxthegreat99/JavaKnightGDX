package com.segfault.games.obj.comp;


import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.segfault.games.JavaKnight;
import com.segfault.games.obj.ent.TargettingMethodID;

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
    /**
     * method id to get the target to point
     */
    public TargettingMethodID targetMethod = null;
    @Override
    public void reset() {
        cursor = false;
        target = null;
        targetMethod = null;
    }


    @Override
    public void dispose(JavaKnight instance) {

    }

    @Override
    public Component Clone(JavaKnight instance, Entity ent) {
        PointingComponent comp = instance.GetEntityManager().GetEngine().createComponent(PointingComponent.class);
        comp.cursor = cursor;
        comp.target = (DrawableComponent) instance.GetEntityManager().GetTargetGetter().GetTarget(targetMethod, DrawableComponent.class);
        comp.targetMethod = targetMethod;
        return comp;
    }

    @Override
    public void write(Json json) {
        json.writeField(cursor, "cursor");
        json.writeField(targetMethod.toString(), "targetMethod");
    }

    @Override
    public void read(JsonValue jsonValue, JavaKnight instance) {
        cursor = jsonValue.getBoolean("cursor");
        targetMethod = TargettingMethodID.valueOf(jsonValue.getString("targetMethod"));
    }
}
