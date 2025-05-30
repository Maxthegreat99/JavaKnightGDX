package com.segfault.games.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.*;

/**
 * holds and loads most of the game's textures and assets
 */
public class AssetManager {
    private TextureAtlas atlas;
    private Texture fontTexture;
    private final ObjectMap<indexT, TextureRegion> textures = new ObjectMap<>();
    private final ObjectMap<indexM, Mesh> meshes = new ObjectMap<>();

    /**
     * loads all the assets and puts them in the texture objectMap
     */
    public void LoadAssets() {
        atlas = new TextureAtlas(Gdx.files.internal("JavaKnight.atlas"));
        fontTexture = new Texture(Gdx.files.internal("DisposableDroidBB.png"), true);
        fontTexture.setFilter(Texture.TextureFilter.MipMapLinearNearest, Texture.TextureFilter.Linear);

        textures.put(indexT.ONE_PARTICLE_1, atlas.findRegion("1"));
        textures.put(indexT.ONE_PARTICLE_2, atlas.findRegion("2"));
        textures.put(indexT.ZERO_PARTICLE_1, atlas.findRegion("3"));
        textures.put(indexT.ZERO_PARTICLE_2, atlas.findRegion("4"));
        textures.put(indexT.ACID_PARTICLE, atlas.findRegion("acidParticle"));
        textures.put(indexT.ACID_BULLET, atlas.findRegion("acidBullet"));
        textures.put(indexT.BOSS_BAR_OUTLINE, atlas.findRegion("barOutline"));
        textures.put(indexT.ACID_BEAM, atlas.findRegion("beam"));
        textures.put(indexT.ACID_BEAM_BALL, atlas.findRegion("beamBall"));
        textures.put(indexT.BOSS, atlas.findRegion("boss"));
        textures.put(indexT.BOSS_BAR_FILLING, atlas.findRegion("bossBarCol"));
        textures.put(indexT.BOSS_DEAD, atlas.findRegion("bossDead"));
        textures.put(indexT.BOSS_HIT, atlas.findRegion("bossHit"));
        textures.put(indexT.BOUNCY, atlas.findRegion("bouncy"));
        textures.put(indexT.BOUNCY_GUN, atlas.findRegion("bouncyGun"));
        textures.put(indexT.BULLET, atlas.findRegion("bullet"));
        textures.put(indexT.NORMAL_DOOR, atlas.findRegion("door"));
        textures.put(indexT.INFINITE_DOOR, atlas.findRegion("door1"));
        textures.put(indexT.QUIT_DOOR, atlas.findRegion("door2"));
        textures.put(indexT.FIRE_PARTICLE, atlas.findRegion("fireParticle"));
        textures.put(indexT.FLOOR_PARTICLE, atlas.findRegion("floorParticle"));
        textures.put(indexT.GUN, atlas.findRegion("gun"));
        textures.put(indexT.GUNNER, atlas.findRegion("gunner"));
        textures.put(indexT.LAZER_GUN, atlas.findRegion("lazerGun"));
        textures.put(indexT.PLAYER, atlas.findRegion("player"));
        textures.put(indexT.ROCKET, atlas.findRegion("rocket"));
        textures.put(indexT.ROCKET_GUN, atlas.findRegion("rocketGun"));
        textures.put(indexT.BG_GREEN, atlas.findRegion("greenBG"));
        textures.put(indexT.BG_RED, atlas.findRegion("redBG"));
        textures.put(indexT.BG_GRAY, atlas.findRegion("grayBG"));
        textures.put(indexT.TEST_N, atlas.findRegion("object-deferred-n"));
        textures.put(indexT.TEST, atlas.findRegion("object-deferred"));
        textures.put(indexT.PLAYER_FIRE_PARTICLE, atlas.findRegion("firePart"));
        textures.put(indexT.FIRE_GRADIENT, atlas.findRegion("gradient"));
        textures.put(indexT.FIRE_CORE, atlas.findRegion("particleCore"));
        textures.put(indexT.FIRE_CORE_NOISE_VORONOI, atlas.findRegion("noiseFireVoronoi"));
        textures.put(indexT.FIRE_CORE_NOISE_PERLIN, atlas.findRegion("noiseFire"));


        loadMeshes();
    }

    private void loadMeshes() {
        float[] verts = new float[45];
        int i = 0;

        // Central vertex (center of the fan)
        verts[i++] = 0f;  // x
        verts[i++] = 0f;  // y
        verts[i++] = 0f;  // z
        verts[i++] = 0.5f; // u (center of texture)
        verts[i++] = 0.5f; // v (center of texture)

        // Angle between vertices
        float angleIncrement = 45;

        // Surrounding vertices
        for (int j = 0; j < 8; j++) {
            float angle = j * angleIncrement;
            float x = MathUtils.cosDeg(angle); // x coordinate
            float y = MathUtils.sinDeg(angle); // y coordinate
            verts[i++] = x;  // x
            verts[i++] = y;  // y
            verts[i++] = 0f; // z
            verts[i++] = (x + 1) / 2; // u (mapping x to [0, 1] range)
            verts[i++] = (y + 1) / 2; // v (mapping y to [0, 1] range)
        }

        // Create the mesh
        Mesh mesh = new Mesh(true, 9, 0,
                new VertexAttribute(VertexAttributes.Usage.Position, 3, "a_position"),
                new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, "a_texCoord"));

        // Set the vertices
        mesh.setVertices(verts);

        meshes.put(indexM.BULLET, mesh);

    }

    public TextureAtlas GetAtlas() {
        return atlas;
    }

    public Texture GetFontTexture() {
        return fontTexture;
    }
    public ObjectMap<indexT, TextureRegion> GetTextures() {
        return textures;
    }
    public ObjectMap<indexM, Mesh> GetMeshes() {
        return meshes;
    }
    public void Dispose() {
        atlas.dispose();
        fontTexture.dispose();
        for (ObjectMap.Entry<indexM, Mesh> i : meshes)
            i.value.dispose();
    }
}


