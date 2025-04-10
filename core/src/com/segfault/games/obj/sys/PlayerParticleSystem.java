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
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Vector4;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
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
    private final ShaderProgram fireCoreShader;
    private final Vector3 cameraPos;
    private final TextureRegion gradient;
    private final FrameBuffer particleBuffer;
    private final FrameBuffer[] chainedBuffer;
    private final TextureRegion[] chainedTexReg;
    private final TextureRegion particleBufferTexReg;
    private float timeElapsed = 0f;
    private final TextureRegion voronoiNoiseCore;
    private final TextureRegion perlinNoiseCore;
    private final OrthographicCamera camera;
    private final Sprite particleCoreSprite;
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
        particleCoreSprite = new Sprite(instance.GetAssetManager().GetTextures().get(indexT.FIRE_CORE));
        fireCoreShader = instance.GetRenderer().GetShaders().get(Shaders.FIRE_CORE);
        perlinNoiseCore = instance.GetAssetManager().GetTextures().get(indexT.FIRE_CORE_NOISE_PERLIN);
        voronoiNoiseCore = instance.GetAssetManager().GetTextures().get(indexT.FIRE_CORE_NOISE_VORONOI);
    }

    private final Pool<CoreParticle> coreParticlePool = new Pool<CoreParticle>() {
        @Override
        protected CoreParticle newObject() {
            CoreParticle p = new CoreParticle();

            p.elapsedLifetime = 0f;
            p.XlerpFactor = MathUtils.random();
            p.YlerpFactor = MathUtils.random();
            p.lifeTime = MathUtils.random(PARTICLE_LIFETIME_MIN, PARTICLE_LIFETIME_MAX);
            p.sizeY = p.sizeX = MathUtils.random(INITIAL_PARTICLE_SIZE_MIN, INITIAL_PARTICLE_SIZE_MAX);
            p.velX = MathUtils.random(PARTICLE_VEL_X_MIN, PARTICLE_VEL_X_MAX);
            p.velY = MathUtils.random(PARTICLE_VEL_Y_MIN, PARTICLE_VEL_Y_MAX);
            p.sprite.set(particleCoreSprite);

            return p;
        }
        @Override
        protected void reset(CoreParticle p) {
            p.elapsedLifetime = 0f;
            p.XlerpFactor = MathUtils.random();
            p.YlerpFactor = MathUtils.random();
            p.lifeTime = MathUtils.random(PARTICLE_LIFETIME_MIN, PARTICLE_LIFETIME_MAX);
            p.sizeY = p.sizeX = MathUtils.random(INITIAL_PARTICLE_SIZE_MIN, INITIAL_PARTICLE_SIZE_MAX);
            p.velX = MathUtils.random(PARTICLE_VEL_X_MIN, PARTICLE_VEL_X_MAX);
            p.velY = MathUtils.random(PARTICLE_VEL_Y_MIN, PARTICLE_VEL_Y_MAX);
            p.sprite.set(particleCoreSprite);
        }
    };

    private static final Array<CoreParticle> toRemoveCore = new Array<>();
    private static final Array<CoreParticle> particleArray = new Array<>();

    private static final int PARTICLES_PER_UPDATE = 10;
    private static final int PARTICLES_MAX = 500;
    private static final float INITIAL_PARTICLE_SIZE_MIN = 0.02f;
    private static final float INITIAL_PARTICLE_SIZE_MAX = 0.05f;
    private static final Array<Vector4> PARTICLE_SIZE_CURVE = new Array<>(new Vector4[] {
            new Vector4(0.00f, 0.47f, 0.00f, -0.37f),
            new Vector4(0.49f, 0.84f, 0.11f, -0.83f),
            new Vector4(1.00f, 0.00f, 0.00f, 0.04f)
    });
    private static final Array<Vector4> PARTICLE_VEL_X_CURVE_FIRST = new Array<>(new Vector4[] {
            new Vector4(0.00f, 0.00f, 0.00f, 0.00f),
            new Vector4(0.52f, 0.36f, 0.46f, 1.24f),
            new Vector4(1.00f, 0.79f, 0.00f, -0.11f)
    });
    private static final Array<Vector4> PARTICLE_VEL_X_CURVE_SECOND = new Array<>(new Vector4[] {
            new Vector4(0.00f, 0.00f, 0.00f, 0.00f),
            new Vector4(0.52f, 0.36f, 0.46f, 1.24f),
            new Vector4(1.00f, 0.79f, 0.00f, -0.11f)
    });
    private static final Array<Vector4> PARTICLE_VEL_Y_CURVE_FIRST = new Array<>(new Vector4[] {
            new Vector4(0.00f, 0.00f, 0.00f, 0.00f),
            new Vector4(0.22f, 0.11f, 0.44f, 0.49f),
            new Vector4(0.46f, 0.90f, 0.72f, -0.25f),
            new Vector4(1.00f, 0.81f, 0.00f, -0.20f)
    });
    private static final Array<Vector4> PARTICLE_VEL_Y_CURVE_SECOND = new Array<>(new Vector4[] {
            new Vector4(0.00f, 0.00f, 0.00f, 0.00f),
            new Vector4(0.46f, 0.90f, 0.72f, -0.25f),
            new Vector4(1.00f, 0.81f, 0.00f, -0.20f)
    });
    private static final float PARTICLE_LIFETIME_MIN = 0.5f;
    private static final float PARTICLE_LIFETIME_MAX = 1.5f;
    private static final float PARTICLE_VEL_X_MIN = 0.6f;
    private static final float PARTICLE_VEL_X_MAX = 0.8f;
    private static final float PARTICLE_VEL_Y_MIN = 0.8f;
    private static final float PARTICLE_VEL_Y_MAX = 1f;

    public class CoreParticle {
        public Sprite sprite = new Sprite();
        public float velX = 0f;
        public float velY = 0f;
        public float sizeX = 0f;
        public float sizeY = 0f;
        public float lifeTime = 0f;
        public float elapsedLifetime = 0f;
        public float XlerpFactor = 0f;
        public float YlerpFactor = 0f;
    }

    /**
     *  - if allowed spawn new particle
     *  - increase timer for each and spawn obsolete ones
     *  - run velocity code
     *  - render
     */

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

        timeElapsed += deltaTime;

        PlayerParticlesComponent plyPart = mappers.PlayerParticles.get(entity);
        CollidesComponent col = mappers.Collides.get(entity);

        if (particleArray.size < PARTICLES_MAX) {
            for (int i = 0; i < PARTICLES_PER_UPDATE; i++) {
                Vector2 colPos = col.physicBody.getWorldCenter();

                spawnPos.set(colPos);

                dir.setToRandomDirection();
                dir.setLength(MathUtils.random() * col.physicBody.getFixtureList().first().getShape().getRadius());

                spawnPos.add(dir);

                spawnPos.set(spawnPos.x * Renderer.PIXEL_TO_METERS, spawnPos.y * Renderer.PIXEL_TO_METERS);

                CoreParticle p = coreParticlePool.obtain();

                p.sprite.setCenter(spawnPos.x, spawnPos.y - (col.physicBody.getFixtureList().first().getShape().getRadius() * Renderer.PIXEL_TO_METERS) * 0.4f);

                particleArray.add(p);
            }
        }


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
        batch.setShader(fireCoreShader);

        fireCoreShader.setUniformf("u_time", timeElapsed);
        fireCoreShader.setUniformf("u_scale", 0.491f);
        fireCoreShader.setUniformf("u_distortScale", 0.5f);
        fireCoreShader.setUniformf("u_cutoff", 0.309f);
        fireCoreShader.setUniformf("u_speed", 0.309f);
        fireCoreShader.setUniformf("u_brightness", 1.951f);
        fireCoreShader.setUniformf("u_edgeWidth", 0.4f);
        fireCoreShader.setUniformf("u_particleShift", 0.1f);
        fireCoreShader.setUniformf("u_stretch", 0.385f);
        fireCoreShader.setUniformi("u_multiply", 1);
        fireCoreShader.setUniformf("u_startUgradient", gradient.getU());
        fireCoreShader.setUniformf("u_startVgradient",  gradient.getV());
        fireCoreShader.setUniformf("u_gradientWidth",  gradient.getU2() - gradient.getU());
        fireCoreShader.setUniformf("u_tint",  plyPart.endColor);
        fireCoreShader.setUniformf("u_edgeColor",  plyPart.startColor);
        fireCoreShader.setUniformf("u_startUnoise",  voronoiNoiseCore.getU());
        fireCoreShader.setUniformf("u_startVnoise",  voronoiNoiseCore.getV());
        fireCoreShader.setUniformf("u_heightNoise", voronoiNoiseCore.getV2() - voronoiNoiseCore.getV());
        fireCoreShader.setUniformf("u_widthNoise", voronoiNoiseCore.getU2() - voronoiNoiseCore.getU());
        fireCoreShader.setUniformf("u_startUdistort", perlinNoiseCore.getU());
        fireCoreShader.setUniformf("u_startVdistort", perlinNoiseCore.getV());
        fireCoreShader.setUniformf("u_heightDistort", perlinNoiseCore.getV2() - perlinNoiseCore.getV());
        fireCoreShader.setUniformf("u_widthDistort", perlinNoiseCore.getU2() - perlinNoiseCore.getU());
        fireCoreShader.setUniformf("u_mainTexWidth", particleCoreSprite.getU2() - particleCoreSprite.getU());
        fireCoreShader.setUniformf("u_mainTexHeight", particleCoreSprite.getV2() - particleCoreSprite.getV());
        fireCoreShader.setUniformf("u_mainStartU", particleCoreSprite.getU());
        fireCoreShader.setUniformf("u_mainStartV", particleCoreSprite.getV());





        particleCoreSprite.getTexture().bind(0);
        voronoiNoiseCore.getTexture().bind(1);
        perlinNoiseCore.getTexture().bind(2);
        gradient.getTexture().bind(3);

        for (int i = 0; i < particleArray.size; i++) {
            CoreParticle p = particleArray.get(i);
            p.elapsedLifetime += deltaTime;

            if (p.elapsedLifetime > p.lifeTime) {
                toRemoveCore.add(p);
                continue;
            }

            float lifetime = p.elapsedLifetime / p.lifeTime;

            float velX = p.velX * MathUtils.lerp(-getYAtX(PARTICLE_VEL_X_CURVE_FIRST, lifetime), getYAtX(PARTICLE_VEL_X_CURVE_SECOND, lifetime), p.XlerpFactor);
            float velY = p.velY * MathUtils.lerp(getYAtX(PARTICLE_VEL_Y_CURVE_FIRST, lifetime), getYAtX(PARTICLE_VEL_Y_CURVE_SECOND, lifetime), p.YlerpFactor);


            p.sprite.setPosition(p.sprite.getX() + velX, p.sprite.getY() + velY );


            float scaleMul = getYAtX(PARTICLE_SIZE_CURVE, lifetime);

            p.sprite.setScale(p.sizeX * scaleMul, p.sizeY * scaleMul);

            fireCoreShader.setUniformf("u_particleAge", lifetime);

            p.sprite.draw(batch);

            batch.flush();

        }

        /*
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
        }*/

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


        bloomShader.setUniformf("bloom_spread", 0.65f);
        bloomShader.setUniformf("bloom_intensity", 0.9f);
        bloomShader.setUniformf("u_resolution", JavaKnight.FRAME_WIDTH / 4f, JavaKnight.FRAME_HEIGHT / 4f);


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

        for (int i = 0; i < toRemoveCore.size; i++) {
            CoreParticle p = toRemoveCore.get(i);
            coreParticlePool.free(p);
            particleArray.removeValue(p, true);
        }

        toRemoveCore.clear();
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
