package com.segfault.games;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.segfault.games.gra.Renderer;
import com.segfault.games.util.AssetManager;
import com.segfault.games.obj.Rec;
import com.segfault.games.obj.Text;
import com.segfault.games.obj.ent.EntityManager;

public class JavaKnight extends ApplicationAdapter {

	private Renderer renderer;
	private EntityManager entityManager;
	private AssetManager assetManager;

	private final Array<Rec> rectangles = new Array<>(); // rectangle array used for debugging
	private final Array<Text> texts = new Array<>(); // texts array rendered by the renderer's bitmap font
	private final ObjectMap<BitmapFontCache, Float> staticFonts = new ObjectMap<>(); // static texts on screen to save performance

	private long ticks = 0;
	private float timeElapsed = 0.0f;
	private final Text FPS = new Text();

	@Override
	public void create () {
		assetManager = new AssetManager();
		assetManager.LoadAssets();
		renderer = new Renderer(assetManager, 1680, 1050, 616, 385, 0.90909f);
		entityManager = new EntityManager(this);

		entityManager.InitializeSystems(this, renderer.GetSpriteBatch());
		entityManager.LoadEntities(this, renderer.FRAME_WIDTH, renderer.FRAME_HEIGHT);
		entityManager.GetEntityLoader().LoadEntities(entityManager, this);
		FPS.X = 30;
		FPS.Y = renderer.FRAME_HEIGHT - 50;
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

	@Override
	public void dispose () {
		renderer.Dispose();
		assetManager.Dispose();
		entityManager.Dispose();

		texts.clear();
		staticFonts.forEach(i -> i.key.clear());
		staticFonts.clear();
		rectangles.clear();

	}
}
