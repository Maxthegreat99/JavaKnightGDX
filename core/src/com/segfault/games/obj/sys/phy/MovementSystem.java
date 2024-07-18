package com.segfault.games.obj.sys.phy;

import com.badlogic.ashley.core.Entity;
import com.segfault.games.JavaKnight;
import com.segfault.games.obj.Rec;
import com.segfault.games.obj.comp.*;
import com.segfault.games.obj.ent.EntityManager;

/**
 * movement system, controling entities specifying velocity,
 * this system runs at a constant rate.
 */
public class MovementSystem {
    private final EntityManager manager;

    public MovementSystem(JavaKnight ins) {
        manager = ins.GetEntityManager();

    }

    public void processEntity(Entity entity, float interval) {
        if (entity.isScheduledForRemoval()) return;

        MovingComponent movement = manager.GetMappers().Moving.get(entity);

        if (Float.compare(movement.dx, 0f) == 0 && Float.compare(movement.dy, 0f) == 0) return;
        DrawableComponent drawable = manager.GetMappers().Drawable.get(entity);

        float x = drawable.sprite.getX();
        float y = drawable.sprite.getY();

        // to ensure the movement per update is scaled to the movement per secs
        // we multiply velocity by the interval constant

        float dx = movement.dx * interval;
        float dy = movement.dy * interval;

        float targetX = x + dx;
        float targetY = y + dy;

        boolean hasJBumpCol = manager.GetMappers().Collides.get(entity) != null;


        if (!hasJBumpCol)
            drawable.sprite.setPosition(targetX, targetY);

        // if JBump collision exists we let JBump handle the movement
        else {
            CollidesComponent collisionInfo = manager.GetMappers().Collides.get(entity);
            collisionInfo.res = manager.GetPhysicWorld().move(collisionInfo.physicItem, targetX, targetY, collisionInfo.filter);


            drawable.sprite.setPosition(collisionInfo.res.goalX, collisionInfo.res.goalY);

            collisionInfo.x = collisionInfo.res.goalX;
            collisionInfo.y = collisionInfo.res.goalY;

            dx = collisionInfo.res.goalX - x;
            dy = collisionInfo.res.goalY - y;

        }

        // some entities have both systems so we do not directly return
        // after we handled JBump collision movement
        if (manager.GetMappers().RecOwner.get(entity) == null) return;

        RecOwnerComponent recInfo = manager.GetMappers().RecOwner.get(entity);
        Rec r = recInfo.rectangle;
        r.Move(dx, dy);

    }
}
