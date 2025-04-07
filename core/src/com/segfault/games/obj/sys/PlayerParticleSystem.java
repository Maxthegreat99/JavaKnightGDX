package com.segfault.games.obj.sys;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Vector4;
import com.badlogic.gdx.utils.Array;
import com.segfault.games.JavaKnight;
import com.segfault.games.gra.Renderer;
import com.segfault.games.obj.comp.CollidesComponent;
import com.segfault.games.obj.comp.PlayerParticlesComponent;
import com.segfault.games.obj.comp.Shaders;
import com.segfault.games.obj.ent.Mappers;
import com.segfault.games.util.indexT;


public class PlayerParticleSystem extends IteratingSystem {
    private final Mappers mappers;
    private final Vector2 dir = new Vector2();
    private final Vector2 spawnPos = new Vector2();
    private final Array<PlayerParticlesComponent.FireParticle> partsToRemove = new Array<>();
    private final FrameBuffer screenBuffer;
    private final SpriteBatch batch;
    private final ShaderProgram particleFireShader;
    private final ShaderProgram bloomShader;
    private final Vector3 cameraPos;
    private final TextureRegion gradient;
    private final FrameBuffer particleBuffer;
    private final FrameBuffer[] chainedBuffer;
    private final TextureRegion[] chainedTexReg;
    private final TextureRegion particleBufferTexReg;
    private final OrthographicCamera camera;

    public PlayerParticleSystem(JavaKnight instance, int priority) {
        super(Family.all(PlayerParticlesComponent.class).get(), priority);

        batch = instance.GetRenderer().GetSpriteBatch();
        screenBuffer = instance.GetRenderer().GetScreenBuffer();
        particleFireShader = instance.GetRenderer().GetShaders().get(Shaders.FIRE_PARTICLE);
        mappers = instance.GetEntityManager().GetMappers();
        cameraPos = instance.GetRenderer().CameraPos;
        gradient = instance.GetAssetManager().GetTextures().get(indexT.FIRE_GRADIENT);
        particleBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, JavaKnight.FRAME_WIDTH, JavaKnight.FRAME_HEIGHT, false);
        chainedBuffer = instance.GetRenderer().GetChainFBO();
        bloomShader = instance.GetRenderer().GetShaders().get(Shaders.BLOOM_SHADER);
        chainedTexReg = instance.GetRenderer().GetChainTexReg();
        particleBufferTexReg = new TextureRegion(particleBuffer.getColorBufferTexture());
        particleBufferTexReg.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        particleBufferTexReg.flip(false, true);
        camera = instance.GetRenderer().GetScreenCamera();
    }

    /**
     *  - if allowed spawn new particle
     *  - increase timer for each and spawn obsolete ones
     *  - run velocity code
     *  - render
     */

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

        PlayerParticlesComponent plyPart = mappers.PlayerParticles.get(entity);
        CollidesComponent col = mappers.Collides.get(entity);

        if (plyPart.activeParts.size < plyPart.maxParts  && plyPart.partSpawnChance < MathUtils.random()) {
            for (int i = 0; i < 5; i++) {


                Vector2 colPos = col.physicBody.getWorldCenter();

                spawnPos.set(colPos);

                dir.setToRandomDirection();
                dir.setLength(MathUtils.random() * col.physicBody.getFixtureList().first().getShape().getRadius());

                spawnPos.add(dir);

                spawnPos.set(spawnPos.x * Renderer.PIXEL_TO_METERS, spawnPos.y * Renderer.PIXEL_TO_METERS);

                PlayerParticlesComponent.FireParticle p = plyPart.particlePool.obtain();

                p.sprite.setCenter(spawnPos.x, spawnPos.y - (col.physicBody.getFixtureList().first().getShape().getRadius() * Renderer.PIXEL_TO_METERS) * 0.4f);

                plyPart.activeParts.add(p);
            }
        }

        particleBuffer.begin();
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.setShader(particleFireShader);



        particleFireShader.setUniformf("u_cameraPosition", cameraPos);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);


        for (int i = 0; i < plyPart.activeParts.size; i++) {
            PlayerParticlesComponent.FireParticle p = plyPart.activeParts.get(i);

            p.elapsedLifetime += deltaTime;

            if (p.elapsedLifetime > p.lifeTime) {
                partsToRemove.add(p);
                continue;
            }

            p.sprite.setPosition(p.sprite.getX() + p.velX, p.sprite.getY() + p.velY );

            float lifetime = p.elapsedLifetime / p.lifeTime;

            float scaleMultiplier = getYAtX(plyPart.sizeCurve, lifetime);

            gradient.getTexture().bind(1);

            p.sprite.setScale(p.sizeX * scaleMultiplier, p.sizeY * scaleMultiplier);
            particleFireShader.setUniformf("u_voronoiSpeed", p.voronoiSpeed);
            particleFireShader.setUniformf("u_voronoiScale", p.voronoiScale);
            particleFireShader.setUniformf("u_startUgradient", gradient.getU());
            particleFireShader.setUniformf("u_startVgradient", gradient.getV());
            particleFireShader.setUniformf("u_gradientWidth", gradient.getU2() - gradient.getU());
            particleFireShader.setUniformf("u_time", lifetime);
            particleFireShader.setUniformf("u_alphaThreshold", getYAtX(plyPart.alphaClipCurve, lifetime));
            particleFireShader.setUniformf("u_fresnelPower", plyPart.fresnelPower);


            p.sprite.draw(batch);

            batch.flush();
        }

        batch.setShader(null);
        batch.end();
        particleBuffer.end();


        batch.setTransformMatrix(camera.view);
        batch.setProjectionMatrix(camera.projection);


        chainedBuffer[0].begin();

        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.setShader(bloomShader);

        bloomShader.setUniformf("bloom_spread", 0.8f);
        bloomShader.setUniformf("bloom_intensity", 1.2f);
        bloomShader.setUniformf("u_resolution", JavaKnight.FRAME_WIDTH / 2, JavaKnight.FRAME_HEIGHT / 2);


        particleBufferTexReg.getTexture().bind(0);

        batch.draw(particleBufferTexReg, 0, 0);

        batch.setShader(null);
        batch.end();
        chainedBuffer[0].end();


        screenBuffer.begin();
        batch.begin();

        Gdx.gl.glBlendFunc(GL20.GL_ONE, GL20.GL_ONE);

        batch.draw(chainedTexReg[0], 0, 0);

        batch.end();
        screenBuffer.end();

        for (int i = 0; i < partsToRemove.size; i++) {
            PlayerParticlesComponent.FireParticle p = partsToRemove.get(i);
            plyPart.particlePool.free(p);
            plyPart.activeParts.removeValue(p, true);
        }

        partsToRemove.clear();
    }
    @Override
    public void removedFromEngine (Engine engine) {
        particleBuffer.dispose();
        super.removedFromEngine(engine);
    }


    public static float hermiteInterpolate(Vector4 p0, Vector4 p1, float t) {
        float t2 = t * t;
        float t3 = t2 * t;

        float h00 = 2 * t3 - 3 * t2 + 1;
        float h10 = t3 - 2 * t2 + t;
        float h01 = -2 * t3 + 3 * t2;
        float h11 = t3 - t2;

        float dx = p1.x - p0.x;

        float m0 = (p0.z != 0) ? (p0.w / p0.z) : 0;
        float m1 = (p1.z != 0) ? (p1.w / p1.z) : 0;

        return h00 * p0.y + h10 * m0 * dx + h01 * p1.y + h11 * m1 * dx;
    }

    public static float getYAtX(Array<Vector4> points, float queryX) {
        int n = points.size;
        if (queryX <= points.get(0).x) {
            return points.get(0).y;
        } else if (queryX >= points.get(n - 1).x) {
            return points.get(n - 1).y;
        }

        for (int i = 0; i < n - 1; i++) {
            Vector4 p0 = points.get(i);
            Vector4 p1 = points.get(i + 1);
            if (queryX >= p0.x && queryX <= p1.x) {
                float t = (queryX - p0.x) / (p1.x - p0.x);
                return hermiteInterpolate(p0, p1, t);
            }
        }

        return Float.NaN; // Should not reach here if points cover the range.
    }
}
