package com.segfault.games.obj.sys.phy.col_event;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.dongbat.jbump.Collision;
import com.dongbat.jbump.Item;
import com.segfault.games.JavaKnight;
import com.segfault.games.obj.Rec;
import com.segfault.games.obj.comp.*;
import com.segfault.games.obj.ent.EntityManager;

/**
 * System dealing with damage on collision from entities
 */
public class DamageCollisionSystem implements CollisionEventSystem {
    private final EntityManager manager;
    private final Vector2 range = new Vector2();

    public DamageCollisionSystem(JavaKnight ins) {
        manager = ins.GetEntityManager();
    }

    private static void takeHit(LifeComponent target, DamageComponent dmgInfo) {
        target.life = Math.max(target.life - dmgInfo.damage, 0);
        target.tookHit = true;
    }

    @Override
    public void HandleCollision(Entity entity, Entity target, Collision collision) {
        DamageComponent dmgInfo = manager.GetMappers().Damage.get(entity);

        boolean hasTarget = dmgInfo.target != null;

        LifeComponent targetLife = (hasTarget) ? dmgInfo.target : manager.GetMappers().Life.get(target);

        takeHit(dmgInfo.target, dmgInfo);

    }
}
