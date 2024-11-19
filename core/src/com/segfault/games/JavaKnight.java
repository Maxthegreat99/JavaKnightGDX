package com.segfault.games;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.segfault.games.gra.Renderer;
import com.segfault.games.obj.Rec;
import com.segfault.games.obj.Text;
import com.segfault.games.obj.ent.EntityManager;
import com.segfault.games.obj.wld.MapLoader;
import com.segfault.games.util.AssetManager;

public class JavaKnight extends ApplicationAdapter {

	private Renderer renderer;
	private EntityManager entityManager;
	private AssetManager assetManager;
	private MapLoader mapLoader;
	private final Array<Rec> rectangles = new Array<>(); // rectangle array used for debugging
	private final Array<Text> texts = new Array<>(); // texts array rendered by the renderer's bitmap font
	private final ObjectMap<BitmapFontCache, Float> staticFonts = new ObjectMap<>(); // static texts on screen to save performance

	private long ticks = 0;
	private float timeElapsed = 0.0f;
	private final Text FPS = new Text();
	public static final int SCREEN_WIDTH = 1680;
	public static final int SCREEN_HEIGHT = 1050;
	public static final int FRAME_WIDTH = 616;
	public static final int FRAME_HEIGHT = 385;
	public static final float ZOOM = 0.90909f;

	@Override
	public void create () {

		assetManager = new AssetManager();
		assetManager.LoadAssets();
		// with the zoom in consideration, the player see is aprox, 560 : 350 of the buffer
		renderer = new Renderer(assetManager, SCREEN_WIDTH, SCREEN_HEIGHT, FRAME_WIDTH, FRAME_HEIGHT, ZOOM);
		Box2D.init();
		entityManager = new EntityManager(this);
		mapLoader = new MapLoader();
		mapLoader.CacheMaps();

		entityManager.InitializeSystems(this, renderer.GetSpriteBatch());
		entityManager.LoadEntities(this);
		FPS.X = 30;
		FPS.Y = FRAME_HEIGHT - 50;
		texts.add(FPS);

		renderer.SetBackground(renderer.GRAY_BG, 0.045f);
	}

	@Override
	public void render () {
		if (ticks % 8 == 0)
			FPS.Str = String.valueOf(Math.round(1f / Gdx.graphics.getDeltaTime()));
		ticks++;
		timeElapsed += Gdx.graphics.getDeltaTime();

		renderer.SetUpFrame(timeElapsed);
		entityManager.GetEngine().update(Gdx.graphics.getDeltaTime());
		renderer.Render(this, entityManager.GetPhysicWorld());

	}

	public Array<Rec> GetRectangles() {
		return rectangles;
	}

	public Array<Text> GetTexts() {
		return texts;
	}

	public ObjectMap<BitmapFontCache, Float> GetStaticFonts() {
		return staticFonts;
	}

	public AssetManager GetAssetManager() {
		return assetManager;
	}

	public Renderer GetRenderer() {
		return renderer;
	}

	public EntityManager GetEntityManager() {
		return entityManager;
	}
	public MapLoader GetMapLoader() {
		return mapLoader;
	}

	@Override
	public void dispose () {
		renderer.Dispose();
		assetManager.Dispose();
		entityManager.Dispose();

		texts.clear();
		for (ObjectMap.Entry<BitmapFontCache, Float> i : staticFonts)
			i.key.clear();

		staticFonts.clear();
		rectangles.clear();

	}
}
