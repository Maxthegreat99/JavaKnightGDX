package com.segfault.games.obj.sys;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.segfault.games.JavaKnight;
import com.segfault.games.obj.comp.LifetimeComponent;

/**
 * system that control and dispose lifetimed entities
 */
public class LifetimeSystem extends IteratingSystem {
    private final JavaKnight instance;
    public LifetimeSystem(JavaKnight ins, int priority) {
        super(Family.all(LifetimeComponent.class).get());
        instance = ins;
        this.priority = priority;
    }
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        if (entity.isScheduledForRemoval()) return;

        LifetimeComponent lifetimeInfo = instance.EntityManager.Lm.get(entity);

        lifetimeInfo.lifetime -= deltaTime;

        if (lifetimeInfo.lifetime <= 0) instance.PooledECS.removeEntity(entity);

    }
}
