package com.segfault.games.obj.comp;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector4;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.segfault.games.JavaKnight;
import com.segfault.games.util.indexT;

/**
 * for objects that need to draw a normal map
 */
public class NormalComponent extends Component{
    /**
     * texture region ID
     */
    public indexT texID = null;

    @Override
    public void dispose(JavaKnight instance) {

    }

    @Override
    public Component clone(JavaKnight instance, Entity ent, Vector4 pol, JsonValue properties) {
        NormalComponent comp = instance.GetEntityManager().GetEngine().createComponent(this.getClass());
        comp.texID = texID;

        return comp;
    }

    @Override
    public void read(JsonValue jsonValue, JavaKnight instance, boolean maploading, Entity ent) {
        texID = indexT.valueOf(jsonValue.getString("texID"));
    }

    @Override
    public void write(Json json) {
        json.writeField(texID.toString(), "texID");
    }

    @Override
    public void reset() {
        texID = null;
    }
}
