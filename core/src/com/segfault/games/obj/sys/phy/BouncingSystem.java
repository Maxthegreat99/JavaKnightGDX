package com.segfault.games.obj.sys.phy;

import com.badlogic.ashley.core.Entity;
import com.dongbat.jbump.Collision;
import com.segfault.games.JavaKnight;
import com.segfault.games.obj.comp.BounceComponent;
import com.segfault.games.obj.comp.CollidesComponent;
import com.segfault.games.obj.comp.CollisionRelationship;
import com.segfault.games.obj.comp.MovingComponent;
import com.segfault.games.obj.ent.EntityManager;

/**
 * System controling bouncing entities
 */
public class BouncingSystem {
    private final EntityManager manager;
    public BouncingSystem(JavaKnight ins) {
        manager = ins.GetEntityManager();

    }

    public void processEntity(Entity entity) {
        BounceComponent BouncingInfo = manager.GetMappers().Bounce.get(entity);
        CollidesComponent colInfo = manager.GetMappers().Collides.get(entity);

        if (colInfo.res == null) return;

        // go through the different collisions to find the right one
        boolean found = false;
        Collision c = null;

        for (int i = 0; i < colInfo.res.projectedCollisions.size(); i++) {

            c = colInfo.res.projectedCollisions.get(i);

            // check for the right relationship
            if (BouncingInfo.relationship != CollisionRelationship.NULL
                && manager.GetMappers().Collides.get((Entity) c.other.userData).relationship != BouncingInfo.relationship)
                continue;

            found = true;
        }

        if (!found) return;

        BouncingInfo.bounces++;

        if (BouncingInfo.bounces > BouncingInfo.maxBounces) {
            manager.GetEngine().removeEntity(entity);
            return;
        }

        MovingComponent MvInf = manager.GetMappers().Moving.get(entity);

        // reverse direction based on which side was hit
        if (c.normal.y != 0) {
            MvInf.dy = -MvInf.dy;
        }
        if (c.normal.x != 0) {
            MvInf.dx = -MvInf.dx;
        }

    }
}
