package com.segfault.games.obj.sys.phy.col_event;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.segfault.games.JavaKnight;
import com.segfault.games.obj.comp.BounceComponent;
import com.segfault.games.obj.comp.MovingComponent;
import com.segfault.games.obj.ent.EntityManager;

/**
 * System controling bouncing entities
 */
public class BouncingCollisionSystem implements CollisionEventSystem {
    private final EntityManager manager;
    public BouncingCollisionSystem(JavaKnight ins) {
        manager = ins.GetEntityManager();

    }

    @Override
    public void HandleCollision(Entity entity, Entity target, Contact collision) {
        BounceComponent BouncingInfo = manager.GetMappers().Bounce.get(entity);

        BouncingInfo.bounces++;

        if (BouncingInfo.bounces > BouncingInfo.maxBounces) {
            manager.GetEngine().removeEntity(entity);
            return;
        }

        MovingComponent MvInf = manager.GetMappers().Moving.get(entity);

        Vector2 normal = collision.getWorldManifold().getNormal();

        // reverse direction based on which side was hit
        if (normal.y != 0)
            MvInf.dy = -MvInf.dy;

        if (normal.x != 0)
            MvInf.dx = -MvInf.dx;
    }
}
