package com.segfault.games.obj.sys;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.segfault.games.JavaKnight;
import com.segfault.games.obj.comp.*;

public class DamageCollisionSystem extends IteratingSystem {
    private final JavaKnight instance;

    public DamageCollisionSystem(JavaKnight ins) {
        super(Family.all(DamageComponent.class).get());
        instance = ins;
    }
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        if (entity.isScheduledForRemoval()) return;

        DamageComponent dmgInfo = instance.EntityManager.Dgm.get(entity);

        boolean hasJBumpCol = entity.getComponent(CollidesComponent.class) != null;

        if (hasJBumpCol) {
            CollidesComponent Jcol = instance.EntityManager.Cm.get(entity);
            
        }



    }
}
