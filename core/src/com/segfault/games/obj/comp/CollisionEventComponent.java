package com.segfault.games.obj.comp;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector4;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.segfault.games.JavaKnight;

/**
 * Component to describe interactions with different colidable entities
 */
public class CollisionEventComponent extends Component {

    // <entity type, bitflag of events to happen>
    public final ObjectMap<CollisionRelationship, Long> collisionEvents = new ObjectMap<>();
    public final Array<String> stringCopyEvents = new Array<>();

    @Override
    public void reset() {
        collisionEvents.clear();
        stringCopyEvents.clear();
    }

    @Override
    public void dispose(JavaKnight instance) {

    }

    @Override
    public Component clone(JavaKnight instance, Entity ent, Vector4 pol, JsonValue properties) {
        CollisionEventComponent comp = instance.GetEntityManager().GetEngine().createComponent(this.getClass());
        comp.collisionEvents.putAll(collisionEvents);
        comp.stringCopyEvents.addAll(stringCopyEvents);
        return comp;
    }

    @Override
    public void write(Json json) {
        json.writeArrayStart("collisionEvents");

        for (int i = 0; i < stringCopyEvents.size; i++) json.writeValue(stringCopyEvents.get(i));

        json.writeArrayEnd();
    }

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
        long events = 0L;
        String key = null;

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
                    events = 0L;
                }

                key = name;
                continue;
            }

            events = events | CollisionEvents.valueOf(name).flag;

        }

        collisionEvents.put(CollisionRelationship.valueOf(key), events);
    }

}