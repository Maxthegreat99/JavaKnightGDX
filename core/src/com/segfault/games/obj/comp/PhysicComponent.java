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
    public Array<SubSystem> physicSystems = new Array<>();
    /**
     * String array copy referencing the systems used
     */
    public Array<String> stringCopySystems = new Array<>();

    @Override
    public void reset() {
        physicSystems.clear();
        stringCopySystems.clear();
    }

    @Override
    public void dispose(JavaKnight instance) {
        return;
    }

    @Override
    public Component Clone(JavaKnight instance, Entity ent, Vector4 pol, JsonValue properties) {
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
    public void read(JsonValue jsonValue, JavaKnight instance) {
        for (int i = 0; i < jsonValue.get("physicSystems").size; i++) {
            String name = jsonValue.get("physicSystems").getString(i);

            stringCopySystems.add(name);
        }

        stringCopySystems.sort((o1, o2) -> Integer.compare(indexEntitySystems.valueOf(o1).ordinal(), indexEntitySystems.valueOf(o1).ordinal()));

        for (String s : stringCopySystems) physicSystems.add((SubSystem) instance.GetEntityManager().GetSystems().get(indexEntitySystems.valueOf(s)));
    }
}
