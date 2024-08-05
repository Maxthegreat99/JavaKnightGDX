package com.segfault.games.obj.comp;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector4;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.segfault.games.JavaKnight;
import com.segfault.games.obj.Rec;
import com.segfault.games.obj.ent.ParticlesCreator;
import com.segfault.games.obj.ent.TargettingMethodID;

/**
 * component disposing the object whenever they hit
 * the specified collision
 */

public class DisposeOnCollisionComponent extends Component {
    /**
     * TODO : IMPLEMENT PARTICLES
     * particle creator
     */
    public ParticlesCreator particles = null;
    /**
     * CollisionRelationships needed to be collided with to dispose
     * the object, kept null if using a rectangle
     */
    public Array<CollisionRelationship> relationships = new Array<>();
    /**
     * Rectangle to collide with to dispose the object,
     * kept null if using JBump collision
     */
    public Rec rectangle = null;
    /**
     * way to get the rectangle component
     */
    public TargettingMethodID targetMethod = null;

    /**
     * set this value if using a rectangle,
     * the range to the square to check
     * if the specified rectangle is colliding.
     */
    public float checkRange2 = 0f;

    @Override
    public void reset() {
        particles = null;
        rectangle = null;
        relationships.clear();
        targetMethod = null;
        checkRange2 = 0f;
    }

    @Override
    public void dispose(JavaKnight instance) {

    }

    @Override
    public Component Clone(JavaKnight instance, Entity ent, Vector4 pol, JsonValue properties) {
        DisposeOnCollisionComponent comp = instance.GetEntityManager().GetEngine().createComponent(this.getClass());
        comp.particles = particles;
        comp.checkRange2 = checkRange2;
        comp.relationships.addAll(relationships);
        comp.targetMethod = targetMethod;
        if (relationships == null)
            comp.rectangle = (Rec) instance.GetEntityManager().GetTargetGetter().GetTarget(targetMethod, Rec.class);
        return comp;
    }

    @Override
    public void write(Json json) {
        json.writeField(checkRange2, "checkRange2");
        if (relationships != null) {
            json.writeArrayStart("relationships");

            for (int i = 0; i < relationships.size; i++) json.writeValue(relationships.get(i).toString());

            json.writeArrayEnd();
        }
        else json.writeField(targetMethod.toString(), "targetMethod");

    }

    @Override
    public void read(JsonValue jsonValue, JavaKnight instance) {

        if (jsonValue.has("relationships")) {

            for (int i = 0; i < jsonValue.get("relationships").size; i++)
                relationships.add( CollisionRelationship.valueOf(jsonValue.get("relationships").getString(i)));
        }
        else {
            targetMethod = TargettingMethodID.valueOf(jsonValue.getString("targetMethod"));
            checkRange2 = jsonValue.getFloat("checkRange2");
        }

    }

}
