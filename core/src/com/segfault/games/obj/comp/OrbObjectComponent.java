package com.segfault.games.obj.comp;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector4;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.segfault.games.JavaKnight;

/**
 * component for the orb the player can hit to regain their dash
 */
public class OrbObjectComponent extends Component{

    /**
     *  cooldown to activate once hit
     */
    public float cooldown = 0f;

    /**
     *  elapsed cooldown once hit
     */
    public float elapsedCooldown = 0f;

    /**
     * whether the orb has been hit
     */
    public boolean hasBeenHit = false;

    @Override
    public void dispose(JavaKnight instance) {

    }

    @Override
    public Component clone(JavaKnight instance, Entity ent, Vector4 pol, JsonValue properties) {
        return null;
    }

    @Override
    public void read(JsonValue jsonValue, JavaKnight instance, boolean maploading, Entity ent) {

        cooldown = jsonValue.getFloat("cooldown");
    }

    @Override
    public void write(Json json) {

    }

    @Override
    public void reset() {
        hasBeenHit = false;
        elapsedCooldown = 0f;
        cooldown = 0f;
    }
}
