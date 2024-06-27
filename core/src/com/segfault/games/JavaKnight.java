package com.segfault.games;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;
import com.segfault.games.assets.AssetManager;
import com.segfault.games.obj.Rec;
import com.segfault.games.obj.Text;
import com.segfault.games.obj.abs.Object;

public class JavaKnight extends ApplicationAdapter {

	private SpriteBatch batch;
	private BitmapFont font; // Font the whole game is gonna use
	// Game window dimensions
	private AssetManager assetManager;
	public final int SCREEN_WIDTH = 1680;
	public final int SCREEN_HEIGHT = 1050;
	public final int FRAME_WIDTH = 560;
	public final int FRAME_HEIGHT = 350;
	public final Array<Object> LayerOne = new Array<>();
	public final Array<Object> LayerTwo = new Array<>();
	public final Array<Object> LayerThree = new Array<>();
	public final Array<Object> LayerFour = new Array<>();
	public final Array<Object> LayerUI = new Array<>();

	// Lists for specific types of objects
	public final Array<Rec> Rectangles = new Array<>();
	public final Array<Text> Texts = new Array<>();
	private FrameBuffer screenBuffer;
	private TextureRegion fboTextureRegion;
	private TextureAtlas atlas;
	private ShaderProgram fontShader;
	private Texture fontTexture;
	//public final Player Plr;

	// Physics world instance
	//public final World<PhysicObject> PhysicWorld;
	public OrthographicCamera camera;
	public long ticks = 0;

	public Sprite plr;
	@Override
	public void create () {
		batch = new SpriteBatch();

		assetManager = new AssetManager();
		atlas = new TextureAtlas(Gdx.files.internal("JavaKnight.atlas"));
		plr = atlas.createSprite("player");
		plr.setOriginCenter();
		plr.setPosition((float) FRAME_WIDTH / 2, (float) FRAME_HEIGHT / 2);


		fontTexture = new Texture(Gdx.files.internal("DisposableDroidBB.png"), true); // true enables mipmaps
		fontTexture.setFilter(Texture.TextureFilter.MipMapLinearNearest, Texture.TextureFilter.Linear); // linear filtering in nearest mipmap image
		font = new DistanceFieldFont(Gdx.files.internal("DisposableDroidBB.fnt"), new TextureRegion(fontTexture), false);
		fontShader = new ShaderProgram(Gdx.files.internal("font.vert"), Gdx.files.internal("font.frag"));
		if (!fontShader.isCompiled()) Gdx.app.error("fontShader", "compilation failed:\n" + fontShader.getLog());

		screenBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, FRAME_WIDTH, FRAME_HEIGHT, false);
		camera = new OrthographicCamera(SCREEN_WIDTH, SCREEN_HEIGHT);

		camera.position.set(SCREEN_WIDTH / 2f, SCREEN_HEIGHT / 2f, 0);
		camera.update();
		camera.zoom += 0.02f;

	}

	private void update() {
		// Update objects in each layer
		for (int i = 0; i < LayerOne.size; i++)
			LayerOne.get(i).update();
		for (int i = 0; i < LayerTwo.size; i++)
			LayerTwo.get(i).update();
		for (int i = 0; i < LayerThree.size; i++)
			LayerThree.get(i).update();
		for (int i = 0; i < LayerFour.size; i++)
			LayerFour.get(i).update();
		for (int i = 0; i < LayerUI.size; i++)
			LayerUI.get(i).update();


		ticks++;
	}

	@Override
	public void render () {
		update();

		// Set the viewport to the framebuffer size
		camera.setToOrtho(false, FRAME_WIDTH, FRAME_HEIGHT);
		camera.update();

		screenBuffer.begin();

		batch.setProjectionMatrix(camera.combined);
		// clear the screen, update the camera and make the sprite batch
		// use its matrices.
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		for (int i = 0; i < LayerOne.size; i++)
			LayerOne.get(i).draw(batch);

		for (int i = 0; i < LayerTwo.size; i++)
			LayerTwo.get(i).draw(batch);

		for (int i = 0; i < LayerThree.size; i++)
			LayerThree.get(i).draw(batch);

		for (int i = 0; i < LayerFour.size; i++)
			LayerFour.get(i).draw(batch);

		for (int i = 0; i < LayerUI.size; i++)
			LayerUI.get(i).draw(batch);

		// Draw text objects
		for (int i = 0; i < Texts.size; i++) {
			Text t = Texts.get(i);
			font.getData().setScale(t.Scale);
			font.draw(batch, t.Str, t.X, t.Y);
		}

		plr.rotate(1);
		plr.draw(batch);

		batch.setShader(fontShader);
		font.getData().setScale(1);
		font.draw(batch, "Hello LibGDX World!!!", (float) FRAME_WIDTH / 3, FRAME_HEIGHT - (float) FRAME_HEIGHT * 0.75f);
		batch.setShader(null);

		batch.end();
		screenBuffer.end();

		camera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);
		camera.update();

		Texture sboTexture = screenBuffer.getColorBufferTexture();
		sboTexture.setFilter(Texture.TextureFilter.Linear,
				Texture.TextureFilter.Nearest);
		fboTextureRegion = new TextureRegion(sboTexture);
		fboTextureRegion.flip(false, true);

		batch.setProjectionMatrix(camera.combined);
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
		Rectangles.clear();

	}
}
