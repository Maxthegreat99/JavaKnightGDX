package com.segfault.games.obj.sys.phy.col_event;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.dongbat.jbump.Collision;
import com.segfault.games.JavaKnight;
import com.segfault.games.obj.Rec;
import com.segfault.games.obj.comp.CollidesComponent;
import com.segfault.games.obj.comp.CollisionRelationship;
import com.segfault.games.obj.comp.DisposeOnCollisionComponent;
import com.segfault.games.obj.comp.RecOwnerComponent;
import com.segfault.games.obj.ent.EntityManager;

/**
 * System disposing entities on specific collisions
 */
public class DisposeCollisionSystem implements CollisionEventSystem {
    private final EntityManager manager;
    private final Vector2 range = new Vector2();

    public DisposeCollisionSystem(JavaKnight ins) {
        manager = ins.GetEntityManager();

    }

    public void processEntity(Entity entity, float interval) {

        DisposeOnCollisionComponent disInfo = manager.GetMappers().DisposeOnCollision.get(entity);

        boolean hasJBumpCol = disInfo.relationships != null;

        if (!hasJBumpCol) {
            RecOwnerComponent rOwner = manager.GetMappers().RecOwner.get(entity);
            Rec r2 = disInfo.rectangle;

            range.set(rOwner.rectangle.X, rOwner.rectangle.Y);

            // first check if the rectangle is in range, if it is
            // see if it collides, if not return
            if (range.dst2(r2.X, r2.Y) > disInfo.checkRange2
                || !r2.IsPolygonsIntersecting(rOwner.rectangle)) return;

            manager.GetEngine().removeEntity(entity);

            return;
        }

        CollidesComponent colInfo = manager.GetMappers().Collides.get(entity);

        // check through each collision if the relationship is the specified one, if so dispose the entity
        for (int i = 0; i < colInfo.res.projectedCollisions.size(); i++) {

            Collision c = colInfo.res.projectedCollisions.get(i);

            CollisionRelationship otherRelationship = manager.GetMappers().Collides.get((Entity) c.other.userData).relationship;
            if (!disInfo.relationships.contains(otherRelationship,false))
                continue;
            manager.GetEngine().removeEntity(entity);
            return;
        }

    }

    @Override
    public void HandleCollision(Entity entity, Collision c) {

        DisposeOnCollisionComponent disInfo = manager.GetMappers().DisposeOnCollision.get(entity);

        manager.GetEngine().removeEntity(entity);
    }
}
