package com.segfault.games.obj.sys.phy;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.dongbat.jbump.Item;
import com.segfault.games.JavaKnight;
import com.segfault.games.obj.Rec;
import com.segfault.games.obj.comp.*;
import com.segfault.games.obj.ent.EntityManager;

/**
 * System dealing with damage on collision from entities 
 */
public class DamageCollisionSystem {
    private final EntityManager manager;
    private final Vector2 range = new Vector2();

    public DamageCollisionSystem(JavaKnight ins) {
        manager = ins.GetEntityManager();

    }
    public void processEntity(Entity entity) {
        if (entity.isScheduledForRemoval()) return;

        DamageComponent dmgInfo = manager.GetMappers().Damage.get(entity);

        boolean hasJBumpCol = manager.GetMappers().Collides.get(entity) != null;

        if (!hasJBumpCol) {
            RecOwnerComponent rOwner = manager.GetMappers().RecOwner.get(entity);
            RectangleCollisionComponent rcInfo = manager.GetMappers().RecCollision.get(entity);
            Rec r2 = rcInfo.targetRectangle;

            range.set(rOwner.rectangle.X, rOwner.rectangle.Y);

            if (range.dst2(r2.X, r2.Y) > rcInfo.checkRange2
                || !r2.IsPolygonsIntersecting(rOwner.rectangle)) return;

            takeHit(dmgInfo.target, dmgInfo);

            return;
        }


        CollidesComponent colInfo = manager.GetMappers().Collides.get(entity);

        for (Item<Entity> c : colInfo.res.projectedCollisions.others) {
            // continue if the relationship of the entity is not right
            if (manager.GetMappers().Collides.get(c.userData).relationship.equals(dmgInfo.relationship))
                continue;

            // if the component has no target and that the entity has a lifecomponent
            // hit the entity, else hit the component's target
            if (dmgInfo.target == null && manager.GetMappers().Life.get(c.userData) != null) {
                takeHit(manager.GetMappers().Life.get(c.userData), dmgInfo);
                continue;
            }

            takeHit(dmgInfo.target, dmgInfo);
        }

    }

    private static void takeHit(LifeComponent target, DamageComponent dmgInfo) {
        target.life = Math.max(target.life - dmgInfo.damage, 0);
        target.tookHit = true;
    }
}
