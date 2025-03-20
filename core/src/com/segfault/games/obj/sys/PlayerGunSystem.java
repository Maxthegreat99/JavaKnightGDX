package com.segfault.games.obj.sys;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
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
        if (entity.isScheduledForRemoval()) return;
        PositionRecoilComponent posRecoil = manager.GetMappers().PositionRecoil.get(entity);
        PointingComponent pointing = manager.GetMappers().Pointing.get(entity);

        if (posRecoil.dis > 0) {
            float angle = pointing.angle + 180;
            angle %= 360;
            angle = (pointing.flip) ? angle + 20 : angle - 20;
            posRecoil.angle = angle;
        }

        PlayerGunComponent comp = manager.GetMappers().PlayerGun.get(entity);
        AngleRecoilComponent recoil = manager.GetMappers().AngleRecoil.get(entity);
        ScreenRecoilComponent screenRecoil = manager.GetMappers().ScreenRecoil.get(entity);
        BulletSpawnComponent bulletSpawn = manager.GetMappers().BulletSpawn.get(entity);


        if (comp.cooldown <= 0f && Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            recoil.trigger = true;

            posRecoil.trigger = true;
            posRecoil.distanceSpeed = posRecoil.initialDistanceSpeed;
            recoil.angleSpeed = recoil.intialAngleSpeed;

            comp.cooldown = comp.initialCd;

            screenRecoil.trigger = true;
            screenRecoil.angle = (pointing.angle + 180) % 360;

            bulletSpawn.spawn = true;
            /*
             * calculate the position to spawn the bullet
             */
            Sprite sprite = manager.GetMappers().Drawable.get(entity).sprite;
            float cos = MathUtils.cosDeg(pointing.angle);
            float sin = MathUtils.sinDeg(pointing.angle);
            bulletSpawn.spawnPosX = (sprite.getX() + sprite.getWidth() / 2) + (cos * sprite.getWidth() / 2) - (bulletSpawn.width / 2);
            bulletSpawn.spawnPosY = (sprite.getY() + sprite.getHeight() / 2) + (sin * sprite.getHeight() / 2) - (bulletSpawn.height / 2);

            bulletSpawn.deltaX = comp.bulletSpeed * cos;
            bulletSpawn.deltaY = comp.bulletSpeed * sin;

            bulletSpawn.angle = pointing.angle;
        }

        comp.cooldown -= deltaTime;

    }
}
