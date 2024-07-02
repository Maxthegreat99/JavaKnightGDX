package com.segfault.games.obj.sys;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import com.badlogic.ashley.systems.IteratingSystem;
import com.dongbat.jbump.Response;
import com.segfault.games.JavaKnight;
import com.segfault.games.obj.Rec;
import com.segfault.games.obj.comp.*;

public class MovementSystem extends IntervalIteratingSystem {
    private final JavaKnight instance;

    public MovementSystem(JavaKnight ins, int priority, float interval) {
        super(Family.all(MovingComponent.class, DrawableComponent.class)
                    .exclude(PrototypeComp.class).get(), interval);
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

        x += dx;
        y += dy;

        boolean hasJBumpCol = entity.getComponent(CollidesComponent.class) != null;

        if (!hasJBumpCol)
            drawable.sprite.setPosition(x, y);

        else {
            CollidesComponent collisionInfo = instance.EntityManager.Cm.get(entity);
            Response.Result res = instance.PhysicWorld.move(collisionInfo.physicItem, x, y, collisionInfo.filter);
            collisionInfo.res = res;

            drawable.sprite.setPosition(res.goalX - collisionInfo.offsetX, res.goalY + collisionInfo.offsetY);
        }

        if (entity.getComponent(RecOwnerComponent.class) != null) {
            RecOwnerComponent recInfo = instance.EntityManager.Rm.get(entity);
            Rec r = recInfo.rectangle;
            r.MoveTo(drawable.sprite.getX(), drawable.sprite.getY(), drawable.sprite.getX(), drawable.sprite.getY(), r.angle);
        }
    }
}
