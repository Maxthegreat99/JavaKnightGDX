package com.segfault.games.obj.comp;


import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector4;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.segfault.games.JavaKnight;

/**
 * Component modifying the Drawable component of an entity
 * to decrease its alpha over time
 */

public class AlphaDecreaseComponent extends Component {
    /**
     * value to decrease the alpha, note this value is multiplied by deltaTime
     */
    public float alphaDecrease = 0.0f;
    /**
     * value to compare for a sense of acceleration in
     * the decrease of the alpha, if component's alpha is
     * greater than this value, its alpha gets decreased by
     * the alphaDecrease x2, if the alpha is more than this value / 2,
     * by x1 and if the alpha is less than that it is decreased by the alpha
     * decreased / 2.
     */
    public float comparator = 0.0f;
    @Override
    public void reset() {
        alphaDecrease = 0.0f;
        comparator = 0.0f;
    }

    @Override
    public void dispose(JavaKnight instance) {

    }

    @Override
    public Component Clone(JavaKnight instance, Entity ent, Vector4 pol, JsonValue properties) {
        AlphaDecreaseComponent comp = instance.GetEntityManager().GetEngine().createComponent(this.getClass());
        comp.alphaDecrease = alphaDecrease;
        comp.comparator = comparator;
        return comp;
    }

    @Override
    public void write(Json json) {
        json.writeFields(this);
    }

    @Override
    public void read(JsonValue jsonValue, JavaKnight instance) {
        alphaDecrease = jsonValue.getFloat("alphaDecrease");
        comparator = jsonValue.getFloat("comparator");
    }
}
