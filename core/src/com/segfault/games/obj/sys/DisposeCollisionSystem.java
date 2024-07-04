package com.segfault.games.obj.sys;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.dongbat.jbump.Item;
import com.segfault.games.JavaKnight;
import com.segfault.games.obj.Rec;
import com.segfault.games.obj.comp.*;

/**
 * System disposing entities on specific collisions
 */
public class DisposeCollisionSystem extends IteratingSystem {
    private final JavaKnight instance;
    private final Vector2 range = new Vector2();

    public DisposeCollisionSystem(JavaKnight ins, int priority) {
        super(Family.all(DisposeOnCollisionComponent.class).get());
        instance = ins;
        this.priority = priority;
    }
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        if (entity.isScheduledForRemoval()) return;

        DisposeOnCollisionComponent disInfo = instance.EntityManager.Dim.get(entity);

        boolean hasJBumpCol = disInfo.relationship != null;

        if (!hasJBumpCol) {
            RecOwnerComponent rOwner = instance.EntityManager.Rm.get(entity);
            Rec r2 = disInfo.rectangle;

            range.set(rOwner.rectangle.X, rOwner.rectangle.Y);

            // first check if the rectangle is in range, if it is
            // see if it collides, if not return
            if (range.dst2(r2.X, r2.Y) > disInfo.checkRange2
                || !r2.IsPolygonsIntersecting(rOwner.rectangle)) return;

            instance.PooledECS.removeEntity(entity);

            return;
        }

        CollidesComponent colInfo = instance.EntityManager.Cm.get(entity);

        // check through each collision if the relationship is the specified one, if so dispose the entity
        for (Item<Entity> c : colInfo.res.projectedCollisions.others) {
            if (!instance.EntityManager.Cm.get(c.userData).collisionRelationShip.equals(disInfo.relationship))
                continue;
            instance.PooledECS.removeEntity(entity);
            return;
        }

    }
}
