package com.segfault.games.gra;

import box2dLight.Light;
import box2dLight.RayHandler;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.segfault.games.JavaKnight;
import com.segfault.games.obj.Text;
import com.segfault.games.obj.comp.LightHolderComponent;
import com.segfault.games.obj.comp.Shaders;
import com.segfault.games.obj.ent.EntityManager;
import com.segfault.games.util.AssetManager;
import com.segfault.games.util.indexT;

/**
 * Class where most rendering of the rendering is handled,
 * holds the sprite batch and the background changing logic
 */
public class Renderer {
    public final int GREEN_BG = 0;
    public final int GRAY_BG = 1;
    public final int RED_BG = 2;
    public float ScreenTranslationX = 0;
    public float ScreenTranslationY = 0;

    private final RayHandler rayHandler;

    private final ObjectMap<Shaders, ShaderProgram> shaders = new ObjectMap<>();

    private final ObjectMap<Light, LightHolderComponent.LightObject> lights = new ObjectMap<>();

    private Animation<TextureRegion> background;
    private final SpriteBatch batch;
    private final NormalBatch normalBatch;
    private final OrthographicCamera worldCamera;
    private final OrthographicCamera screenCamera;
    private final OrthographicCamera physicsCamera;
    private final FrameBuffer screenBuffer;
    private final FrameBuffer normalBuffer;
    private final FitViewport viewport;

    private final ShaderProgram fontShader;
    private final BitmapFont font;
    private final Box2DDebugRenderer debugRenderer;
    public float CameraZoom;
    private final TextureRegion[][] backgrounds = new TextureRegion[3][];

    private final int SCREEN_WIDTH;
    private final int SCREEN_HEIGHT;

    private final int FRAME_WIDTH;
    private final int FRAME_HEIGHT;

    public final Vector3 CameraPos = new Vector3();

    public boolean UpdateCamera = false;

    public static final float PIXEL_TO_METERS = 60f;
    private static final Vector3 screenCoord = new Vector3();
    private static final Color ambientColor = new Color(0.15f, 0.0f, 0.15f, 0.4f);

    public Renderer(AssetManager assetManager, EntityManager entityManager, World world , int frameWidth, int frameHeight, float zoom) {

        Graphics.DisplayMode displayMode = Gdx.graphics.getDisplayMode();

        SCREEN_HEIGHT = displayMode.height;
        SCREEN_WIDTH = displayMode.width;
        FRAME_HEIGHT = frameHeight;
        FRAME_WIDTH = frameWidth;

        CameraZoom = zoom;

        batch = new SpriteBatch();
        normalBatch = new NormalBatch();
        screenCamera = new OrthographicCamera();
        worldCamera = new OrthographicCamera(frameWidth, frameHeight);
        physicsCamera = new OrthographicCamera(FRAME_WIDTH / PIXEL_TO_METERS, FRAME_HEIGHT / PIXEL_TO_METERS);

        screenBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, frameWidth, frameHeight, false);

        Gdx.graphics.setFullscreenMode(displayMode);

        viewport = new FitViewport(FRAME_WIDTH, FRAME_HEIGHT, screenCamera);
        viewport.update(SCREEN_WIDTH, SCREEN_HEIGHT, true);

        font = new BitmapFont(Gdx.files.internal("DisposableDroidBB.fnt"), new TextureRegion(assetManager.GetFontTexture()), false);
        fontShader = new ShaderProgram(Gdx.files.internal("Shaders/font.vert"), Gdx.files.internal("Shaders/font.frag"));
        if (!fontShader.isCompiled()) Gdx.app.error("fontShader", "compilation failed:\n" + fontShader.getLog());

        debugRenderer = new Box2DDebugRenderer();


        CameraPos.set((float) FRAME_WIDTH / 2f,
                (float) FRAME_HEIGHT / 2f, 0);
        screenCamera.position.set(CameraPos);
        worldCamera.zoom = 1f;
        worldCamera.setToOrtho(false, frameWidth, frameHeight);

        physicsCamera.setToOrtho(false, FRAME_WIDTH / PIXEL_TO_METERS, FRAME_HEIGHT / PIXEL_TO_METERS);
        physicsCamera.position.set((FRAME_WIDTH / PIXEL_TO_METERS) / 2f ,
                (FRAME_HEIGHT / PIXEL_TO_METERS) / 2f, 0 );
        physicsCamera.update();

        screenCamera.update();
        worldCamera.update();
        physicsCamera.update();

        loadBackgrounds(assetManager);

        Texture sboTexture = screenBuffer.getColorBufferTexture();
        sboTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Nearest);
        fboTextureRegion = new TextureRegion(sboTexture);
        fboTextureRegion.flip(false, true);

        normalBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, frameWidth, frameHeight, false);

        Texture normalsTex = normalBuffer.getColorBufferTexture();

        normalsTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        normals = new TextureRegion(normalsTex);
        normals.flip(false, false);

        viewport.apply(true);

        loadShaders();

        ShaderProgram lightShader = shaders.get(Shaders.LIGHT);

        RayHandler.setGammaCorrection(true);
        RayHandler.useDiffuseLight(true);

        // world, fbowidth, fboheight
        rayHandler = new RayHandler(world, FRAME_WIDTH, FRAME_HEIGHT) {
            @Override protected void updateLightShader () {}

            @Override protected void updateLightShaderPerLight (Light light) {

                Vector3 coord = worldCamera.project(screenCoord.set(light.getX() * PIXEL_TO_METERS, light.getY() * PIXEL_TO_METERS, 0));

                // light position must be normalized
                float x = (coord.x) / viewport.getScreenWidth();
                float y = (coord.y) / viewport.getScreenHeight();

                LightHolderComponent.LightObject l = lights.get(light);

                lightShader.setUniformf("u_lightpos", x, y, 0.05f);
                lightShader.setUniformf("u_intensity", l.color.a * 10f);
                lightShader.setUniformf("u_tint", l.color.r, l.color.g, l.color.b);

            }

        };
        rayHandler.setLightShader(lightShader);
        rayHandler.setAmbientLight(ambientColor);
        rayHandler.setBlurNum(1);

        rayHandler.useCustomViewport(viewport.getScreenX(), viewport.getScreenY(), viewport.getScreenWidth(), viewport.getScreenHeight());

        batch.enableBlending();
        normalBatch.enableBlending();
    }

    /**
     * clears the screen,
     * @param timeElapsed, begins the buffers, sets up camera and draws the background
     */
    public void SetUpFrame(float timeElapsed) {

        batch.setProjectionMatrix(worldCamera.projection);
        batch.setTransformMatrix(worldCamera.view);


        normalBatch.setProjectionMatrix(worldCamera.projection);
        normalBatch.setTransformMatrix(worldCamera.view);

        // clear the screen
        Gdx.gl.glClearColor(1f, 1f, 1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (Float.compare(CameraZoom, worldCamera.zoom) != 0 || UpdateCamera) {
            worldCamera.zoom = CameraZoom;
            worldCamera.position.set(MathUtils.round(CameraPos.x) + MathUtils.round(ScreenTranslationX),
                    MathUtils.round(CameraPos.y) + MathUtils.round(ScreenTranslationY), 0);
            physicsCamera.position.set(MathUtils.round(CameraPos.x) / PIXEL_TO_METERS + MathUtils.round(ScreenTranslationX) / PIXEL_TO_METERS,
                    MathUtils.round(CameraPos.y) / PIXEL_TO_METERS + MathUtils.round(ScreenTranslationY) / PIXEL_TO_METERS, 0);
            worldCamera.update();
            physicsCamera.update();
            UpdateCamera = false;
        }

    }
    private final TextureRegion fboTextureRegion;
    private final TextureRegion normals;
    /**
     * deals with the rendering of text, debug boxes and renders the main texture from the framebuffer
     * @param instance
     * @param physicWorld
     */
    public void Render(JavaKnight instance, World physicWorld) {

        //renderShapes(physicsCamera, debugRenderer, physicWorld);

        batch.end();
        screenBuffer.end();

        rayHandler.setCombinedMatrix(physicsCamera);
        rayHandler.update();
        normals.getTexture().bind(1);
        rayHandler.prepareRender();
        screenBuffer.begin();
        rayHandler.renderOnly();
        screenBuffer.end();

        viewport.update(SCREEN_WIDTH, SCREEN_HEIGHT);

        batch.setProjectionMatrix(screenCamera.projection);
        batch.setTransformMatrix(screenCamera.view);

        batch.begin();
        batch.draw(fboTextureRegion.getTexture(), 0, 0, FRAME_WIDTH, FRAME_HEIGHT, 0, 0, 1, 1);
        if (!instance.GetTexts().isEmpty() || !instance.GetStaticFonts().isEmpty()) {
            // Draw text objects
            batch.setShader(fontShader);
            for (Text t : instance.GetTexts().iterator()) {
                if (Float.compare(font.getData().scaleX, t.Scale) != 0)
                    font.getData().setScale(t.Scale);
                font.draw(batch, t.Str, t.X, t.Y);
            }

            for (BitmapFontCache k : instance.GetStaticFonts().iterator())
                k.draw(batch);

            batch.setShader(null);
        }
        batch.end();

    }

    private void renderShapes(OrthographicCamera camera, Box2DDebugRenderer debugRenderer, World physicWorld) {
        debugRenderer.render(physicWorld, camera.combined);
    }

    private final Color col = new Color(255f, 0,0,0.5f);

    private void loadBackgrounds(AssetManager assetManager) {
        TextureRegion greenBG = assetManager.GetTextures().get(indexT.BG_GREEN);
        TextureRegion redBG = assetManager.GetTextures().get(indexT.BG_RED);
        TextureRegion grayBG = assetManager.GetTextures().get(indexT.BG_GRAY);

        TextureRegion[] greenBgFrames = greenBG.split(1,1050)[0];


        TextureRegion[] redBgFrames = redBG.split(1,1050)[0];


        TextureRegion[] grayBgFrames = grayBG.split(1, 1050)[0];



        backgrounds[0] = greenBgFrames;
        backgrounds[1] = grayBgFrames;
        backgrounds[2] = redBgFrames;

    }
    public void SetBackground(int id, float frameDuration) {
        this.background = new Animation<>(frameDuration ,backgrounds[id]);
    }
    private void loadShaders() {
        final String vertexShader =
                "attribute vec4 vertex_positions;\n" //
                        + "attribute vec4 quad_colors;\n" //
                        + "attribute float s;\n"
                        + "uniform mat4 u_projTrans;\n" //
                        + "varying vec4 v_color;\n" //
                        + "varying vec2 vTexCoord;\n"
                        + "void main()\n" //
                        + "{\n" //
                        + "   v_color = s * quad_colors;\n" //
                        + "   gl_Position =  u_projTrans * vertex_positions;\n" //
                        + "}\n";
        final String fragmentShader = "#ifdef GL_ES\n" //
                + "precision lowp float;\n" //
                + "#define MED mediump\n"
                + "#else\n"
                + "#define MED \n"
                + "#endif\n" //
                + "varying vec4 v_color;\n" //
                + "uniform sampler2D u_normals;\n" //
                + "uniform vec3 u_lightpos;\n" //
                + "uniform vec2 u_resolution;\n" //
                + "uniform float u_intensity = 1.0;\n" +
                "uniform vec3 u_tint = vec3(1.0, 1.0, 1.0);\n" +
                "// New uniform for tweaking normal map strength\n" +
                "uniform float u_normalStrength = 1.0;" //
                + "void main() {\n"//
                + "    // Screen position in normalized coordinates ([0, 1])\n" +
                "    vec2 screenPos = gl_FragCoord.xy / u_resolution.xy;\n" +
                "\n" +
                "    // Get the normal map at this position\n" +
                "    vec3 NormalMap = texture2D(u_normals, screenPos).rgb;\n" +
                "\n" +
                "    // Detect if there’s something in the normal map (non-zero values)\n" +
                "    float normalMapStrength = length(NormalMap);\n" +
                "\n" +
                "    // Calculate the light direction\n" +
                "    vec3 LightDir = vec3(vec2(u_lightpos.x, u_lightpos.y) - screenPos, u_lightpos.z);\n" +
                "\n" +
                "// Normalize the normals and light direction\n" +
                "vec3 N = normalize(NormalMap * 2.0 - 1.0); // Normal map transformed to [-1, 1]\n" +
                "vec3 L = normalize(LightDir);\n" +
                "\n" +
                "// Calculate the dot product to determine base light intensity\n" +
                "float baseLight = pow(max(dot(N, L), 0.0), 0.5); // Reduce light dominance\n" +
                "float normalFactor = mix(0.5, baseLight, normalMapStrength * u_normalStrength); " +
                "\n" +
                "// Lighting effect based on normals and blend it with u_tint\n" +
                "vec3 lightEffect = v_color.rgb * normalFactor * u_intensity * u_tint;\n" +
                "\n" +
                "// Blend the base color (v_color) with the light effect based on normal strength\n" +
                "vec3 finalColor = mix(v_color.rgb, lightEffect, normalMapStrength);\n" +
                "\n" +
                "// Output the final color with the alpha from v_color\n" +
                "gl_FragColor = vec4(finalColor, v_color.a);" //
                + "}";

        ShaderProgram.pedantic = false;
        ShaderProgram lightShader = new ShaderProgram(vertexShader,
                fragmentShader);
        if (!lightShader.isCompiled()) {
            Gdx.app.log("ERROR", lightShader.getLog());
        }

        lightShader.begin();
        lightShader.setUniformi("u_normals", 1);
        lightShader.setUniformf("u_resolution", FRAME_WIDTH, FRAME_HEIGHT);
        lightShader.setUniformf("u_tint", 1, 1, 1);
        lightShader.setUniformf("u_normalStrength", 1.2f);
        lightShader.end();


        shaders.put(Shaders.LIGHT, lightShader);
    }

    public SpriteBatch GetSpriteBatch() {
        return batch;
    }
    public NormalBatch GetNormalBatch() {
        return normalBatch;
    }
    public FrameBuffer GetNormalBuffer() {
        return normalBuffer;
    }
    public OrthographicCamera GetCamera() {
        return worldCamera;
    }
    public FitViewport GetViewport() {
        return viewport;
    }
    public FrameBuffer GetScreenBuffer() {
        return screenBuffer;
    }
    public RayHandler GetRayHandler() {
        return rayHandler;
    }

    public ObjectMap<Light, LightHolderComponent.LightObject> GetLights() {
        return lights;
    }
    public void Dispose() {
        batch.dispose();
        screenBuffer.dispose();
        font.dispose();
        fontShader.dispose();
        debugRenderer.dispose();
        normalBatch.dispose();
        rayHandler.dispose();
    }
}
