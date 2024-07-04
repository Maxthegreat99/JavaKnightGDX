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
 * System dealing with damage on collision from entities 
 */
public class DamageCollisionSystem extends IteratingSystem {
    private final JavaKnight instance;
    private final Vector2 range = new Vector2();

    public DamageCollisionSystem(JavaKnight ins, int priority) {
        super(Family.all(DamageComponent.class).get());
        instance = ins;
        this.priority = priority;
    }
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        if (entity.isScheduledForRemoval()) return;

        DamageComponent dmgInfo = instance.EntityManager.Dgm.get(entity);

        boolean hasJBumpCol = instance.EntityManager.Cm.get(entity) != null;

        if (!hasJBumpCol) {
            RecOwnerComponent rOwner = instance.EntityManager.Rm.get(entity);
            RectangleCollisionComponent rcInfo = instance.EntityManager.Rcm.get(entity);
            Rec r2 = rcInfo.targetRectangle;

            range.set(rOwner.rectangle.X, rOwner.rectangle.Y);

            if (range.dst2(r2.X, r2.Y) > rcInfo.checkRange2
                || !r2.IsPolygonsIntersecting(rOwner.rectangle)) return;

            takeHit(dmgInfo.target, dmgInfo);

            return;
        }


        CollidesComponent colInfo = instance.EntityManager.Cm.get(entity);

        for (Item<Entity> c : colInfo.res.projectedCollisions.others) {
            // continue if the relationship of the entity is not right
            if (!instance.EntityManager.Cm.get(c.userData).collisionRelationShip.equals(dmgInfo.relationship))
                continue;

            // if the component has no target and that the entity has a lifecomponent
            // hit the entity, else hit the component's target
            if (dmgInfo.target == null && instance.EntityManager.Lim.get(c.userData) != null) {
                takeHit(instance.EntityManager.Lim.get(c.userData), dmgInfo);
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
