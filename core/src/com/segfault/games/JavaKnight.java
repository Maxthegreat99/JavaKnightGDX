package com.segfault.games;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.dongbat.jbump.*;
import com.segfault.games.obj.comp.*;
import com.segfault.games.obj.ent.EntityCreator;
import com.segfault.games.obj.ent.EntityID;
import com.segfault.games.obj.ent.EntityListener;
import com.segfault.games.obj.sys.*;
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
	public EntityCreator EntityCreator;
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
	public Entity Plr;

	// Physics world instance
	public World<Entity> PhysicWorld;
	public OrthographicCamera camera;
	public long ticks = 0;

	public float zoom = 0.90909f;
	private final Vector3 cameraPos = new Vector3();
	// ECS engine instance, used to store entities, components and systems
	public PooledEngine PooledECS;
	public EntityManager EntityManager;
	private ShapeRenderer shapeRenderer;
	private Animation<TextureRegion> background;
	private final TextureRegion[][] backgrounds = new TextureRegion[3][];
	public float timeElapsed = 0.0f;
	private final Text FPS = new Text();
	@Override
	public void create () {
		EntityManager = new EntityManager();
		PooledECS = new PooledEngine();
		PhysicWorld = new World<>();
		EntityCreator = new EntityCreator(this);
		shapeRenderer = new ShapeRenderer();

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
		loadEntityEngine();
		loadPlayer();
		Plr = EntityCreator.spawnEntity(EntityID.PLAYER);
		loadEntities();
		loadBackgrounds();
		background = new Animation<>(0.045f, backgrounds[0]);

		FPS.X = 30;
		FPS.Y = FRAME_HEIGHT - 50;
		Texts.add(FPS);
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
		assetManager.Textures[indexT.BG_GREEN.ordinal()] = atlas.findRegion("greenBG");
		assetManager.Textures[indexT.BG_RED.ordinal()] = atlas.findRegion("redBG");
		assetManager.Textures[indexT.BG_GRAY.ordinal()] = atlas.findRegion("grayBG");
	}

	private void loadEntityEngine() {
		PooledECS.addEntityListener(new EntityListener(this));

		PooledECS.addSystem(new LifetimeSystem(this, 1));
		PooledECS.addSystem(new CooldownSystem(this, 2));
		PooledECS.addSystem(new SpeedDecreaseSystem(this, 3));
		PooledECS.addSystem(new MovementInputSystem(this, 4));
		PooledECS.addSystem(new MovementSystem(this, 5, 0.02f));
		PooledECS.addSystem(new DamageCollisionSystem(this, 6));
		PooledECS.addSystem(new BouncingSystem(this, 7));
		PooledECS.addSystem(new DisposeCollisionSystem(this, 8));
		PooledECS.addSystem(new AlphaDecreaseSystem(this, 9));
		PooledECS.addSystem(new TrailingSystem(this, 10));
		PooledECS.addSystem(new RenderingSystem(this, batch, 11));

	}
	private Rec a;
	private void loadEntities() {


		Entity obs = PooledECS.createEntity();
		CollidesComponent oComp = PooledECS.createComponent(CollidesComponent.class);
		oComp.physicItem = new Item<>(obs);
		oComp.filter = CollisionFilter.defaultFilter;
		oComp.collisionRelationShip = CollisionRelationship.OBSTACLE;
		oComp.x = FRAME_WIDTH / 2f;
		oComp.y = FRAME_HEIGHT / 2f;
		oComp.width = 75;
		oComp.height = 75;
		obs.add(oComp);
		PhysicWorld.add(oComp.physicItem, FRAME_WIDTH / 2f, FRAME_HEIGHT / 2f, 75, 75);
		PooledECS.addEntity(obs);

		Entity rObs = PooledECS.createEntity();
		RecOwnerComponent rOwn = PooledECS.createComponent(RecOwnerComponent.class);
		rOwn.rectangle = new Rec(FRAME_WIDTH / 4f, FRAME_HEIGHT / 2f, 75, 75);
		rOwn.rectangle.Rotate(57f, rOwn.rectangle.X, rOwn.rectangle.Y);
		Rectangles.add(rOwn.rectangle);
		rObs.add(rOwn);

		a = rOwn.rectangle;

		DisposeOnCollisionComponent rDis = PooledECS.createComponent(DisposeOnCollisionComponent.class);
		rDis.rectangle = Plr.getComponent(RecOwnerComponent.class).rectangle;
		rDis.checkRange2 = 75 * 75 + 75 * 75;
		rObs.add(rDis);

		PooledECS.addEntity(rObs);
	}

	private void loadPlayer() {
		Entity player = PooledECS.createEntity();
		player.add(PooledECS.createComponent(PrototypeComp.class));
		DrawableComponent pDrawable = PooledECS.createComponent(DrawableComponent.class);

		pDrawable.order = 3;
		pDrawable.sprite = new Sprite(assetManager.Textures[indexT.PLAYER.ordinal()]);
		pDrawable.sprite.setOriginCenter();
		pDrawable.sprite.setPosition(0,0);
		player.add(pDrawable);

		player.add(PooledECS.createComponent(MovingComponent.class));
		MovementInputComponent pMoveInput = PooledECS.createComponent(MovementInputComponent.class);
		pMoveInput.speed2 = 70.71f * 70.71f;
		player.add(pMoveInput);

		TrailComponent pTrail = PooledECS.createComponent(TrailComponent.class);
		pTrail.alphaComparator = 0.60f;
		pTrail.trailIninitalAlpha = 0.75f;
		pTrail.trailCooldown = 0.25f;
		pTrail.trailInitialCooldown = 0.15f;
		pTrail.trailAlphaDecrease = 1.5f;

		player.add(pTrail);

		CollidesComponent pCol = PooledECS.createComponent(CollidesComponent.class);
		pCol.height = 16;
		pCol.width = 16;
		pCol.x = -8;
		pCol.y = -8;
		pCol.collisionRelationShip = CollisionRelationship.PLAYER;
		pCol.filter = new CollisionFilter() {
			@Override
			public Response filter(Item item, Item other) {
				if (EntityManager.Cm.get((Entity) other.userData).collisionRelationShip.equals(CollisionRelationship.OBSTACLE))
					return Response.slide;
				return null;
			}
		};
		pCol.physicItem = new Item<Entity>(player);
		player.add(pCol);
		RecOwnerComponent pRec = PooledECS.createComponent(RecOwnerComponent.class);
		pRec.rectangle = new Rec(8,8,16, 16);
		player.add(pRec);

		EntityCreator.addPrototype(player, EntityID.PLAYER);
	}

	@Override
	public void render () {
		if (ticks % 8 == 0)
			FPS.Str = String.valueOf(Math.round(1f / Gdx.graphics.getDeltaTime()));
		ticks++;
		timeElapsed += Gdx.graphics.getDeltaTime();

		a.Rotate(5f, a.X, a.Y);
		// Set the viewport to the framebuffer size
		camera.setToOrtho(false, FRAME_WIDTH, FRAME_HEIGHT);
		camera.zoom = 1f;
		camera.update();


		screenBuffer.begin();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();

		// clear the screen
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.disableBlending();
		batch.draw(background.getKeyFrame(timeElapsed, true), 0, 0, FRAME_WIDTH, FRAME_HEIGHT);
		batch.enableBlending();

		// update and draw to the frame buffer
		PooledECS.update(Gdx.graphics.getDeltaTime());

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

		debugBoxes();


		batch.end();
		screenBuffer.end();


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

	private final Color col = new Color(255f, 0,0,0.5f);

	private void debugBoxes() {
		// Renders collisions
		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
		shapeRenderer.setColor(col);
		shapeRenderer.setProjectionMatrix(camera.combined);

		for (Rec r : Rectangles){
			shapeRenderer.polygon(r.ConvertToFloatArray());
		}
		shapeRenderer.end();

		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		for (Rect i : PhysicWorld.getRects()) shapeRenderer.rect(i.x, i.y, i.w, i.h);
		shapeRenderer.end();
	}

	private static final int GREEN_BG = 0;

	private static final int GRAY_BG = 1;

	private static final int RED_BG = 2;

	private void loadBackgrounds() {
		TextureRegion greenBG = assetManager.Textures[indexT.BG_GREEN.ordinal()];
		TextureRegion redBG = assetManager.Textures[indexT.BG_RED.ordinal()];
		TextureRegion grayBG = assetManager.Textures[indexT.BG_GRAY.ordinal()];

		TextureRegion[] greenBgFrames = greenBG.split(1,1050)[0];


		TextureRegion[] redBgFrames = redBG.split(1,1050)[0];


		TextureRegion[] grayBgFrames = grayBG.split(1, 1050)[0];



		backgrounds[0] = greenBgFrames;
		backgrounds[1] = grayBgFrames;
		backgrounds[2] = redBgFrames;

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

		PooledECS.clearPools();

	}
}
