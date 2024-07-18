package com.segfault.games.obj.wld;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectMap;
import com.segfault.games.JavaKnight;

public class MapLoader {

    private final TmxMapLoader mapLoader = new TmxMapLoader();
    public final ObjectMap<MapID, TiledMap> maps = new ObjectMap<>();

    public MapLoader() {

    }

    public void CacheMaps() {
        maps.put(MapID.BOSS_ROOM, mapLoader.load("bossRoom.tmx"));
    }

    private final Vector2 vec = new Vector2();

    /**
     * libgdx uses y up while tiled uses y down, some objects also need to be initialized
     * taking into about that their position is in the middle. the origin of objects in
     * @return
     */
    public Vector2 tiledPosToGDX(float x, float y, float width, float height, String name) {
        vec.set(x, y);

        if (name.startsWith("Rec")) {
            vec.y = vec.y + height / 2;
            vec.x = vec.x + width / 2;
        }

        return vec;

    }
}
