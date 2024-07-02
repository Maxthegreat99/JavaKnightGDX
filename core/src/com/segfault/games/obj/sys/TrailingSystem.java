package com.segfault.games.obj.sys;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.segfault.games.JavaKnight;
import com.segfault.games.obj.comp.*;

public class TrailingSystem extends IteratingSystem {
    private final JavaKnight instance;
    public TrailingSystem(JavaKnight ins) {
        super(Family.all(TrailComponent.class, DrawableComponent.class).get());
        instance = ins;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        if (entity.isScheduledForRemoval()) return;

        TrailComponent trailing = instance.EntityManager.Tm.get(entity);
        DrawableComponent drawable = instance.EntityManager.Dm.get(entity);


        if (trailing.trailCooldown <= 0f) {
            trailing.trailCooldown = trailing.trailInitialCooldown;

            Entity e = instance.PooledECS.createEntity();
            DrawableComponent eDraw = instance.PooledECS.createComponent(DrawableComponent.class);
            eDraw.order = 1;
            eDraw.blending = true;
            eDraw.sprite = new Sprite(drawable.sprite);
            eDraw.alpha = trailing.trailIninitalAlpha;
            eDraw.sprite.setAlpha(trailing.trailIninitalAlpha);
            eDraw.sprite.setPosition(drawable.sprite.getX(), drawable.sprite.getY());
            e.add(eDraw);

            DecreasingAlplaComponent alphaDecreaseComp = instance.PooledECS.createComponent(DecreasingAlplaComponent.class);
            alphaDecreaseComp.comparator = trailing.alphaComparator;
            alphaDecreaseComp.alphaDecrease = trailing.trailAlphaDecrease;
            e.add(alphaDecreaseComp);
            instance.PooledECS.addEntity(e);

        }

        trailing.trailCooldown -= deltaTime;

    }

}
