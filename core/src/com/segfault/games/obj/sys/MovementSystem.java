package com.segfault.games.obj.sys;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import com.dongbat.jbump.Response;
import com.segfault.games.JavaKnight;
import com.segfault.games.obj.Rec;
import com.segfault.games.obj.comp.*;
import com.segfault.games.obj.ent.EntityManager;

/**
 * movement system, controling entities specifying velocity,
 * this system runs at a constant rate.
 */
public class MovementSystem extends IntervalIteratingSystem {
    private final EntityManager manager;

    public MovementSystem(JavaKnight ins, int priority, float interval) {
        super(Family.all(MovingComponent.class, DrawableComponent.class).get(), interval);
        manager = ins.GetEntityManager();
        this.priority = priority;
    }

    @Override
    protected void processEntity(Entity entity) {
        if (entity.isScheduledForRemoval()) return;

        MovingComponent movement = manager.GetMappers().Moving.get(entity);
        DrawableComponent drawable = manager.GetMappers().Drawable.get(entity);

        float x = drawable.sprite.getX();
        float y = drawable.sprite.getY();

        // to ensure the movement per update is scaled to the movement per secs
        // we multiply velocity by the interval constant

        float dx = movement.dx * getInterval();
        float dy = movement.dy * getInterval();

        float targetX = x + dx;
        float targetY = y + dy;

        boolean hasJBumpCol = manager.GetMappers().Collides.get(entity) != null;


        if (!hasJBumpCol)
            drawable.sprite.setPosition(targetX, targetY);

        // if JBump collision exists we let JBump handle the movement
        else {
            CollidesComponent collisionInfo = manager.GetMappers().Collides.get(entity);
            Response.Result res = manager.GetPhysicWorld().move(collisionInfo.physicItem, targetX, targetY, collisionInfo.filter);
            collisionInfo.res = res;

            drawable.sprite.setPosition(res.goalX, res.goalY);

            collisionInfo.x = res.goalX;
            collisionInfo.y = res.goalY;

            dx = res.goalX - x;
            dy = res.goalY - y;
        }

        // some entities have both systems so we do not directly return
        // after we handled JBump collision movement
        if (entity.getComponent(RecOwnerComponent.class) == null) return;

        RecOwnerComponent recInfo = manager.GetMappers().RecOwner.get(entity);
        Rec r = recInfo.rectangle;
        r.Move(dx, dy);

    }
}
