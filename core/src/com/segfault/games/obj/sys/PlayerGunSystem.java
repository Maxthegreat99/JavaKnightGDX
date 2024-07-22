package com.segfault.games.obj.sys;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.segfault.games.JavaKnight;
import com.segfault.games.obj.comp.*;
import com.segfault.games.obj.ent.EntityManager;

public class PlayerGunSystem extends IteratingSystem {

    private final EntityManager manager;
    public PlayerGunSystem(JavaKnight instance, int priority) {
        super(Family.all(PlayerGunComponent.class).get(), priority);
        manager = instance.GetEntityManager();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PlayerGunComponent comp = manager.GetMappers().PlayerGun.get(entity);
        AngleRecoilComponent recoil = manager.GetMappers().AngleRecoil.get(entity);
        ScreenRecoilComponent screenRecoil = manager.GetMappers().ScreenRecoil.get(entity);
        PositionRecoilComponent posRecoil = manager.GetMappers().PositionRecoil.get(entity);
        PointingComponent pointing = manager.GetMappers().Pointing.get(entity);


        if (comp.cooldown <= 0f && Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            recoil.trigger = true;
            posRecoil.trigger = true;
            comp.cooldown = comp.initialCd;
            screenRecoil.trigger = true;
            screenRecoil.angle = (pointing.angle + 180) % 360;
        }

        if (posRecoil.dis > 0) {
            float angle = pointing.angle + 180;
            angle %= 360;
            angle = (pointing.flip) ? angle + 20 : angle - 20;
            posRecoil.angle = angle;
        }
        comp.cooldown -= deltaTime;

    }
}
