package com.segfault.games.obj.comp;


import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector4;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.segfault.games.JavaKnight;
import com.segfault.games.obj.ent.indexEntitySystems;
import com.segfault.games.obj.sys.SubSystem;

import java.util.Comparator;

/**
 * Defines the physic systems an entity is tied to
 * for the physic system to execute
 */
public class PhysicComponent extends Component {

    /**
     * Subsystems the engine needs to execute for the entity
     */
    public final Array<SubSystem> physicSystems = new Array<>();

    /**
     * String array copy referencing the systems used
     */
    public final Array<String> stringCopySystems = new Array<>();

    @Override
    public void reset() {
        physicSystems.clear();
        stringCopySystems.clear();
    }

    @Override
    public void dispose(JavaKnight instance) {
    }

    @Override
    public Component clone(JavaKnight instance, Entity ent, Vector4 pol, JsonValue properties) {
        PhysicComponent comp = instance.GetEntityManager().GetEngine().createComponent(this.getClass());
        comp.physicSystems.addAll(physicSystems);
        comp.stringCopySystems.addAll(stringCopySystems);
        return comp;
    }

    @Override
    public void write(Json json) {
        json.writeArrayStart("physicSystems");

        for (int i = 0; i < stringCopySystems.size; i++) json.writeValue(stringCopySystems.get(i));

        json.writeArrayEnd();
    }

    @Override
    public void read(JsonValue jsonValue, JavaKnight instance, boolean maploading, Entity ent) {
        for (int i = 0; i < jsonValue.get("physicSystems").size; i++) {
            String name = jsonValue.get("physicSystems").getString(i);

            stringCopySystems.add(name);
        }

        stringCopySystems.sort(Comparator.comparingInt(o -> indexEntitySystems.valueOf(o).ordinal()));

        for (String s : stringCopySystems) physicSystems.add((SubSystem) instance.GetEntityManager().GetSystems().get(indexEntitySystems.valueOf(s)));

    }
}
