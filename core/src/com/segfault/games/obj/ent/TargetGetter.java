package com.segfault.games.obj.ent;

import com.badlogic.ashley.core.Entity;
import com.segfault.games.obj.Rec;

/**
 * holds the targetting logic for entities
 */
public class TargetGetter {
    private final EntityManager manager;
    public TargetGetter(EntityManager entityManager) {
        this.manager = entityManager;
    }

    /**
     * gets the specified target with the specified target method. the class being
     * the component that should be returned from the class
     * @param methodID
     * @param c - can be a component, the entity or special classes like Rec
     * @return
     */
    public Object GetTarget(TargettingMethodID methodID, Class c) {
        switch (methodID) {
            case PLAYER -> {
                if (c.isAssignableFrom(Entity.class)) return manager.GetPlayer();
                else return getEntityComponentOrValue(manager.GetPlayer(), c);
            }
        }

        return null;

    }

    private Object getEntityComponentOrValue(Entity entity, Class c) {
        if (c == Rec.class) return manager.GetMappers().RecOwner.get(entity).rectangle;

        return entity.getComponent(c);
    }

}
