package com.segfault.games.obj.sys;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.segfault.games.JavaKnight;
import com.segfault.games.obj.comp.DecreasingAlplaComponent;
import com.segfault.games.obj.comp.DrawableComponent;

public class AlphaDecreaseSystem extends IteratingSystem {
    private final JavaKnight instance;
    public AlphaDecreaseSystem (JavaKnight ins) {
        super(Family.all(DecreasingAlplaComponent.class, DrawableComponent.class).get());
        instance = ins;
    }
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        if (entity.isScheduledForRemoval()) return;

        DrawableComponent drawableInfo = instance.EntityManager.Dm.get(entity);
        DecreasingAlplaComponent alphaDecInfo = instance.EntityManager.Am.get(entity);

        if (drawableInfo.alpha > alphaDecInfo.comparator)
            drawableInfo.alpha -= alphaDecInfo.alphaDecrease * 2 * deltaTime;
        else if (drawableInfo.alpha > alphaDecInfo.comparator / 2)
            drawableInfo.alpha -= alphaDecInfo.alphaDecrease * deltaTime;
        else drawableInfo.alpha -= alphaDecInfo.alphaDecrease / 2 * deltaTime;


        if (drawableInfo.alpha < 0f)  {
            drawableInfo.alpha = 0f;
            instance.PooledECS.removeEntity(entity);
        }

        drawableInfo.sprite.setAlpha(drawableInfo.alpha);
    }
}
