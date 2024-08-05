package com.segfault.games.gra;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.dongbat.jbump.Rect;
import com.dongbat.jbump.World;
import com.segfault.games.JavaKnight;
import com.segfault.games.obj.Rec;
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
    public float screenTranslationX = 0;
    public float screenTranslationY = 0;

    private Animation<TextureRegion> background;
    private final SpriteBatch batch;
    private final OrthographicCamera camera;
    private final FrameBuffer screenBuffer;

    private final ShaderProgram fontShader;
    private final BitmapFont font;
    private final ShapeRenderer shapeRenderer;
    public float cameraZoom;
    private final Vector3 cameraPos = new Vector3();
    private final TextureRegion[][] backgrounds = new TextureRegion[3][];

    private final int SCREEN_WIDTH;
    private final int SCREEN_HEIGHT;

    private final int FRAME_WIDTH;
    private final int FRAME_HEIGHT;

    public Renderer(AssetManager assetManager, int screenWidth, int screenHeight, int frameWidth, int frameHeight, float zoom) {
        SCREEN_HEIGHT = screenHeight;
        SCREEN_WIDTH = screenWidth;
        FRAME_HEIGHT = frameHeight;
        FRAME_WIDTH = frameWidth;

        cameraZoom = zoom;

        batch = new SpriteBatch();
        camera = new OrthographicCamera(screenWidth, screenHeight);
        screenBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, frameWidth, frameHeight, false);

        font = new BitmapFont(Gdx.files.internal("DisposableDroidBB.fnt"), new TextureRegion(assetManager.GetFontTexture()), false);
        fontShader = new ShaderProgram(Gdx.files.internal("Shaders/font.vert"), Gdx.files.internal("Shaders/font.frag"));
        if (!fontShader.isCompiled()) Gdx.app.error("fontShader", "compilation failed:\n" + fontShader.getLog());

        shapeRenderer = new ShapeRenderer();


        cameraPos.set((float) SCREEN_WIDTH / 2f,
                (float) SCREEN_HEIGHT / 2f , 0 );
        camera.position.set(cameraPos);
        camera.update();

        loadBackgrounds(assetManager);
    }

    /**
     * clears the screen,
     * @param timeElapsed, begins the buffers, sets up camera and draws the background
     */
    public void SetUpFrame(float timeElapsed) {
        // Set the viewport to the framebuffer size
        camera.zoom = 1f;
        camera.setToOrtho(false, FRAME_WIDTH, FRAME_HEIGHT);

        screenBuffer.begin();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // clear the screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.disableBlending();
        batch.draw(background.getKeyFrame(timeElapsed, true), 0, 0, FRAME_WIDTH, FRAME_HEIGHT);
        batch.enableBlending();

    }
    private TextureRegion fboTextureRegion;

    /**
     * deals with the rendering of text, debug boxes and renders the main texture from the framebuffer
     * @param instance
     * @param physicWorld
     */
    public void Render(JavaKnight instance, World<Entity> physicWorld) {

        if (!instance.GetTexts().isEmpty() || !instance.GetStaticFonts().isEmpty()) {
            // Draw text objects
            batch.setShader(fontShader);
            for (Text t : instance.GetTexts()) {
                font.getData().setScale(t.Scale);
                font.draw(batch, t.Str, t.X, t.Y);
            }

            for (BitmapFontCache k : instance.GetStaticFonts().keys()) {
                float scale = instance.GetStaticFonts().get(k);
                k.getFont().getData().setScale(scale);
                k.draw(batch);
            }
            batch.setShader(null);
        }
        //debugBoxes(instance.GetRectangles(), physicWorld);

        batch.end();
        screenBuffer.end();

        camera.zoom = cameraZoom;

        camera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);
        cameraPos.set((float) SCREEN_WIDTH / 2f + screenTranslationX,
                (float) SCREEN_HEIGHT / 2f + screenTranslationY, 0 );
        camera.position.set(cameraPos);
        camera.update();

        batch.setProjectionMatrix(camera.combined);

        if (fboTextureRegion == null) {
            Texture sboTexture = screenBuffer.getColorBufferTexture();
            sboTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Nearest);
            fboTextureRegion = new TextureRegion(sboTexture);
            fboTextureRegion.flip(false, true);
        }

        batch.begin();
        batch.draw(fboTextureRegion.getTexture(), 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, 0, 0, 1, 1);
        batch.end();
    }

    private final Color col = new Color(255f, 0,0,0.5f);
    private void debugBoxes(Array<Rec> rectangles, World<Entity> physicWorld) {
        // Renders collisions
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(col);
        shapeRenderer.setProjectionMatrix(camera.combined);

        for (Rec r : rectangles){
            shapeRenderer.polygon(r.ConvertToFloatArray());
        }
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Rect i : physicWorld.getRects()) shapeRenderer.rect(i.x, i.y, i.w, i.h);
        shapeRenderer.end();
    }


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
        return camera;
    }
    public void Dispose() {
        batch.dispose();
        screenBuffer.dispose();
        font.dispose();
        fontShader.dispose();
        shapeRenderer.dispose();
    }
}
