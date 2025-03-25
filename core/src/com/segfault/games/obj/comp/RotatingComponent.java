package com.segfault.games.obj.comp;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector4;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.segfault.games.JavaKnight;

/**
 * component for physic objects with bodies to give them a constant rotation
 */

public class RotatingComponent extends Component{

    /**
     * angular speed of the object
     */
    public float rotatingSpeed = 0f;

    @Override
    public void dispose(JavaKnight instance) {

    }

    @Override
    public Component clone(JavaKnight instance, Entity ent, Vector4 pol, JsonValue properties) {
        RotatingComponent comp = instance.GetEntityManager().GetEngine().createComponent(this.getClass());
        comp.rotatingSpeed = rotatingSpeed;

        return comp;
    }

    @Override
    public void read(JsonValue jsonValue, JavaKnight instance, boolean maploading, Entity ent) {
        rotatingSpeed = jsonValue.getFloat("rotatingSpeed");
    }

    @Override
    public void write(Json json) {
        json.writeField(rotatingSpeed, "rotatingSpeed");
    }

    @Override
    public void reset() {
        rotatingSpeed = 0f;
    }
}
