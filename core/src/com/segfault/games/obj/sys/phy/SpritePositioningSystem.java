package com.segfault.games.obj.sys.phy;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.segfault.games.JavaKnight;
import com.segfault.games.gra.Renderer;
import com.segfault.games.obj.comp.CollidesComponent;
import com.segfault.games.obj.comp.DrawableComponent;
import com.segfault.games.obj.comp.PhysicComponent;
import com.segfault.games.obj.ent.EntityManager;

/**
 * Moves an entity's sprite to a collision's updated position after our physics step
 */

public class SpritePositioningSystem extends IteratingSystem {

    private final EntityManager manager;
    public SpritePositioningSystem(JavaKnight instance, int priority) {
        super(Family.all(PhysicComponent.class, DrawableComponent.class, CollidesComponent.class).get(), priority);

        manager = instance.GetEntityManager();
    }

    private final Vector2 vel = new Vector2();
    @Override
    protected void processEntity(Entity entity, float deltaTime) {

        CollidesComponent collidesComponent = manager.GetMappers().Collides.get(entity);
        DrawableComponent drawableComponent = manager.GetMappers().Drawable.get(entity);

        Vector2 pos = collidesComponent.physicBody.getPosition();

        float x = pos.x * Renderer.PIXEL_TO_METERS - collidesComponent.width * Renderer.PIXEL_TO_METERS / 2;
        float y = pos.y * Renderer.PIXEL_TO_METERS - collidesComponent.height * Renderer.PIXEL_TO_METERS / 2;

        drawableComponent.sprite.setPosition(x,  y);


    }
}
