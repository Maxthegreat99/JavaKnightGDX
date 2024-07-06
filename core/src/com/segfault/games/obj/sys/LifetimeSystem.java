package com.segfault.games.obj.sys;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.segfault.games.JavaKnight;
import com.segfault.games.obj.comp.LifetimeComponent;
import com.segfault.games.obj.ent.EntityManager;

/**
 * system that control and dispose lifetimed entities
 */
public class LifetimeSystem extends IteratingSystem {
    private final EntityManager manager;
    public LifetimeSystem(JavaKnight ins, int priority) {
        super(Family.all(LifetimeComponent.class).get());
        manager = ins.GetEntityManager();
        this.priority = priority;
    }
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        if (entity.isScheduledForRemoval()) return;

        LifetimeComponent lifetimeInfo = manager.GetMappers().Lifetime.get(entity);

        lifetimeInfo.lifetime -= deltaTime;

        if (lifetimeInfo.lifetime <= 0) manager.GetEngine().removeEntity(entity);

    }
}
