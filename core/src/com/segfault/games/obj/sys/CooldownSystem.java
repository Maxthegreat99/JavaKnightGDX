package com.segfault.games.obj.sys;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.segfault.games.JavaKnight;
import com.segfault.games.obj.comp.CooldownComponent;

public class CooldownSystem extends IteratingSystem {
    private final JavaKnight instance;
    public CooldownSystem(JavaKnight ins) {
        super(Family.all(CooldownComponent.class).get());
        instance = ins;
    }
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        if (entity.isScheduledForRemoval()) return;

        CooldownComponent cdInfo = instance.EntityManager.Cdm.get(entity);

        if (!cdInfo.automated && !cdInfo.activate) return;

        if (cdInfo.cd > 0) cdInfo.cd = Math.max(cdInfo.cd - deltaTime, 0);
        else cdInfo.cd = cdInfo.initialCd;
    }
}