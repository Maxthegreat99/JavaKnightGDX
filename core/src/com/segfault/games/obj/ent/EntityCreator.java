package com.segfault.games.obj.ent;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector4;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.segfault.games.JavaKnight;
import com.segfault.games.obj.comp.Component;

/**
 * Handles spawning entities and registering protorypes to spawn
 */
public class EntityCreator {
    private final JavaKnight instance;

    private final ObjectMap<EntityID, Entity> prototypes = new ObjectMap<>();
    public EntityCreator(JavaKnight ins) {
        instance = ins;
    }

    /**
     * adds a prototype to the prototype set, to spawn the entity use spawnEntity with the same ID
     * @param prototype - the prototype to clone at spawn
     * @param id - id of the prototype
     */
    public void AddPrototype(Entity prototype, EntityID id) {
        prototypes.put(id, prototype);
    }

    /**
     * clones the components of the specified prototype in
     * a new entity and adds the entity to the ECS engine
     * @param id - id of the prototype
     * @return - the cloned entity
     */
    public Entity SpawnEntity(EntityID id, boolean addToEngine, Vector4 pol, JsonValue properties) {
        Entity e = instance.GetEntityManager().GetEngine().createEntity();
        for (com.badlogic.ashley.core.Component c : prototypes.get(id).getComponents()) {
            Component comp = (Component) c;

            e.add(comp.Clone(instance, e, pol, properties));
        }

        if (addToEngine)
            instance.GetEntityManager().GetEngine().addEntity(e);

        return e;
    }

}
