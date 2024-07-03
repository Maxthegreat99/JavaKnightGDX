package com.segfault.games.obj.ent;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.ObjectMap;
import com.segfault.games.JavaKnight;
import com.segfault.games.obj.comp.PrototypeComp;

public class EntityCreator {
    private final JavaKnight instance;
    private final ObjectMap<EntityID, Entity> prototypes = new ObjectMap<>();
    public EntityCreator(JavaKnight ins) {
        instance = ins;
    }

    public void addPrototype(Entity prototype, EntityID id) {
        prototypes.put(id, prototype);
    }

    public Entity spawnEntity(EntityID id) {
        Entity e = instance.PooledECS.createEntity();
        for (Component c : prototypes.get(id).getComponents()) {
            com.segfault.games.obj.comp.Component comp = (com.segfault.games.obj.comp.Component) (c);
            if (comp.getClass() == PrototypeComp.class) continue;

            e.add(comp.Clone(instance, e));
        }
        instance.PooledECS.addEntity(e);

        return e;
    }

}
