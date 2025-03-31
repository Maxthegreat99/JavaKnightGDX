package com.segfault.games.obj.sys.phy.col_event;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Contact;
import com.segfault.games.JavaKnight;
import com.segfault.games.obj.ent.EntityManager;

/**
 * System disposing entities on specific collisions
 */
public class DisposeCollisionSystem implements CollisionEventSystem {
    private final EntityManager manager;

    public DisposeCollisionSystem(JavaKnight ins) {
        manager = ins.GetEntityManager();
    }

    @Override
    public void HandleCollision(Entity entity, Entity target, Contact collision) {
        manager.GetEngine().removeEntity(entity);
    }

    @Override
    public void HandleEndCollision(Entity entity, Entity target, Contact collision) {
        return;
    }
}
