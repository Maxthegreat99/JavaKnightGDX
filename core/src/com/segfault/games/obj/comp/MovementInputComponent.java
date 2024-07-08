package com.segfault.games.obj.comp;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.segfault.games.JavaKnight;

/**
 * component used to modify the movingComponent
 * based on player input, the component  expects
 * the entity to have a movingComponent
 */
public class MovementInputComponent extends Component {

    /**
     * the squared speed that the entity should
     * move at based on player input.
     */
    public float speed2 = 0f;

    @Override
    public void reset() {
        speed2 = 0f;
    }

    @Override
    public void dispose(JavaKnight instance) {

    }

    @Override
    public Component Clone(JavaKnight instance, Entity ent) {
        MovementInputComponent comp = instance.GetEntityManager().GetEngine().createComponent(this.getClass());
        comp.speed2 = speed2;
        return comp;
    }

    @Override
    public void write(Json json) {
        json.writeFields(this);
    }

    @Override
    public void read(Json json, JsonValue jsonValue) {
        speed2 = jsonValue.getFloat("speed2");
    }
}
