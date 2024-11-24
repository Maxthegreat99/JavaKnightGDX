package com.segfault.games.obj.comp;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector4;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.segfault.games.JavaKnight;

public class PlayerGunComponent extends Component {
    public float cooldown = 0.0f;
    public float initialCd = 0.0f;
    public float bulletSpeed = 0.0f;
    @Override
    public void dispose(JavaKnight instance) {

    }

    @Override
    public Component clone(JavaKnight instance, Entity ent, Vector4 pol, JsonValue properties) {
        PlayerGunComponent comp = instance.GetEntityManager().GetEngine().createComponent(this.getClass());

        comp.cooldown = cooldown;
        comp.initialCd = initialCd;
        comp.bulletSpeed = bulletSpeed;
        return comp;
    }

    @Override
    public void read(JsonValue jsonValue, JavaKnight instance) {
        initialCd = jsonValue.getFloat("initialCd");
        bulletSpeed = jsonValue.getFloat("bulletSpeed");
    }

    @Override
    public void write(Json json) {
        json.writeFields(this);
    }

    @Override
    public void reset() {
        cooldown = 0f;
        initialCd = 0f;
        bulletSpeed = 0f;
    }
}
