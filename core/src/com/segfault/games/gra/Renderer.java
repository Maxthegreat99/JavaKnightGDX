package com.segfault.games.gra;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ObjectMap;
import com.segfault.games.JavaKnight;
import com.segfault.games.obj.Text;
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

    private Animation<TextureRegion> background;
    private final SpriteBatch batch;
    private final OrthographicCamera worldCamera;
    private final OrthographicCamera screenCamera;
    private final OrthographicCamera physicsCamera;
    private final FrameBuffer screenBuffer;

    private final ShaderProgram fontShader;
    private final BitmapFont font;
    private final Box2DDebugRenderer debugRenderer;
    public float CameraZoom;
    private final Vector3 cameraPos = new Vector3();
    private final TextureRegion[][] backgrounds = new TextureRegion[3][];

    private final int SCREEN_WIDTH;
    private final int SCREEN_HEIGHT;

    private final int FRAME_WIDTH;
    private final int FRAME_HEIGHT;

    public boolean UpdateCamera = false;

    public static final float PIXEL_TO_METERS = 60f;

    public Renderer(AssetManager assetManager, int screenWidth, int screenHeight, int frameWidth, int frameHeight, float zoom) {
        SCREEN_HEIGHT = screenHeight;
        SCREEN_WIDTH = screenWidth;
        FRAME_HEIGHT = frameHeight;
        FRAME_WIDTH = frameWidth;

        CameraZoom = zoom;

        batch = new SpriteBatch();
        screenCamera = new OrthographicCamera(screenWidth, screenHeight);
        worldCamera = new OrthographicCamera(frameWidth, frameHeight);
        physicsCamera = new OrthographicCamera(FRAME_WIDTH / PIXEL_TO_METERS, FRAME_HEIGHT / PIXEL_TO_METERS);

        screenBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, frameWidth, frameHeight, false);

        font = new BitmapFont(Gdx.files.internal("DisposableDroidBB.fnt"), new TextureRegion(assetManager.GetFontTexture()), false);
        fontShader = new ShaderProgram(Gdx.files.internal("Shaders/font.vert"), Gdx.files.internal("Shaders/font.frag"));
        if (!fontShader.isCompiled()) Gdx.app.error("fontShader", "compilation failed:\n" + fontShader.getLog());

        debugRenderer = new Box2DDebugRenderer();


        cameraPos.set((float) SCREEN_WIDTH / 2f,
                (float) SCREEN_HEIGHT / 2f, 0);
        screenCamera.position.set(cameraPos);

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

    }

    /**
     * clears the screen,
     * @param timeElapsed, begins the buffers, sets up camera and draws the background
     */
    public void SetUpFrame(float timeElapsed) {

        screenBuffer.begin();
        batch.setProjectionMatrix(worldCamera.combined);
        batch.begin();

        // clear the screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.draw(background.getKeyFrame(timeElapsed, true), 0, 0, FRAME_WIDTH, FRAME_HEIGHT);

    }
    private final TextureRegion fboTextureRegion;

    /**
     * deals with the rendering of text, debug boxes and renders the main texture from the framebuffer
     * @param instance
     * @param physicWorld
     */
    public void Render(JavaKnight instance, World physicWorld) {


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


        renderShapes(physicsCamera, debugRenderer, physicWorld);

        batch.end();
        screenBuffer.end();

        if (Float.compare(CameraZoom, screenCamera.zoom) != 0 || UpdateCamera) {
            screenCamera.zoom = CameraZoom;
            screenCamera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);
            cameraPos.set((float) SCREEN_WIDTH / 2f + ScreenTranslationX,
                    (float) SCREEN_HEIGHT / 2f + ScreenTranslationY, 0);
            screenCamera.position.set(cameraPos);
            screenCamera.update();
            UpdateCamera = false;
        }

        batch.setProjectionMatrix(screenCamera.combined);

        batch.begin();
        batch.draw(fboTextureRegion.getTexture(), 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, 0, 0, 1, 1);
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

    public SpriteBatch GetSpriteBatch() {
        return batch;
    }
    public OrthographicCamera GetCamera() {
        return worldCamera;
    }
    public void Dispose() {
        batch.dispose();
        screenBuffer.dispose();
        font.dispose();
        fontShader.dispose();
        debugRenderer.dispose();
    }
}
