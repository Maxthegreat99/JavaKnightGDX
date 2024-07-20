package com.segfault.games.obj.sys;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.segfault.games.JavaKnight;
import com.segfault.games.obj.comp.AngleRecoilComponent;
import com.segfault.games.obj.comp.DrawableComponent;
import com.segfault.games.obj.comp.PlayerGunComponent;
import com.segfault.games.obj.comp.PointingComponent;
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
        PointingComponent pointing = manager.GetMappers().Pointing.get(entity);
        AngleRecoilComponent recoil = manager.GetMappers().Recoil.get(entity);

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && comp.cooldown <= 0f) {
            recoil.trigger = true;
            comp.cooldown = comp.initialCd;
            recoil.inverseRotation = pointing.flip;
        }

        comp.cooldown -= deltaTime;

    }
}
