package com.segfault.games.obj.sys;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.MathUtils;
import com.segfault.games.JavaKnight;
import com.segfault.games.gra.NormalBatch;
import com.segfault.games.obj.comp.DrawableComponent;
import com.segfault.games.obj.comp.NormalComponent;
import com.segfault.games.obj.ent.EntityManager;
import com.segfault.games.obj.sys.abs.SortedSystem;
import com.segfault.games.util.AssetManager;

public class NormalRenderingSystem extends SortedSystem {
    private final EntityManager manager;
    private final NormalBatch batch;
    private final AssetManager assetManager;
    private final FrameBuffer normalFbo;

    public NormalRenderingSystem(JavaKnight instance, NormalBatch b, int priority) {
        super(Family.all(NormalComponent.class).get(),
                new OrderComparator(instance.GetEntityManager()));
        manager = instance.GetEntityManager();
        assetManager = instance.GetAssetManager();
        batch = b;
        normalFbo = instance.GetRenderer().GetNormalBuffer();
        this.priority = priority;
    }

    @Override
    public void update (float deltaTime) {
        sort();

        normalFbo.begin();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        for (int i = 0; i < sortedEntities.size; ++i) {
            processEntity(sortedEntities.get(i), deltaTime);
        }
        batch.end();
        normalFbo.end();
    }

    protected void processEntity(Entity entity, float deltaTime) {

        DrawableComponent drawableComp = manager.GetMappers().Drawable.get(entity);
        NormalComponent normalComp = manager.GetMappers().Normal.get(entity);

        TextureRegion reg = assetManager.GetTextures().get(normalComp.texID);
        Sprite sprite = drawableComp.sprite;
        batch.draw(reg, MathUtils.round(sprite.getX()), MathUtils.round(sprite.getY()), sprite.getWidth() / 2, sprite.getHeight() / 2,
                  sprite.getWidth(), sprite.getHeight(), sprite.getScaleX(), -sprite.getScaleY(), sprite.getRotation());

    }
}
