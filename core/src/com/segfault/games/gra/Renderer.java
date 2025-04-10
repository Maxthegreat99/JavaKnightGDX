package com.segfault.games.gra;

import box2dLight.Light;
import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
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

    private final FrameBuffer[] chainFBO = new FrameBuffer[2];

    private final TextureRegion[] chainTexReg = new TextureRegion[2];

    public static final float PIXEL_TO_METERS = 60f;
    private static final Vector3 screenCoord = new Vector3();
    private static final Color ambientColor = new Color(0.15f, 0.15f, 0.15f, 0.4f);

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
        chainFBO[0] = new FrameBuffer(Pixmap.Format.RGBA8888, frameWidth, frameHeight, false);
        chainFBO[1] = new FrameBuffer(Pixmap.Format.RGBA8888, frameWidth, frameHeight, false);

        chainTexReg[0] = new TextureRegion(chainFBO[0].getColorBufferTexture());
        chainTexReg[1] = new TextureRegion(chainFBO[1].getColorBufferTexture());

        chainTexReg[0].flip(false, true);
        chainTexReg[1].flip(false, true);

        chainTexReg[0].getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        chainTexReg[1].getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

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
        rayHandler.setBlurNum(2);

        rayHandler.useCustomViewport(viewport.getScreenX(), viewport.getScreenY(), viewport.getScreenWidth(), viewport.getScreenHeight());

        batch.enableBlending();
        normalBatch.enableBlending();
    }

    /**
     * clears the screen,
     * @param timeElapsed, begins the buffers, sets up camera and draws the background
     */
    public void SetUpFrame(float timeElapsed) {

        // clear the screen
        Gdx.gl.glClearColor(1f, 1f, 1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    }
    private final TextureRegion fboTextureRegion;
    private final TextureRegion normals;
    /**
     * deals with the rendering of text, debug boxes and renders the main texture from the framebuffer
     * @param instance
     * @param physicWorld
     */
    public void Render(JavaKnight instance, World physicWorld) {
/*
        screenBuffer.begin();
        batch.begin();

        renderShapes(physicsCamera, debugRenderer, physicWorld);

        batch.end();
        screenBuffer.end();
*/

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
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        batch.draw(fboTextureRegion, 0, 0);
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
                "vec3 premultiplied = finalColor * v_color.a;" +
                "// Output the final color with the alpha from v_color\n" +
                "gl_FragColor = vec4(premultiplied, 1);" //
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


        final String fireParticleVertex = "#ifdef GL_ES\n" +
                "precision mediump float;\n" +
                "#endif\n" +
                "\n" +
                "attribute vec4 a_position;\n" +
                 "attribute vec2 " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" +
                "uniform mat4 u_projTrans;\n" +
                "uniform vec3 u_cameraPosition;\n" +
                "\n" +
                "varying vec2 v_texCoords;\n" +
                "varying float v_fresnel;\n" +
                "\n" +
                "void main() {\n" +
                "   v_texCoords = " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n"  +
                "\n" +
                "    // Fake Fresnel effect\n" +
                "    vec3 worldPos = (u_projTrans * a_position).xyz; \n" +
                "    vec3 viewDir = normalize(u_cameraPosition - worldPos);\n" +
                "    vec3 normal = vec3(0.0, 0.0, 1.0);\n" +
                "\n" +
                "    v_fresnel = pow(1.0 - dot(viewDir, normal), 1.0);\n" +
                "\n" +
                "    gl_Position = u_projTrans * a_position;\n" +
                "}";

        final String fireParticleFragement = "#ifdef GL_ES\n" +
                "precision mediump float;\n" +
                "#endif\n" +
                "\n" +
                "uniform sampler2D u_texture;\n" +
                "uniform float u_alphaThreshold;\n" +
                "uniform sampler2D u_gradient;\n" +
                "uniform float u_fresnelPower;\n" +
                "uniform float u_voronoiScale;\n" +
                "uniform float u_time;\n" +
                "uniform float u_voronoiSpeed;\n" +
                "uniform float u_startUgradient;\n" +
                "uniform float u_startVgradient;\n" +
                "uniform float u_gradientWidth;\n" +
                "\n" +
                "varying vec2 v_texCoords;\n" +
                "varying float v_fresnel;\n" +
                "\n" +
                "// Hash function for randomness\n" +
                "vec2 hash(vec2 p) {\n" +
                "    p = vec2(dot(p, vec2(127.1, 311.7)), dot(p, vec2(269.5, 183.3)));\n" +
                "    return fract(sin(p) * 43758.5453);\n" +
                "}\n" +
                "\n" +
                "// Voronoi noise function\n" +
                "float voronoi(vec2 uv) {\n" +
                "    uv *= u_voronoiScale;\n" +
                "    vec2 i_uv = floor(uv);\n" +
                "    vec2 f_uv = fract(uv);\n" +
                "\n" +
                "    float minDist = 1.0;\n" +
                "    \n" +
                "    for (int y = -1; y <= 1; y++) {\n" +
                "        for (int x = -1; x <= 1; x++) {\n" +
                "            vec2 neighbor = vec2(float(x), float(y));\n" +
                "            vec2 point = hash(i_uv + neighbor);\n" +
                "            \n" +
                "            float angle = u_time * u_voronoiSpeed;\n" +
                "            point = 0.5 + 0.5 * vec2(cos(angle) * point.x - sin(angle) * point.y, \n" +
                "                                     sin(angle) * point.x + cos(angle) * point.y);\n" +
                "            \n" +
                "            vec2 diff = neighbor + point - f_uv;\n" +
                "            float dist = length(diff);\n" +
                "            \n" +
                "            minDist = min(minDist, dist);\n" +
                "        }\n" +
                "    }\n" +
                "    \n" +
                "    return minDist;\n" +
                "}\n" +
                "\n" +
                "void main() {\n" +
                "    vec4 color = texture2D(u_texture, v_texCoords);\n" +
                "float luminance = dot(color.rgb, vec3(0.299, 0.587, 0.114));\n" +
                "\n" +
                "// Use luminance as alpha\n" +
                "   vec4 clr =  texture2D(u_gradient, vec2(u_startUgradient + u_gradientWidth * u_time, u_startVgradient));" +
                "float baseAlpha = luminance * clr.a;\n" +
                "\n" +
                "    \n" +
                "    // Voronoi noise in alpha channel\n" +
                "    float noise = voronoi(gl_FragCoord.xy);\n" +
                "" +
                "    \n" +
                "    // Alpha Clipping using Voronoi noise\n" +
                "    if (noise < u_alphaThreshold) discard; // Ensures cutout effect\n" +
                "\n" +
                "    // Fresnel effect\n" +
                "    float fresnelFactor = pow(v_fresnel, u_fresnelPower);\n" +
                "\n" +
                "    // Opaque blending (no transparency)\n" +
                "    vec3 finalColor = color.rgb * clr.rgb * (1.0 + fresnelFactor * 3.0);\n" +
                "    \n" +
                "    gl_FragColor = vec4(clr.rgb, baseAlpha); // Force full opacity\n" +
                "}";


        ShaderProgram fireParticles = new ShaderProgram(fireParticleVertex,
                fireParticleFragement);
        if (!fireParticles.isCompiled()) {
            Gdx.app.log("ERROR", fireParticles.getLog());
        }

        fireParticles.begin();
        fireParticles.setUniformi("u_gradient", 1);
        fireParticles.setUniformf("u_alphaThreshold", 0.5f);
        fireParticles.setUniformf("u_fresnelPower", 3.0f);
        fireParticles.setUniformf("u_color", 1.0f, 0.5f, 0.5f, 1.0f);
        fireParticles.setUniformf("u_voronoiScale", 10.0f);
        fireParticles.setUniformf("u_voronoiSpeed", 2.0f);
        fireParticles.end();

        shaders.put(Shaders.FIRE_PARTICLE, fireParticles);

        final String defaultVertex = "// default.vert\n" +
                "attribute vec4 a_position;\n" +
                "attribute vec2 a_texCoord0;\n" +
                "\n" +
                "varying vec2 tex_coord;\n" +
                "uniform mat4 u_projTrans;\n" +
                "\n" +
                "void main() {\n" +
                "    tex_coord = a_texCoord0;\n" +
                "    gl_Position = u_projTrans * a_position;\n" +
                "}";

        final String bloomFrag = "#ifdef GL_ES\n" +
                "precision mediump float;\n" +
                "#endif\n" +
                "\n" +
                "varying vec2 tex_coord;\n" +
                "\n" +
                "uniform sampler2D u_texture;\n" +
                "uniform vec2 u_resolution;  \n" +
                "uniform float bloom_spread;     \n" +
                "uniform float bloom_intensity;  \n" +
                "\n" +
                "void main() {\n" +
                "    vec2 texelSize = 1.0 / u_resolution;\n" +
                "    vec4 bloom = vec4(0.0);\n" +
                "\n" +
                "    for (int y = -4; y <= 4; ++y) {\n" +
                "        float offsetY = float(y) * bloom_spread * texelSize.y;\n" +
                "\n" +
                "        for (int x = -4; x <= 4; ++x) {\n" +
                "            float offsetX = float(x) * bloom_spread * texelSize.x;\n" +
                "            bloom += texture2D(u_texture, tex_coord + vec2(offsetX, offsetY));\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    bloom /= 81.0; // 9x9 samples\n" +
                "    vec4 original = texture2D(u_texture, tex_coord);\n" +
                "\n" +
                "    // Add the bloom glow onto the original texture\n" +
                "    gl_FragColor = vec4((original.rgb + bloom.rgb * bloom_intensity) / (1.0 + bloom_intensity), 1.0);\n" +
                "}\n";

        ShaderProgram bloomShader = new ShaderProgram(defaultVertex,
                bloomFrag);
        if (!bloomShader.isCompiled()) {
            Gdx.app.log("ERROR", bloomShader.getLog());
        }

        bloomShader.bind();
        bloomShader.setUniformi("u_texture", 0);
        bloomShader.end();

        shaders.put(Shaders.BLOOM_SHADER, bloomShader);


        final String fireCoreVert = "attribute vec4 a_position;\n" +
                "attribute vec2 a_texCoord0;\n" +
                "attribute vec4 a_color;\n" +
                "\n" +
                "uniform mat4 u_projTrans;\n" +
                "\n" +
                "varying vec2 v_texCoord;\n" +
                "varying vec4 v_color;\n" +
                "\n" +
                "void main() {\n" +
                "    v_texCoord = a_texCoord0;\n" +
                "    v_color = a_color;\n" +
                "    gl_Position = u_projTrans * a_position;\n" +
                "}\n";

        final String fireCoreFrag = "#ifdef GL_ES\n" +
                "precision mediump float;\n" +
                "#endif\n" +
                "\n" +
                "// Textures\n" +
                "uniform sampler2D u_mainTex;    // Main sprite shape\n" +
                "uniform sampler2D u_noiseTex;   // Base dissolve noise\n" +
                "uniform sampler2D u_distortTex; // Distortion noise\n" +
                "uniform sampler2D u_gradient;" +
                "\n" +
                "// UV input\n" +
                "varying vec2 v_texCoord;\n" +
                "varying vec4 v_color;\n" +
                "\n" +
                "// Uniforms\n" +
                "uniform float u_time;          // Time in seconds\n" +
                "uniform float u_scale;         // Noise scale\n" +
                "uniform float u_distortScale;  // Distortion scale\n" +
                "uniform float u_cutoff;        // Smoothstep edge\n" +
                "uniform float u_speed;         // Flow speed\n" +
                "uniform float u_brightness;    // Brightness boost\n" +
                "uniform float u_edgeWidth;     // Width of edge highlight\n" +
                "uniform float u_particleAge;   // From 0 to 1\n" +
                "uniform float u_particleShift; // Shift in age cutoff\n" +
                "uniform float u_stretch;       // Y distortion stretch\n" +
                "uniform bool u_multiply;       // Multiply or add noise\n" +
                "uniform float u_startUgradient;\n" +
                "uniform float u_startVgradient;\n" +
                "uniform float u_gradientWidth;\n" +
                "uniform float u_startUnoise;" +
                "uniform float u_startVnoise;" +
                "uniform float u_heightNoise;" +
                "uniform float u_widthNoise;" +
                "uniform float u_startUdistort;" +
                "uniform float u_startVdistort;" +
                "uniform float u_heightDistort;" +
                "uniform float u_widthDistort;" +
                "uniform float u_mainTexWidth;" +
                "uniform float u_mainTexHeight;" +
                "uniform float u_mainStartU;" +
                "uniform float u_mainStartV;" +
                "\n" +
                "uniform vec4 u_tint;       // Main tint\n" +
                "uniform vec4 u_edgeColor;  // Edge color\n" +
                "\n" +
                "void main()\n" +
                "{\n" +
                "    vec2 uv = v_texCoord;\n" +
                "\n" +
                "    // Simulate vertical scrolling noise\n" +
                "    // Step 1: Normalize UV to [0,1] within the subregion\n" +
                "vec2 noiseUV = (uv - vec2(u_mainStartU, u_mainStartV)) / vec2(u_mainTexWidth, u_mainTexHeight);\n" +
                "\n" +
                "// Step 2: Scroll and stretch vertically\n" +
                "noiseUV.y = (noiseUV.y - u_time * u_speed) * u_stretch * u_scale;\n" +
                "\n" +
                "// Step 3: Wrap Y back into [0,1] to avoid going out of bounds\n" +
                "noiseUV.y = mod(noiseUV.y, 1.0);\n" +
                "noiseUV.x = mod(noiseUV.x * u_scale, 1.0);" +
                "\n" +
                "// Step 4: Remap UV back to the subregion on the full texture\n" +
                "vec2 sampleUV = vec2(u_startUnoise, u_startVnoise) + noiseUV * vec2(u_widthNoise, u_heightNoise);\n" +
                "\n" +
                "\n" +
                "    vec2 distortUV = (uv - vec2(u_mainStartU, u_mainStartV)) / vec2(u_mainTexWidth, u_mainTexHeight);\n" +
                "    distortUV.y = (distortUV.y - u_time * u_speed);\n" +
                "    distortUV.y = mod(distortUV.y * u_distortScale, 1.0);" +
                "   distortUV.x = mod(distortUV.x * u_distortScale, 1.0);" +
                "    vec2 sampleDistortUV = vec2(u_startUdistort, u_startVdistort) + distortUV * vec2(u_widthDistort, u_heightDistort);" +
                "\n" +
                "    // Sample distort noise\n" +
                "    float distort = texture2D(u_distortTex, sampleDistortUV).r;\n" +
                "\n" +
                "    // Sample base noise\n" +
                "    float baseNoise = texture2D(u_noiseTex, sampleUV).r;\n" +
                "\n" +
                "    // Combine them\n" +
                "    float finalNoise = u_multiply ? (baseNoise * distort * 2.0) : ((baseNoise + distort) * 2.0);\n" +
                "\n" +
                "    // Sample main shape\n" +
                "    float shapeAlpha = dot(texture2D(u_mainTex, uv).rgb, vec3(0.2126, 0.7152, 0.0722));\n" +
                "\n" +
                "    // Dissolve factor based on age\n" +
                "    float age = u_particleAge - u_particleShift;\n" +
                "    float dis" +
                "solve = smoothstep(age - u_cutoff, age, finalNoise * shapeAlpha);\n" +
                "\n" +
                "    // Edge highlight\n" +
                "    float edge = dissolve * step(finalNoise * shapeAlpha, age + u_edgeWidth) * shapeAlpha;\n" +
                "\n" +
                "    // Final color\n" +
                "    vec4 result = vec4(0.0);\n" +
                "    result.rgb = u_tint.rgb * dissolve * shapeAlpha;\n" +
                "    result.rgb += result.rgb * u_brightness;\n" +
                "\n" +
                "    // Remove edge from main color and add it as glow\n" +
                "    result.rgb -= edge * u_tint.rgb;\n" +
                "    result.rgb += edge * u_edgeColor.rgb;\n" +
                "\n" +
                "    result.a = dissolve * shapeAlpha;\n" +
                "    result *= texture2D(u_gradient, vec2(u_startUgradient + u_gradientWidth * u_particleAge, u_startVgradient)); // Per-vertex tint (optional)\n" +
                "\n" +
                "    gl_FragColor = result;\n" +
                "}";



        ShaderProgram fireCoreShader = new ShaderProgram(fireCoreVert,
                fireCoreFrag);
        if (!fireCoreShader.isCompiled()) {
            Gdx.app.log("ERROR", fireCoreShader.getLog());
        }

        fireCoreShader.bind();
        fireCoreShader.setUniformi("u_mainTex", 0);
        fireCoreShader.setUniformi("u_noiseTex", 1);
        fireCoreShader.setUniformi("u_distortTex", 2);
        fireCoreShader.setUniformi("u_gradient", 3);
        fireCoreShader.end();

        shaders.put(Shaders.FIRE_CORE, fireCoreShader);



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
    public OrthographicCamera GetWorldCamera() {
        return worldCamera;
    }
    public OrthographicCamera GetScreenCamera() {
        return screenCamera;
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
    public ObjectMap<Shaders, ShaderProgram> GetShaders() {
        return shaders;
    }

    /**
     * gives the engine's chained framebuffer object for applying multiple shader
     * to a buffer
     */
    public FrameBuffer[] GetChainFBO()
    {
        return chainFBO;
    }

    /**
     * gives the engine's chainedFBO tex regions
     */
    public TextureRegion[] GetChainTexReg() {
        return chainTexReg;
    }

    public void UpdateCameras() {

        if (Float.compare(CameraZoom, worldCamera.zoom) != 0 || UpdateCamera) {
            worldCamera.zoom = CameraZoom;
            worldCamera.position.set(CameraPos.x + ScreenTranslationX,
                    CameraPos.y + ScreenTranslationY, 0);
            physicsCamera.position.set(CameraPos.x / PIXEL_TO_METERS + ScreenTranslationX / PIXEL_TO_METERS,
                    CameraPos.y / PIXEL_TO_METERS + ScreenTranslationY / PIXEL_TO_METERS, 0);
            worldCamera.update();
            physicsCamera.update();
            UpdateCamera = false;
        }

        batch.setProjectionMatrix(worldCamera.projection);
        batch.setTransformMatrix(worldCamera.view);

        normalBatch.setProjectionMatrix(worldCamera.projection);
        normalBatch.setTransformMatrix(worldCamera.view);

        viewport.update(SCREEN_WIDTH, SCREEN_HEIGHT);

    }

    public ObjectMap<Light, LightHolderComponent.LightObject> GetLights() {
        return lights;
    }
    public void Dispose() {
        batch.dispose();
        screenBuffer.dispose();
        chainFBO[0].dispose();
        chainFBO[1].dispose();
        normalBuffer.dispose();
        font.dispose();
        fontShader.dispose();
        debugRenderer.dispose();
        normalBatch.dispose();
        rayHandler.dispose();
    }
}
