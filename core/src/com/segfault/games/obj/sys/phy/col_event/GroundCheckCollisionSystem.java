package com.segfault.games.obj.sys.phy.col_event;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.segfault.games.JavaKnight;
import com.segfault.games.obj.comp.CollidesComponent;
import com.segfault.games.obj.comp.GroundCheckComponent;
import com.segfault.games.obj.ent.EntityManager;

public class GroundCheckCollisionSystem implements CollisionEventSystem{
    private final EntityManager manager;

    public GroundCheckCollisionSystem(JavaKnight ins) {
        manager = ins.GetEntityManager();
    }

    @Override
    public void HandleCollision(Entity entity, Entity target, Contact collision) {
        GroundCheckComponent checkComponent = manager.GetMappers().GroundCheck.get(entity);
        CollidesComponent targetCol = manager.GetMappers().Collides.get(target);

        Vector2 normal = collision.getWorldManifold().getNormal();

        if (normal.y < checkComponent.normalReq)
            return;

        checkComponent.isOnGround = true;
        checkComponent.touchingBodies.add(targetCol);

    }

    @Override
    public void HandleEndCollision(Entity entity, Entity target, Contact collision) {
        GroundCheckComponent checkComponent = manager.GetMappers().GroundCheck.get(entity);
        CollidesComponent targetCol = manager.GetMappers().Collides.get(target);

        if (!checkComponent.touchingBodies.contains(targetCol, true))
            return;

        checkComponent.touchingBodies.removeValue(targetCol, true);

        if (checkComponent.touchingBodies.size == 0)
            checkComponent.isOnGround = false;
    }
}
