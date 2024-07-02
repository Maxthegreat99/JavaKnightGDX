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
import com.segfault.games.util.AssetManager;
import com.segfault.games.obj.Rec;
import com.segfault.games.obj.Text;
import com.segfault.games.obj.ent.EntityManager;
import com.segfault.games.util.indexT;

public class JavaKnight extends ApplicationAdapter {

	private SpriteBatch batch;
	public BitmapFont font; // Font the whole game is gonna use
	public ObjectMap<BitmapFontCache, Float> StaticFonts = new ObjectMap<>();
	// Game window dimensions
	private AssetManager assetManager; // asset manager instance, gets passed classes for use of assets
	public final float WORLD_SCALE = 3f;
	public final int SCREEN_WIDTH = 1680;
	public final int SCREEN_HEIGHT = 1050;
	public final int FRAME_WIDTH = 616; // Frame buffer width
	public final int FRAME_HEIGHT = 385; // Frame buffer height

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

		loadAssets();
	}

	private void loadAssets() {
		assetManager.Textures[indexT.ONE_PARTICLE_1.ordinal()] =  atlas.findRegion("1");
		assetManager.Textures[indexT.ONE_PARTICLE_2.ordinal()] =  atlas.findRegion("2");
		assetManager.Textures[indexT.ZERO_PARTICLE_1.ordinal()] =  atlas.findRegion("3");
		assetManager.Textures[indexT.ZERO_PARTICLE_2.ordinal()] =  atlas.findRegion("4");
		assetManager.Textures[indexT.ACID_PARTICLE.ordinal()] =  atlas.findRegion("acidParticle");
		assetManager.Textures[indexT.BOSS_BAR_OUTLINE.ordinal()] =  atlas.findRegion("barOutline");
		assetManager.Textures[indexT.ACID_BEAM.ordinal()] =  atlas.findRegion("beam");
		assetManager.Textures[indexT.ACID_BEAM_BALL.ordinal()] =  atlas.findRegion("beamBall");
		assetManager.Textures[indexT.BOSS.ordinal()] =  atlas.findRegion("boss");
		assetManager.Textures[indexT.BOSS_BAR_FILLING.ordinal()] =  atlas.findRegion("bossBarCol");
		assetManager.Textures[indexT.BOSS_DEAD.ordinal()] =  atlas.findRegion("bossDead");
		assetManager.Textures[indexT.BOSS_HIT.ordinal()] =  atlas.findRegion("bossHit");
		assetManager.Textures[indexT.BOUNCY.ordinal()] =  atlas.findRegion("bouncy");
		assetManager.Textures[indexT.BOUNCY_GUN.ordinal()] =  atlas.findRegion("bouncyGun");
		assetManager.Textures[indexT.BULLET.ordinal()] =  atlas.findRegion("bullet");
		assetManager.Textures[indexT.NORMAL_DOOR.ordinal()] =  atlas.findRegion("door");
		assetManager.Textures[indexT.INFINITE_DOOR.ordinal()] =  atlas.findRegion("door1");
		assetManager.Textures[indexT.QUIT_DOOR.ordinal()] =  atlas.findRegion("door2");
		assetManager.Textures[indexT.FIRE_PARTICLE.ordinal()] =  atlas.findRegion("fireParticle");
		assetManager.Textures[indexT.FLOOR_PARTICLE.ordinal()] =  atlas.findRegion("floorParticle");
		assetManager.Textures[indexT.GUN.ordinal()] =  atlas.findRegion("gun");
		assetManager.Textures[indexT.GUNNER.ordinal()] =  atlas.findRegion("gunner");
		assetManager.Textures[indexT.LAZER_GUN.ordinal()] =  atlas.findRegion("lazerGun");
		assetManager.Textures[indexT.PLAYER.ordinal()] =  atlas.findRegion("player");
		assetManager.Textures[indexT.ROCKET.ordinal()] =  atlas.findRegion("rocket");
		assetManager.Textures[indexT.ROCKET_GUN.ordinal()] =  atlas.findRegion("rocketGun");
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
		for (Text t : Texts) {

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

		Texts.clear();
		StaticFonts.forEach(i -> i.key.clear());
		StaticFonts.clear();
		Rectangles.clear();

	}
}
