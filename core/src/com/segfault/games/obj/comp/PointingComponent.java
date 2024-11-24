package com.segfault.games.obj.comp;


import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector4;
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
    /**
     * radius at which the object should be rotating
     */
    public float rotatingRadius = 0;
    /**
     * method to get the base around which the rotate
     */
    public TargettingMethodID baseTargetMethod = null;

    /**
     * base around which to rotate
     */
    public DrawableComponent base = null;

    /**
     * whether the pointing object is currently flipped
     */
    public boolean flip = false;

    /**
     * current pointing angle
     */
    public float angle = 0f;

    @Override
    public void reset() {
        cursor = false;
        target = null;
        targetMethod = null;
        base = null;
        baseTargetMethod = null;
        rotatingRadius = 0f;
        flip = false;
        angle = 0f;
    }


    @Override
    public void dispose(JavaKnight instance) {

    }

    @Override
    public Component clone(JavaKnight instance, Entity ent, Vector4 pol, JsonValue properties) {
        PointingComponent comp = instance.GetEntityManager().GetEngine().createComponent(PointingComponent.class);
        comp.cursor = cursor;
        if (targetMethod != null)
            comp.target = (DrawableComponent) instance.GetEntityManager().GetTargetGetter().GetTarget(targetMethod, DrawableComponent.class);
        comp.targetMethod = targetMethod;
        comp.base = (DrawableComponent) instance.GetEntityManager().GetTargetGetter().GetTarget(baseTargetMethod, DrawableComponent.class);
        comp.baseTargetMethod = baseTargetMethod;
        comp.rotatingRadius = rotatingRadius;
        comp.flip = flip;
        comp.angle = angle;
        return comp;
    }

    @Override
    public void write(Json json) {
        json.writeField(cursor, "cursor");
        json.writeField(targetMethod.toString(), "targetMethod");
        json.writeField(rotatingRadius, "rotatingRadius");
        json.writeField(baseTargetMethod.toString(), "baseTargetMethod");
    }

    @Override
    public void read(JsonValue jsonValue, JavaKnight instance) {
        cursor = jsonValue.getBoolean("cursor");
        if (jsonValue.has("targetMethod"))
            targetMethod = TargettingMethodID.valueOf(jsonValue.getString("targetMethod"));
        baseTargetMethod = TargettingMethodID.valueOf(jsonValue.getString("baseTargetMethod"));
        rotatingRadius = jsonValue.getFloat("rotatingRadius");

    }
}
