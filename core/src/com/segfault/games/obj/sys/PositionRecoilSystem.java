package com.segfault.games.obj.sys;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.segfault.games.JavaKnight;
import com.segfault.games.obj.comp.DrawableComponent;
import com.segfault.games.obj.comp.PositionRecoilComponent;
import com.segfault.games.obj.ent.Mappers;

public class PositionRecoilSystem extends IteratingSystem {
    private final Mappers mappers;
    public PositionRecoilSystem(JavaKnight instance, int priority) {
        super(Family.all(PositionRecoilComponent.class, DrawableComponent.class).get(), priority);
        mappers = instance.GetEntityManager().GetMappers();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        if (entity.isScheduledForRemoval()) return;

        DrawableComponent drawable = mappers.Drawable.get(entity);
        PositionRecoilComponent posRecoil = mappers.PositionRecoil.get(entity);

        if(posRecoil.trigger) {
            posRecoil.dis += posRecoil.distanceSpeed * deltaTime;
            posRecoil.trigger = false;

        } else if (posRecoil.dis > 0) {
            posRecoil.distanceSpeed -= posRecoil.distanceDecceleration;
            posRecoil.dis += posRecoil.distanceSpeed * deltaTime;
            drawable.sprite.translate(posRecoil.dis * MathUtils.cosDeg(posRecoil.angle), posRecoil.dis * MathUtils.sinDeg(posRecoil.angle));

            if (posRecoil.dis <= 0) {
                posRecoil.distanceSpeed = posRecoil.initialDistanceSpeed;
                posRecoil.dis = 0;
            }
            else if (posRecoil.dis > posRecoil.maxDis)
                posRecoil.dis = posRecoil.maxDis;

        }

    }
}
