package com.segfault.games.obj.sys;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.segfault.games.JavaKnight;
import com.segfault.games.obj.comp.AlphaDecreaseComponent;
import com.segfault.games.obj.comp.DrawableComponent;
import com.segfault.games.obj.comp.MovingComponent;
import com.segfault.games.obj.comp.TrailComponent;
import com.segfault.games.obj.ent.EntityManager;

/**
 * Trailing system, creates copies of the current sprite state of components based on a cooldown
 */
public class TrailingSystem extends IteratingSystem {
    private final EntityManager manager;
    public TrailingSystem(JavaKnight ins, int priority) {
        super(Family.all(TrailComponent.class, DrawableComponent.class).get());
        manager = ins.GetEntityManager();
        this.priority = priority;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        if (entity.isScheduledForRemoval()) return;

        if (manager.GetMappers().Moving.get(entity) != null) {
            MovingComponent comp = manager.GetMappers().Moving.get(entity);
            if (Float.compare(comp.dx, 0) == 0 && Float.compare(comp.dy, 0) == 0)
                return;
        }

        TrailComponent trailing = manager.GetMappers().Trail.get(entity);
        DrawableComponent drawable = manager.GetMappers().Drawable.get(entity);


        if (trailing.trailCooldown > 0f) {
            trailing.trailCooldown -= deltaTime;
            return;
        }

        trailing.trailCooldown = trailing.trailInitialCooldown;

        Entity e = manager.GetEngine().createEntity();
        DrawableComponent eDraw = manager.GetEngine().createComponent(DrawableComponent.class);
        eDraw.order = 1;
        eDraw.blending = true;
        eDraw.sprite = new Sprite(drawable.sprite);
        eDraw.alpha = trailing.trailIninitalAlpha;
        eDraw.sprite.setAlpha(trailing.trailIninitalAlpha);
        eDraw.sprite.setPosition(drawable.sprite.getX(), drawable.sprite.getY());
        e.add(eDraw);

        AlphaDecreaseComponent alphaDecreaseComp = manager.GetEngine().createComponent(AlphaDecreaseComponent.class);
        alphaDecreaseComp.comparator = trailing.alphaComparator;
        alphaDecreaseComp.alphaDecrease = trailing.trailAlphaDecrease;
        e.add(alphaDecreaseComp);

        manager.GetEngine().addEntity(e);

    }

}
