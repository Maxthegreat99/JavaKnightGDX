package com.segfault.games.obj.sys;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.segfault.games.JavaKnight;
import com.segfault.games.obj.comp.CooldownComponent;
import com.segfault.games.obj.ent.EntityManager;

/**
 * System decreasing cooldown of entities based on deltatime
 */
public class CooldownSystem extends IteratingSystem {
    private final EntityManager manager;
    public CooldownSystem(JavaKnight ins, int priority) {
        super(Family.all(CooldownComponent.class).get());
        manager = ins.GetEntityManager();
        this.priority = priority;
    }
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        if (entity.isScheduledForRemoval()) return;

        CooldownComponent cdInfo = manager.GetMappers().Cooldown.get(entity);

        if (!cdInfo.automated && !cdInfo.activate) return;

        if (cdInfo.cd > 0) cdInfo.cd -= deltaTime;
        else cdInfo.cd = cdInfo.initialCd;
    }
}
