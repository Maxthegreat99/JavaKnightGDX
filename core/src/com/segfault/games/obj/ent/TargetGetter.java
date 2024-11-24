package com.segfault.games.obj.ent;

import com.badlogic.ashley.core.Entity;

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
     * @param c - the component from the target
     * @return
     */
    public Object GetTarget(TargettingMethodID methodID, Class c) {
        switch (methodID) {
            case PLAYER :
                if (c.isAssignableFrom(Entity.class)) return manager.GetPlayer();
                else return manager.GetPlayer().getComponent(c);

            case NULL :
                return null;

        }

        return null;

    }

}
