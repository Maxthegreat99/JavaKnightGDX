package com.segfault.games.obj.comp;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector4;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.segfault.games.JavaKnight;

/**
 * lifetime component, defines a time an object stays alive
 * when the LifetimeSystem sees it reach 0 the object is disposed
 */

public class LifetimeComponent extends Component {
    /**
     * current lifetime, in seconds, the object disposes
     * when this reaches 0 or less
     */
    public float lifetime = 0.0f;

    @Override
    public void reset() {
        lifetime = 0.0f;
    }

    @Override
    public void dispose(JavaKnight instance) {

    }

    @Override
    public Component clone(JavaKnight instance, Entity ent, Vector4 pol, JsonValue properties) {
        LifetimeComponent comp = instance.GetEntityManager().GetEngine().createComponent(this.getClass());
        comp.lifetime = lifetime;
        return comp;
    }

    @Override
    public void write(Json json) {
        json.writeFields(this);
    }

    @Override
    public void read(JsonValue jsonValue, JavaKnight instance, boolean maploading, Entity ent) {
        lifetime = jsonValue.getFloat("lifetime");
    }
}
