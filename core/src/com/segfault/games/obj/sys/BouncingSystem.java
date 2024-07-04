package com.segfault.games.obj.sys;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.dongbat.jbump.Collision;
import com.segfault.games.JavaKnight;
import com.segfault.games.obj.comp.BounceComponent;
import com.segfault.games.obj.comp.CollidesComponent;
import com.segfault.games.obj.comp.MovingComponent;

/**
 * System controling bouncing entities
 */
public class BouncingSystem extends IteratingSystem {
    private final JavaKnight instance;
    private final Vector2 tmp = new Vector2();
    public BouncingSystem(JavaKnight ins, int priority) {
        super(Family.all(BounceComponent.class, CollidesComponent.class, MovingComponent.class).get());
        instance = ins;
        this.priority = priority;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        BounceComponent BouncingInfo = instance.EntityManager.Bm.get(entity);
        CollidesComponent colInfo = instance.EntityManager.Cm.get(entity);

        // go through the different collisions to find the right one
        boolean found = false;
        Collision c = null;

        for (int i = 0; i < colInfo.res.projectedCollisions.size(); i++) {
            c = colInfo.res.projectedCollisions.get(i);

            // check for the right relationship
            if (instance.EntityManager.Cm.get((Entity) c.other.userData).collisionRelationShip != BouncingInfo.relationship)
                continue;

            found = true;
        }

        if (!found) return;

        BouncingInfo.bounces++;

        if (BouncingInfo.bounces > BouncingInfo.maxBounces) {
            instance.PooledECS.removeEntity(entity);
            return;
        }

        MovingComponent MvInf = instance.EntityManager.Mm.get(entity);

        // reverse direction based on which side was hit
        if (c.normal.y != 0) {
            MvInf.dx = -MvInf.dx;
            MvInf.dy = -MvInf.dy;
        }
        if (c.normal.x != 0) {
            tmp.set(MvInf.dx, MvInf.dy);
            tmp.rotateRad(MathUtils.PI);
            MvInf.dx = tmp.x;
            MvInf.dy = tmp.y;
        }

    }


}
