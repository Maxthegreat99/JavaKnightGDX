package com.segfault.games.obj.sys;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.segfault.games.JavaKnight;
import com.segfault.games.obj.comp.DrawableComponent;

import java.util.Comparator;

/**
 * Rendering system, sorts drawables by their order and renders them to the currently open buffers
 */
public class RenderingSystem extends SortedIteratingSystem {
    private final JavaKnight instance;
    private final SpriteBatch batch;

    public RenderingSystem(JavaKnight instance, SpriteBatch b, int priority) {
        super(Family.all(DrawableComponent.class).get(),
              new OrderComparator(instance));
        this.instance = instance;
        batch = b;
        this.priority = priority;
    }

    protected void processEntity(Entity entity, float deltaTime) {

        DrawableComponent drawableComp = instance.EntityManager.Dm.get(entity);

        if (!drawableComp.blending) batch.disableBlending();
        drawableComp.sprite.draw(batch);
        if (!drawableComp.blending) batch.enableBlending();
    }


}

/** comparator handling the sorting of entities for the renderer to go through */
 class OrderComparator implements Comparator<Entity> {
    private final ComponentMapper<DrawableComponent> drawableMapper;

    public OrderComparator(JavaKnight ins) {
        drawableMapper = ins.EntityManager.Dm;
    }

    @Override
    public int compare(Entity e1, Entity e2) {
        return (int) Math.signum(drawableMapper.get(e1).order - drawableMapper.get(e2).order);
    }
}