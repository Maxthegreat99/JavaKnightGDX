package com.segfault.games.obj.sys;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.dongbat.jbump.Item;
import com.segfault.games.JavaKnight;
import com.segfault.games.obj.Rec;
import com.segfault.games.obj.comp.*;

public class DamageCollisionSystem extends IteratingSystem {
    private final JavaKnight instance;
    private final Vector2 tmp = new Vector2();

    public DamageCollisionSystem(JavaKnight ins, int priority) {
        super(Family.all(DamageComponent.class)
                    .exclude(PrototypeComp.class).get());
        instance = ins;
        this.priority = priority;
    }
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        if (entity.isScheduledForRemoval()) return;

        DamageComponent dmgInfo = instance.EntityManager.Dgm.get(entity);

        boolean hasJBumpCol = entity.getComponent(CollidesComponent.class) != null;

        if (hasJBumpCol) {
            CollidesComponent Jcol = instance.EntityManager.Cm.get(entity);

            for (Item<Entity> c : Jcol.res.projectedCollisions.others) {
                if (!instance.EntityManager.Cm.get(c.userData).collisionRelationShip.equals(dmgInfo.relationship))
                    continue;

                if (instance.EntityManager.Lim.get(c.userData) != null) {
                    LifeComponent lifeInf = instance.EntityManager.Lim.get(c.userData);
                    lifeInf.life -= dmgInfo.damage;
                    lifeInf.life = Math.max(lifeInf.life, 0);
                    lifeInf.tookHit = true;
                    return;
                }

                LifeComponent lifeInf = dmgInfo.target;
                lifeInf.life -= dmgInfo.damage;
                lifeInf.life = Math.max(lifeInf.life, 0);
                lifeInf.tookHit = true;
            }

            return;
        }

        RecOwnerComponent rOwner = instance.EntityManager.Rm.get(entity);
        RectangleCollisionComponent rcInfo = instance.EntityManager.Rcm.get(entity);
        Rec r2 = rcInfo.targetRectangle;

        tmp.set(rOwner.rectangle.X, rOwner.rectangle.Y);

        if (tmp.dst2(r2.X, r2.Y) > rcInfo.checkRange * rcInfo.checkRange
                || !r2.IsPolygonsIntersecting(rOwner.rectangle)) return;

        LifeComponent lifeInf = dmgInfo.target;
        lifeInf.life -= dmgInfo.damage;
        lifeInf.life = Math.max(lifeInf.life, 0);
        lifeInf.tookHit = true;

    }
}
