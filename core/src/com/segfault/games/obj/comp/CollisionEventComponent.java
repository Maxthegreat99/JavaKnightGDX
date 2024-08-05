package com.segfault.games.obj.comp;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector4;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.segfault.games.JavaKnight;
import com.segfault.games.obj.Rec;
import com.segfault.games.obj.ent.indexEntitySystems;
import com.segfault.games.obj.sys.SubSystem;

/**
 * Component to describe interactions with different collidable entities
 */
public class CollisionEventComponent extends Component {

    public ObjectMap<CollisionRelationship, Array<SubSystem>> collisionEvents = new ObjectMap<>();
    public Array<String> stringCopyEvents = new Array<>();
    public ObjectMap<Rec, Array<SubSystem>> recCollisionEvents = new ObjectMap<>();

    public Array<Rec> rectangle
    @Override
    public void reset() {
        collisionEvents.clear();
        stringCopyEvents.clear();
    }

    @Override
    public void dispose(JavaKnight instance) {
        return;
    }

    @Override
    public Component Clone(JavaKnight instance, Entity ent, Vector4 pol, JsonValue properties) {
        CollisionEventComponent comp = instance.GetEntityManager().GetEngine().createComponent(this.getClass());
        comp.collisionEvents.putAll(collisionEvents);
        return comp;
    }

    @Override
    public void write(Json json) {
        json.writeArrayStart("physicSystems");

        for (int i = 0; i < stringCopyEvents.size; i++) json.writeValue(stringCopyEvents.get(i));

        json.writeArrayEnd();
    }

    private String key = null;
    private final Array<SubSystem> events = new Array<>();

    /**
     * keys and values are read like this
     * [
     *  KEY1,
     *  VALUE1,
     *  VALUE2,
     *  KEY2,
     *  VALUE1,
     *  VALUE2,
     *  VALUE3,
     *  ...
     *  ]
     */
    @Override
    public void read(JsonValue jsonValue, JavaKnight instance) {
        for (int i = 0; i < jsonValue.get("collisionEvents").size; i++) {
            String name = jsonValue.get("collisionEvents").getString(i);
            stringCopyEvents.add(name);

            boolean isKey = false;

            try {
                CollisionRelationship.valueOf(name);

                isKey = true;
            }
            catch (Exception ignored) {  }

            if (isKey) {
                if (key != null) {
                    collisionEvents.put(CollisionRelationship.valueOf(key), events);
                    events.clear();
                }

                key = name;
                continue;
            }

            events.add((SubSystem) instance.GetEntityManager().GetSystems().get(indexEntitySystems.valueOf(name)));

        }

        collisionEvents.put(CollisionRelationship.valueOf(key), events);
    }

}