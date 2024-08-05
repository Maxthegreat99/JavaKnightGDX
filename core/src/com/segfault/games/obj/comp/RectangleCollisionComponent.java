package com.segfault.games.obj.comp;


import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector4;
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
     * range squared that the target rectangle should be in before checking for collision
     */
    public float checkRange2 = 0f;
    @Override
    public void reset() {
        checkRange2 = 0f;
    }

    @Override
    public void dispose(JavaKnight instance) {

    }

    @Override
    public Component Clone(JavaKnight instance, Entity ent, Vector4 pol, JsonValue properties) {
        RectangleCollisionComponent comp = instance.GetEntityManager().GetEngine().createComponent(this.getClass());
        comp.checkRange2 = checkRange2;
        return comp;
    }

    @Override
    public void write(Json json) {
        json.writeField(checkRange2, "checkRange2");

    }

    @Override
    public void read(JsonValue jsonValue, JavaKnight instance) {
        checkRange2 = jsonValue.getFloat("checkRange2");
    }
}
