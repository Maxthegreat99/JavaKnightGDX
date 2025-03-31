package com.segfault.games.obj.comp;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector4;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.segfault.games.JavaKnight;

public class GroundCheckComponent extends Component{
    /**
     * whether the body is currently touching what it considers ground
     */
    public boolean isOnGround = false;

    /**
     * arrays of bodies that the body considers ground and is coliding with
     */
    public Array<CollidesComponent> touchingBodies = new Array<>();

    /**
     * normal value to be able to consider a collision as ground
     */
    public float normalReq = 0;




    @Override
    public void dispose(JavaKnight instance) {

    }

    @Override
    public Component clone(JavaKnight instance, Entity ent, Vector4 pol, JsonValue properties) {
        GroundCheckComponent comp = instance.GetEntityManager().GetEngine().createComponent(this.getClass());

        comp.normalReq = normalReq;

        return comp;

    }

    @Override
    public void read(JsonValue jsonValue, JavaKnight instance, boolean maploading, Entity ent) {
        normalReq = jsonValue.getFloat("normalReq");


    }

    @Override
    public void write(Json json) {

        json.writeField(normalReq, "normalReq");
    }

    @Override
    public void reset() {
        touchingBodies.clear();
        isOnGround = false;
        normalReq = 0;
    }
}
