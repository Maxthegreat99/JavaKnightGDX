package com.segfault.games.obj.comp;


import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector4;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.segfault.games.JavaKnight;

/**
 * Component defining deceleration in an entity, expects the entity
 * to have a moving component
 */
public class SpeedDecreaseComponent extends Component {
    /**
     * the deceleration value, this value is multiplied by deltatime
     */
    public float decelerationValue = 0.0f;
    /**
     * comparator squared, if the speed of the entity is more than it, the entity will
     * decelerate twice as fast, if more than half of it, it will decelerate normally, less
     * than half and it will decelerate twice as slow
     */
    public float comparator2 = 0f;
    /**
     * speed squared at which the entity should stop
     */
    public float stopSpeed2 = 0f;
    @Override
    public void reset() {
        decelerationValue = 0.0f;
        comparator2 = 0.0f;
        stopSpeed2 = 0f;
    }

    @Override
    public void dispose(JavaKnight instance) {

    }

    @Override
    public Component clone(JavaKnight instance, Entity ent, Vector4 pol, JsonValue properties) {
        SpeedDecreaseComponent comp = instance.GetEntityManager().GetEngine().createComponent(this.getClass());
        comp.comparator2 = comparator2;
        comp.decelerationValue = decelerationValue;
        comp.stopSpeed2 = stopSpeed2;
        return comp;
    }

    @Override
    public void write(Json json) {
        json.writeFields(this);
    }

    @Override
    public void read(JsonValue jsonValue, JavaKnight instance, boolean maploading, Entity ent) {
        decelerationValue = jsonValue.getFloat("decelerationValue");
        comparator2 = jsonValue.getFloat("comparator2");
        stopSpeed2 = jsonValue.getFloat("stopSpeed2");
    }
}
