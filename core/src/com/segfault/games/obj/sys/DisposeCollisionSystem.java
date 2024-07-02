package com.segfault.games.obj.sys;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.dongbat.jbump.Item;
import com.segfault.games.JavaKnight;
import com.segfault.games.obj.Rec;
import com.segfault.games.obj.comp.*;

public class DisposeCollisionSystem extends IteratingSystem {
    private final JavaKnight instance;
    private final Vector2 tmp = new Vector2();

    public DisposeCollisionSystem(JavaKnight ins) {
        super(Family.all(CollisionDisposeComponent.class).get());
        instance = ins;
    }
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        if (entity.isScheduledForRemoval()) return;

        CollisionDisposeComponent disInfo = instance.EntityManager.Dim.get(entity);

        boolean hasJBumpCol = disInfo.relationship != null;

        if (hasJBumpCol) {
            CollidesComponent Jcol = instance.EntityManager.Cm.get(entity);

            for (Item<Entity> c : Jcol.res.projectedCollisions.others) {
                if (instance.EntityManager.Cm.get(c.userData).collisionRelationShip != disInfo.relationship)
                    continue;
                instance.PooledECS.removeEntity(entity);
            }

            return;
        }

        RecOwnerComponent rOwner = instance.EntityManager.Rm.get(entity);
        Rec r2 = disInfo.rectangle;

        tmp.set(rOwner.rectangle.X, rOwner.rectangle.Y);

        if (tmp.dst2(r2.X, r2.Y) > disInfo.checkRange * disInfo.checkRange
            || !r2.IsPolygonsIntersecting(rOwner.rectangle)) return;

        instance.PooledECS.removeEntity(entity);
    }
}
