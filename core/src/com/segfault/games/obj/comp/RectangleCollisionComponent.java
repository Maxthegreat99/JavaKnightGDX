package com.segfault.games.obj.comp;


import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.segfault.games.JavaKnight;
import com.segfault.games.obj.Rec;
import com.segfault.games.obj.ent.TargettingMethodID;

/**
 * component defining that the entity's rectangle may
 * collide with a target rectangle for specific events
 */
public class RectangleCollisionComponent extends Component {
    /**
     * the target rectangle triggering the event
     */
    public Rec targetRectangle = null;
    /**
     * range squared that the target rectangle should be in before checking for collision
     */
    public float checkRange2 = 0f;

    /**
     * Method id to get the target rectangle
     */
    public TargettingMethodID targetMethod = null;
    @Override
    public void reset() {
        targetRectangle = null;
        checkRange2 = 0f;
        targetMethod = null;
    }

    @Override
    public void dispose(JavaKnight instance) {

    }

    @Override
    public Component Clone(JavaKnight instance, Entity ent) {
        RectangleCollisionComponent comp = instance.GetEntityManager().GetEngine().createComponent(this.getClass());
        comp.checkRange2 = checkRange2;
        comp.targetRectangle = (Rec) instance.GetEntityManager().GetTargetGetter().GetTarget(targetMethod, Rec.class);
        comp.targetMethod = targetMethod;
        return comp;
    }

    @Override
    public void write(Json json) {
        json.writeField(checkRange2, "checkRange2");
        json.writeField(targetMethod.toString(), "targetMethod");

    }

    @Override
    public void read(JsonValue jsonValue, JavaKnight instance) {
        checkRange2 = jsonValue.getFloat("checkRange2");
        targetMethod = TargettingMethodID.valueOf(jsonValue.getString("targetMethod"));
    }
}
