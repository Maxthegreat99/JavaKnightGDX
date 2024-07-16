package com.segfault.games.obj.comp;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.segfault.games.JavaKnight;

/**
 * life component holds life for systems to handle
 * when it takes damages and when its life reaches 0
 */
public class LifeComponent extends Component {
    /**
     * life value in int
     */
    public int life = 0;
    /**
     * whether the component took damage during this update
     * when handled this value should then be set to false
     * again
     */
    public boolean tookHit = false;
    @Override
    public void reset() {
        life = 0;
        tookHit = false;
    }


    @Override
    public void dispose(JavaKnight instance) {

    }

    @Override
    public Component Clone(JavaKnight instance, Entity ent) {
        LifeComponent comp = instance.GetEntityManager().GetEngine().createComponent(this.getClass());
        comp.tookHit = tookHit;
        comp.life = life;
        return comp;
    }

    @Override
    public void write(Json json) {
        json.writeFields(this);
    }

    @Override
    public void read(JsonValue jsonValue, JavaKnight instance) {
        life = jsonValue.getInt("life");
        tookHit = jsonValue.getBoolean("tookHit");

    }
}
