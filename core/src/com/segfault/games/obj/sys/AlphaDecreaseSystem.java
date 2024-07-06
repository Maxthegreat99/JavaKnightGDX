package com.segfault.games.obj.sys;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.segfault.games.JavaKnight;
import com.segfault.games.obj.comp.AlphaDecreaseComponent;
import com.segfault.games.obj.comp.DrawableComponent;
import com.segfault.games.obj.ent.EntityManager;

/**
 * Controls decreasing entities' alphas with deceleration
 */
public class AlphaDecreaseSystem extends IteratingSystem {
    private final EntityManager manager;
    public AlphaDecreaseSystem (JavaKnight ins, int priority) {
        super(Family.all(AlphaDecreaseComponent.class, DrawableComponent.class).get());
        manager = ins.GetEntityManager();
        this.priority = priority;
    }
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        if (entity.isScheduledForRemoval()) return;

        DrawableComponent drawableInfo = manager.GetMappers().Drawable.get(entity);
        AlphaDecreaseComponent alphaDecInfo = manager.GetMappers().AlphaDecrease.get(entity);

        // decrease depending of the comparator
        if (drawableInfo.alpha > alphaDecInfo.comparator)
            drawableInfo.alpha -= alphaDecInfo.alphaDecrease * 2 * deltaTime;
        else if (drawableInfo.alpha > alphaDecInfo.comparator / 2)
            drawableInfo.alpha -= alphaDecInfo.alphaDecrease * deltaTime;
        else drawableInfo.alpha -= alphaDecInfo.alphaDecrease / 2 * deltaTime;


        // dispose when alpha reaches below 0
        if (drawableInfo.alpha < 0f)  {
            drawableInfo.alpha = 0f;
            manager.GetEngine().removeEntity(entity);
        }

        // you cannot get a sprite's alpha so we need to store them in a variable to control it
        drawableInfo.sprite.setAlpha(drawableInfo.alpha);
    }
}
