package com.segfault.games;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.dongbat.jbump.World;
import com.segfault.games.obj.comp.CollidesComponent;
import com.segfault.games.obj.sys.MovementSystem;
import com.segfault.games.util.AssetManager;
import com.segfault.games.obj.Rec;
import com.segfault.games.obj.Text;
import com.segfault.games.util.EntityManager;

public class JavaKnight extends ApplicationAdapter {

	private SpriteBatch batch;
	public BitmapFont font; // Font the whole game is gonna use
	public ObjectMap<BitmapFontCache, Float> StaticFonts = new ObjectMap<>();
	// Game window dimensions
	private AssetManager assetManager; // asset manager instance, gets passed classes for use of assets
	public final int SCREEN_WIDTH = 1680;
	public final int SCREEN_HEIGHT = 1050;
	public final int FRAME_WIDTH = 616; // Frame buffer width
	public final int FRAME_HEIGHT = 385; // Frame buffer height

	public final Array<Object> LayerOne = new Array<>();
	public final Array<Object> LayerTwo = new Array<>();
	public final Array<Object> LayerThree = new Array<>();
	public final Array<Object> LayerFour = new Array<>();
	public final Array<Object> LayerUI = new Array<>();

	// Only used to render rectangles that are on screen for debug
	public final Array<Rec> Rectangles = new Array<>();
	// List of text to render, all use the same font
	public final Array<Text> Texts = new Array<>();
	// screen buffer instance
	private FrameBuffer screenBuffer;
	// Texture region of the screenBuffer
	private TextureRegion fboTextureRegion;
	// Texture atlas, stores most of the game's textures
	private TextureAtlas atlas;
	// Shader used for drawing texts
	private ShaderProgram fontShader;
	// texture used for drawing text
	private Texture fontTexture;
	//public final Player Plr;

	// Physics world instance
	public World<Entity> PhysicWorld;
	public OrthographicCamera camera;
	public long ticks = 0;

	public float zoom = 0.90909f;
	private final Vector3 cameraPos = new Vector3();
	// ECS engine instance, used to store entities, components and systems
	public PooledEngine PooledECS;
	public EntityManager EntityManager;
	@Override
	public void create () {
		EntityManager = new EntityManager();
		PooledECS = new PooledEngine();
		PhysicWorld = new World<>();

		batch = new SpriteBatch();

		assetManager = new AssetManager();
		atlas = new TextureAtlas(Gdx.files.internal("JavaKnight.atlas"));

		// load fonts
		fontTexture = new Texture(Gdx.files.internal("DisposableDroidBB.png"), true); // true enables mipmaps
		fontTexture.setFilter(Texture.TextureFilter.MipMapLinearNearest, Texture.TextureFilter.Linear); // linear filtering in nearest mipmap image
		font = new BitmapFont(Gdx.files.internal("DisposableDroidBB.fnt"), new TextureRegion(fontTexture), false);

		fontShader = new ShaderProgram(Gdx.files.internal("font.vert"), Gdx.files.internal("font.frag"));
		if (!fontShader.isCompiled()) Gdx.app.error("fontShader", "compilation failed:\n" + fontShader.getLog());

		screenBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, FRAME_WIDTH, FRAME_HEIGHT, false);
		camera = new OrthographicCamera(SCREEN_WIDTH, SCREEN_HEIGHT);
		camera.position.set(SCREEN_WIDTH / 2f, SCREEN_HEIGHT / 2f, 0);
		camera.update();

	}

	@Override
	public void render () {
		ticks++;

		// Set the viewport to the framebuffer size
		camera.setToOrtho(false, FRAME_WIDTH, FRAME_HEIGHT);
		camera.zoom = 1f;
		camera.update();

		// set batch to render to the screen buffer
		screenBuffer.begin();
		batch.setProjectionMatrix(camera.combined);

		// clear the screen
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();

		// Draw text objects
		batch.setShader(fontShader);
		for (int i = 0; i < Texts.size; i++) {
			Text t = Texts.get(i);
			font.getData().setScale(t.Scale);
			font.draw(batch, t.Str, t.X, t.Y);
		}

		ObjectMap.Keys<BitmapFontCache> kset = StaticFonts.keys();
		for (BitmapFontCache k : kset) {
			float scale = StaticFonts.get(k);
			k.getFont().getData().setScale(scale);
			k.draw(batch);
		}
		batch.setShader(null);

		batch.end();
		screenBuffer.end();



		// Render the frame buffer to the screen
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// scale the camera up again
		camera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);
		camera.zoom = zoom;
		cameraPos.set((float) SCREEN_WIDTH / 2f + ((SCREEN_WIDTH * (1f - zoom)) / 2f),
				      (float) SCREEN_HEIGHT / 2f + (SCREEN_HEIGHT * (1f - zoom)) / 2f, 0 );

		camera.position.set(cameraPos);
		camera.update();
		batch.setProjectionMatrix(camera.combined);

		if (fboTextureRegion == null) {
			Texture sboTexture = screenBuffer.getColorBufferTexture();
			sboTexture.setFilter(Texture.TextureFilter.Linear,
					Texture.TextureFilter.Nearest);
			fboTextureRegion = new TextureRegion(sboTexture);
			fboTextureRegion.flip(false, true);
		}

		batch.begin();
		batch.draw(fboTextureRegion.getTexture(), 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, 0, 0, 1, 1);
		batch.end();


	}
	
	@Override
	public void dispose () {
		batch.dispose();
		atlas.dispose();
		font.dispose();
		fontTexture.dispose();
		fontShader.dispose();

		screenBuffer.dispose();

		LayerOne.clear();
		LayerTwo.clear();
		LayerThree.clear();
		LayerFour.clear();
		LayerUI.clear();
		Texts.clear();
		StaticFonts.forEach(i -> i.key.clear());
		StaticFonts.clear();
		Rectangles.clear();

	}
}
