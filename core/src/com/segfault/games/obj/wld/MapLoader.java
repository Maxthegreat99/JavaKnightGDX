package com.segfault.games.obj.wld;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectMap;

public class MapLoader {

    private final TmxMapLoader mapLoader = new TmxMapLoader();
    public final ObjectMap<MapID, TiledMap> maps = new ObjectMap<>();

    public MapLoader() {

    }

    public void CacheMaps() {
        maps.put(MapID.BOSS_ROOM, mapLoader.load("bossRoom.tmx"));

    }



}
