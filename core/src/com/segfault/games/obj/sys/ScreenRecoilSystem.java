package com.segfault.games.obj.sys;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.segfault.games.JavaKnight;
import com.segfault.games.obj.comp.DrawableComponent;
import com.segfault.games.obj.comp.PositionRecoilComponent;
import com.segfault.games.obj.comp.ScreenRecoilComponent;
import com.segfault.games.obj.ent.Mappers;

public class ScreenRecoilSystem extends IteratingSystem {
    private final Mappers mappers;
    private final JavaKnight instance;
    public ScreenRecoilSystem(JavaKnight instance, int priority) {
        super(Family.all(PositionRecoilComponent.class, DrawableComponent.class).get(), priority);
        mappers = instance.GetEntityManager().GetMappers();
        this.instance = instance;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        ScreenRecoilComponent screenRecoil = mappers.ScreenRecoil.get(entity);

        if (screenRecoil.retainTime > 0) {
            screenRecoil.retainTime -= deltaTime;
            float delta = screenRecoil.distanceAcceleration / screenRecoil.divisor * deltaTime;
            screenRecoil.dis += delta;

            instance.GetRenderer().screenTranslationX += delta * MathUtils.cosDeg(screenRecoil.angle);
            instance.GetRenderer().screenTranslationY += delta * MathUtils.sinDeg(screenRecoil.angle);

        }
        else if(screenRecoil.trigger) {
            screenRecoil.distanceSpeed += screenRecoil.distanceAcceleration;
            float delta = screenRecoil.distanceSpeed * deltaTime;
            screenRecoil.dis += delta;

            instance.GetRenderer().screenTranslationX += delta * MathUtils.cosDeg(screenRecoil.angle);
            instance.GetRenderer().screenTranslationY += delta * MathUtils.sinDeg(screenRecoil.angle);

            if (screenRecoil.dis > screenRecoil.maxDis) {
                screenRecoil.trigger = false;
                screenRecoil.retainTime = screenRecoil.initialRetainTime;
                screenRecoil.distanceSpeed = screenRecoil.initialDistanceSpeed;
            }
        }
        else if (screenRecoil.dis > 0) {
            screenRecoil.distanceSpeed += screenRecoil.distanceAcceleration;
            float delta = screenRecoil.distanceSpeed / screenRecoil.divisor * deltaTime;
            screenRecoil.dis -= delta;


            instance.GetRenderer().screenTranslationX -= delta * MathUtils.cosDeg(screenRecoil.angle);
            instance.GetRenderer().screenTranslationY -= delta * MathUtils.sinDeg(screenRecoil.angle);

            if (screenRecoil.dis <= 0) {
                screenRecoil.distanceSpeed = screenRecoil.initialDistanceSpeed;
                instance.GetRenderer().screenTranslationX += -screenRecoil.dis * MathUtils.cosDeg(screenRecoil.angle);
                instance.GetRenderer().screenTranslationY += -screenRecoil.dis * MathUtils.sinDeg(screenRecoil.angle);

                screenRecoil.dis = 0;
            }
        }

    }
}
