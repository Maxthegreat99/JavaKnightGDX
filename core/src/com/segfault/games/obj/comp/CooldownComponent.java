package com.segfault.games.obj.comp;


import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector4;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.segfault.games.JavaKnight;

/**
 * Component for reducing set cooldowns over time
 */

public class CooldownComponent extends Component {
    /**
     * Current cooldown value in seconds, reduces over time
     * and resets itself after it hits 0, if this value
     * is less or equal two zero your event should activate.
     */
    public float cd = 0.0f;
    /**
     * The value to reset the cooldown with once it is done, in seconds
     */
    public float initialCd = 0.0f;
    /**
     * if this boolean is true, the cooldown will keep going
     * and reseting no matter of the activate boolean
     */
    public boolean automated = false;
    /**
     * if the automated boolean is false, this value determines
     * if the cooldown keeps going.
     */

    public boolean activate = false;
    @Override
    public void reset() {
        cd = 0.0f;
        initialCd = 0.0f;
        activate = false;
        automated = false;
    }

    @Override
    public void dispose(JavaKnight instance) {

    }

    @Override
    public Component Clone(JavaKnight instance, Entity ent, Vector4 pol, JsonValue properties) {
        CooldownComponent comp = instance.GetEntityManager().GetEngine().createComponent(this.getClass());
        comp.cd = cd;
        comp.initialCd = initialCd;
        comp.automated = automated;
        comp.activate = activate;
        return comp;
    }

    @Override
    public void write(Json json) {
        json.writeFields(this);
    }

    @Override
    public void read(JsonValue jsonValue, JavaKnight instance) {
        cd = jsonValue.getFloat("cd");
        initialCd = jsonValue.getFloat("initialCd");
        automated = jsonValue.getBoolean("automated");
        activate = jsonValue.getBoolean("activate");
    }
}
