package com.segfault.games.obj.comp;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector4;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.segfault.games.JavaKnight;

/**
 * trail component expects the entity to have a drawableComponent,
 * adds a trail which are entities that copies the current state of
 * the entity's sprite and who have the AlphaDecreaseComponent added
 */
public class TrailComponent extends Component {
    /**
     * current cooldown in seconds of when the component
     * should spawn a sprite trail
     */
    public float trailCooldown = 0f;
    /**
     * initial cooldown to reset the current cooldown of the component
     */
    public float trailInitialCooldown = 0f;
    /**
     * the initial alpha a trail should have
     */
    public float trailIninitalAlpha = 0f;
    /**
     * the alpha decrease the trail should have
     */
    public float trailAlphaDecrease = 0f;
    /**
     * the comparator for alpha decrease acceleration of a trail
     */
    public float alphaComparator = 0f;
    @Override
    public void reset() {
        trailCooldown = 0f;
        trailInitialCooldown = 0f;
        trailIninitalAlpha = 0f;
        trailAlphaDecrease = 0f;
        alphaComparator = 0f;
    }

    @Override
    public void dispose(JavaKnight instance) {

    }

    @Override
    public Component clone(JavaKnight instance, Entity ent, Vector4 pol, JsonValue properties) {
        TrailComponent comp = instance.GetEntityManager().GetEngine().createComponent(TrailComponent.class);
        comp.trailCooldown = trailCooldown;
        comp.trailInitialCooldown = trailInitialCooldown;
        comp.trailIninitalAlpha = trailIninitalAlpha;
        comp.trailAlphaDecrease = trailAlphaDecrease;
        comp.alphaComparator = alphaComparator;
        return comp;
    }

    @Override
    public void write(Json json) {
        json.writeFields(this);
    }

    @Override
    public void read(JsonValue jsonValue, JavaKnight instance, boolean maploading, Entity ent) {
        trailCooldown = jsonValue.getFloat("trailCooldown");
        trailInitialCooldown = jsonValue.getFloat("trailInitialCooldown");
        trailIninitalAlpha = jsonValue.getFloat("trailInitialAlpha");
        trailAlphaDecrease = jsonValue.getFloat("trailAlphaDecrease");
        alphaComparator = jsonValue.getFloat("alphaComparator");
    }
}
