package com.segfault.games.obj.sys;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.segfault.games.JavaKnight;
import com.segfault.games.obj.comp.DrawableComponent;
import com.segfault.games.obj.ent.EntityManager;
import com.segfault.games.obj.sys.abs.SortedSystem;

import java.util.Comparator;

/**
 * Rendering system, sorts drawables by their order and renders them to the currently open buffers
 */
public class RenderingSystem extends SortedSystem {
    private final EntityManager manager;
    private final SpriteBatch batch;
    private final FrameBuffer screenBuffer;

    public RenderingSystem(JavaKnight instance, SpriteBatch b, int priority) {
        super(Family.all(DrawableComponent.class).get(),
              new OrderComparator(instance.GetEntityManager()));
        manager = instance.GetEntityManager();
        batch = b;
        screenBuffer = instance.GetRenderer().GetScreenBuffer();
        this.priority = priority;
    }

    @Override
    public void update (float deltaTime) {
        sort();

        screenBuffer.begin();

        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        for (int i = 0; i < sortedEntities.size; ++i) {
            processEntity(sortedEntities.get(i), deltaTime);
        }
    }

    protected void processEntity(Entity entity, float deltaTime) {

        DrawableComponent drawableComp = manager.GetMappers().Drawable.get(entity);

        drawableComp.sprite.draw(batch);
    }


}

/** comparator handling the sorting of entities for the renderer to go through */
 class OrderComparator implements Comparator<Entity> {
    private final ComponentMapper<DrawableComponent> drawableMapper;

    public OrderComparator(EntityManager manager) {
        drawableMapper = manager.GetMappers().Drawable;
    }

    @Override
    public int compare(Entity e1, Entity e2) {
        return (drawableMapper.get(e1).order - drawableMapper.get(e2).order) * 1_000_000
                + (drawableMapper.get(e1).shader.ordinal() - drawableMapper.get(e2).shader.ordinal());
    }
}