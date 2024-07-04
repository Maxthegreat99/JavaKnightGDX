package com.segfault.games.obj.sys;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import com.dongbat.jbump.Response;
import com.segfault.games.JavaKnight;
import com.segfault.games.obj.Rec;
import com.segfault.games.obj.comp.*;

/**
 * movement system, controling entities specifying velocity,
 * this system runs at a constant rate.
 */
public class MovementSystem extends IntervalIteratingSystem {
    private final JavaKnight instance;

    public MovementSystem(JavaKnight ins, int priority, float interval) {
        super(Family.all(MovingComponent.class, DrawableComponent.class).get(), interval);
        instance = ins;
        this.priority = priority;
    }

    @Override
    protected void processEntity(Entity entity) {
        if (entity.isScheduledForRemoval()) return;

        MovingComponent movement = instance.EntityManager.Mm.get(entity);
        DrawableComponent drawable = instance.EntityManager.Dm.get(entity);

        float x = drawable.sprite.getX();
        float y = drawable.sprite.getY();

        // to ensure the movement per update is scaled to the movement per secs
        // we multiply velocity by the interval constant

        float dx = movement.dx * getInterval();
        float dy = movement.dy * getInterval();

        float targetX = x + dx;
        float targetY = y + dy;

        boolean hasJBumpCol = instance.EntityManager.Cm.get(entity) != null;


        if (!hasJBumpCol)
            drawable.sprite.setPosition(targetX, targetY);

        // if JBump collision exists we let JBump handle the movement
        else {
            CollidesComponent collisionInfo = instance.EntityManager.Cm.get(entity);
            Response.Result res = instance.PhysicWorld.move(collisionInfo.physicItem, targetX, targetY, collisionInfo.filter);
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

        RecOwnerComponent recInfo = instance.EntityManager.Rm.get(entity);
        Rec r = recInfo.rectangle;
        r.Move(dx, dy);

    }
}
