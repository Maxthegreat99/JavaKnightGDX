package com.segfault.games.obj.sys;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.dongbat.jbump.Collision;
import com.segfault.games.JavaKnight;
import com.segfault.games.obj.comp.BounceComponent;
import com.segfault.games.obj.comp.CollidesComponent;
import com.segfault.games.obj.comp.MovingComponent;
import com.segfault.games.obj.comp.PrototypeComp;

public class BouncingSystem extends IteratingSystem {
    private final JavaKnight instance;
    private final Vector2 tmp = new Vector2();
    public BouncingSystem(JavaKnight ins, int priority) {
        super(Family.all(BounceComponent.class, CollidesComponent.class, MovingComponent.class)
                    .exclude(PrototypeComp.class).get());
        instance = ins;
        this.priority = priority;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        BounceComponent BInf = instance.EntityManager.Bm.get(entity);
        CollidesComponent Jcol = instance.EntityManager.Cm.get(entity);

        for (int i = 0; i < Jcol.res.projectedCollisions.size(); i++) {
            Collision c = Jcol.res.projectedCollisions.get(i);
            if (instance.EntityManager.Cm.get((Entity) c.other.userData).collisionRelationShip != BInf.relationship)
                continue;

            BInf.bounces++;
            if (BInf.bounces > BInf.maxBounces) {
                instance.PooledECS.removeEntity(entity);
                return;
            }
            MovingComponent MvInf = instance.EntityManager.Mm.get(entity);

            if (c.normal.y != 0) {
                MvInf.dx = -MvInf.dx;
                MvInf.dy = -MvInf.dy;
            }
            if (c.normal.x != 0) {
                tmp.set(MvInf.dx, MvInf.dy);
                tmp.rotateDeg(180f);
                MvInf.dx = tmp.x;
                MvInf.dy = tmp.y;
            }

        }

    }
}
