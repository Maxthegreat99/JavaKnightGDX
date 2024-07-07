package com.segfault.games.obj.comp;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.segfault.games.JavaKnight;

/**
 * BouncingComponent, used by BouncingSystem to reverse
 * movingComponent velocity
 */

public class BounceComponent extends Component {
    /**
     * maximum amount of bounces the object performs
     * if the field bounces becomes higher than that
     * the object is removed from the engine
     */
    public int maxBounces = 0;
    /**
     * CollisionRelationship the object must collide with
     * to bounce off of
     */
    public CollisionRelationship relationship = null;
    /**
     * the current amount of bounces performed
     */
    public int bounces = 0;
    @Override
    public void reset() {
        relationship = null;
        bounces = 0;
        maxBounces = 0;
    }

    @Override
    public void dispose(JavaKnight instance) {
        return;
    }

    @Override
    public Component Clone(JavaKnight instance, Entity ent) {
        BounceComponent comp = instance.GetEntityManager().GetEngine().createComponent(this.getClass());
        comp.maxBounces = maxBounces;
        comp.bounces = bounces;
        comp.relationship = relationship;
        return comp;
    }

    @Override
    public void write(Json json) {
        json.writeFields(this);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        maxBounces = jsonData.getInt("maxBounces");
        relationship = CollisionRelationship.valueOf(jsonData.getString("relationship"));
        bounces = jsonData.getInt("bounces");
    }
}
