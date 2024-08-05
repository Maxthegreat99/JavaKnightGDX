package com.segfault.games.obj.sys;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector4;
import com.segfault.games.JavaKnight;
import com.segfault.games.obj.comp.BulletSpawnComponent;
import com.segfault.games.obj.comp.DrawableComponent;
import com.segfault.games.obj.comp.MovingComponent;
import com.segfault.games.obj.ent.Mappers;

public class BulletSpawnSystem extends IteratingSystem {
    private final Mappers mappers;
    private final JavaKnight instance;
    public BulletSpawnSystem(JavaKnight instance, int priority) {
        super(Family.all(BulletSpawnComponent.class).get(), priority);
        mappers = instance.GetEntityManager().GetMappers();
        this.instance = instance;
    }

    private final Vector4 entInf = new Vector4();
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        if (entity.isScheduledForRemoval()) return;

        BulletSpawnComponent bulletSpawn = mappers.BulletSpawn.get(entity);

        if (!bulletSpawn.spawn) return;

        entInf.set(bulletSpawn.spawnPosX, bulletSpawn.spawnPosY, bulletSpawn.width, bulletSpawn.height);
        Entity spawnedEntity = instance.GetEntityManager().GetEntityCreator().SpawnEntity(bulletSpawn.bulletID, true, entInf, null);

        MovingComponent moving = mappers.Moving.get(spawnedEntity);

        moving.dx = bulletSpawn.deltaX;
        moving.dy = bulletSpawn.deltaY;

        if (bulletSpawn.angle != 0) {
            DrawableComponent drawable = mappers.Drawable.get(spawnedEntity);
            drawable.sprite.setRotation(bulletSpawn.angle);
        }
        bulletSpawn.spawn = false;
    }
}
