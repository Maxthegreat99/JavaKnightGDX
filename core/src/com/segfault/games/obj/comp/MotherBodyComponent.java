package com.segfault.games.obj.comp;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector4;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.segfault.games.JavaKnight;

/**
 * component for physics bodies that other bodies can follow
 */
public class MotherBodyComponent extends Component{
    public String bodyKey = "";

    @Override
    public void dispose(JavaKnight instance) {
        instance.GetEntityManager().GetMotherBodies().remove(bodyKey);
        bodyKey = "";
    }

    @Override
    public Component clone(JavaKnight instance, Entity ent, Vector4 pol, JsonValue properties) {
        MotherBodyComponent comp = instance.GetEntityManager().GetEngine().createComponent(this.getClass());

        comp.bodyKey = bodyKey;

        Body body = instance.GetEntityManager().GetMappers().Collides.get(ent).physicBody;
        instance.GetEntityManager().GetMotherBodies().put(bodyKey, body);

        return comp;

    }

    @Override
    public void read(JsonValue jsonValue, JavaKnight instance, boolean maploading, Entity ent) {
        bodyKey = jsonValue.getString("bodyKey");

        if (!maploading)
            return;

        Body body = instance.GetEntityManager().GetMappers().Collides.get(ent).physicBody;
        instance.GetEntityManager().GetMotherBodies().put(bodyKey, body);
    }

    @Override
    public void write(Json json) {
        json.writeField(bodyKey, "bodyKey");
    }

    @Override
    public void reset() {

    }
}
