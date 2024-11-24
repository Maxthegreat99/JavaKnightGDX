package com.segfault.games.obj.comp;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector4;
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
     * the current amount of bounces performed
     */
    public int bounces = 0;
    @Override
    public void reset() {
        bounces = 0;
        maxBounces = 0;
    }

    @Override
    public void dispose(JavaKnight instance) {
    }

    @Override
    public Component clone(JavaKnight instance, Entity ent, Vector4 pol, JsonValue properties) {
        BounceComponent comp = instance.GetEntityManager().GetEngine().createComponent(this.getClass());
        comp.maxBounces = maxBounces;
        comp.bounces = bounces;
        return comp;
    }

    @Override
    public void write(Json json) {
        json.writeField(maxBounces, "maxBounces");
        json.writeField(bounces, "bounces");
    }

    @Override
    public void read(JsonValue jsonValue, JavaKnight instance) {
        maxBounces = jsonValue.getInt("maxBounces");
        bounces = jsonValue.getInt("bounces");
    }
}
