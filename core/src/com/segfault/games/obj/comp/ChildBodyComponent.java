package com.segfault.games.obj.comp;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector4;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.segfault.games.JavaKnight;

/**
 * Child body that follows a mother body
 */
public class ChildBodyComponent extends Component{

    public String motherBodyKey = "";

    @Override
    public void dispose(JavaKnight instance) {

    }

    @Override
    public Component clone(JavaKnight instance, Entity ent, Vector4 pol, JsonValue properties) {
        ChildBodyComponent comp = instance.GetEntityManager().GetEngine().createComponent(this.getClass());

        comp.motherBodyKey = motherBodyKey;

        return comp;
    }

    @Override
    public void read(JsonValue jsonValue, JavaKnight instance, boolean maploading, Entity ent) {
        motherBodyKey = jsonValue.getString("motherBodyKey");
    }

    @Override
    public void write(Json json) {
        json.writeField(motherBodyKey, "motherBodyKey");
    }

    @Override
    public void reset() {
        motherBodyKey = "";
    }
}
