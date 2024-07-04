package com.segfault.games.obj.sys;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import com.dongbat.jbump.Response;
import com.segfault.games.JavaKnight;
import com.segfault.games.obj.Rec;
import com.segfault.games.obj.comp.*;

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

        float dx = movement.dx * getInterval();
        float dy = movement.dy * getInterval();

        float targetX = x + dx;
        float targetY = y + dy;

        boolean hasJBumpCol = entity.getComponent(CollidesComponent.class) != null;

        if (!hasJBumpCol)
            drawable.sprite.setPosition(targetX, targetY);

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

        if (entity.getComponent(RecOwnerComponent.class) != null) {
            RecOwnerComponent recInfo = instance.EntityManager.Rm.get(entity);
            Rec r = recInfo.rectangle;
            r.Move(dx, dy);
        }

    }
}
