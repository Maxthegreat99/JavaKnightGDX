package com.segfault.games.obj.comp;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector4;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.segfault.games.JavaKnight;

/**
 * for bodies that move via acceleration
 */

public class AcceleratedBodyComponent extends Component{

    /**
     * acceleration applied on the x axis
     */
    public float ax = 0f;

    /**
     * acceleration applied on the y axis
     */
    public float ay = 0f;

    @Override
    public void dispose(JavaKnight instance) {

    }

    @Override
    public Component clone(JavaKnight instance, Entity ent, Vector4 pol, JsonValue properties) {

        return instance.GetEntityManager().GetEngine().createComponent(this.getClass());
    }

    @Override
    public void read(JsonValue jsonValue, JavaKnight instance, boolean maploading, Entity ent) {

    }

    @Override
    public void write(Json json) {

    }

    @Override
    public void reset() {
        ax = 0f;
        ay = 0f;
    }
}
