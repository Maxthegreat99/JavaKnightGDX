package com.segfault.games.obj.comp;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector4;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.segfault.games.JavaKnight;
import com.segfault.games.obj.ent.EntityID;

public class BulletSpawnComponent extends Component {
    /**
     * entity id of the bullet to spawn
     */
    public EntityID bulletID = null;

    /**
     * x position of where to spawn the entity
     */

    public float spawnPosX = 0f;

    /**
     * y position of where the projectile is meant to spawn
     */
    public float spawnPosY = 0f;

    /**
     * speed of the projectile on the x axis
     */
    public float deltaX = 0f;

    /**
     * speed of the projectile on the y axis
     */
    public float deltaY = 0f;

    /**
     * trigger boolean to spawn the entity
     */
    public boolean spawn = false;

    /**
     * width of the spawned entity
     */
    public float width = 0;

    /**
     * height of the spawned entity
     */
    public float height = 0;

    /**
     * angle at which to spawn the projectile
     */
    public float angle = 0;
    @Override
    public void dispose(JavaKnight instance) {

    }

    @Override
    public Component Clone(JavaKnight instance, Entity ent, Vector4 pol, JsonValue properties) {
        BulletSpawnComponent comp = instance.GetEntityManager().GetEngine().createComponent(this.getClass());
        comp.spawnPosY = spawnPosY;
        comp.spawnPosX = spawnPosX;
        comp.deltaX = deltaX;
        comp.deltaY = deltaY;
        comp.bulletID = bulletID;
        comp.spawn = spawn;
        comp.width = width;
        comp.height = height;
        comp.angle = angle;
        return comp;
    }

    @Override
    public void read(JsonValue jsonValue, JavaKnight instance) {
        bulletID = EntityID.valueOf(jsonValue.getString("bulletID"));
        width = jsonValue.getFloat("width");
        height = jsonValue.getFloat("height");
    }

    @Override
    public void write(Json json) {
        json.writeFields(this);
    }

    @Override
    public void reset() {
        bulletID = null;
        deltaX = 0;
        deltaY = 0;
        spawnPosX = 0;
        spawnPosY = 0;
        spawn = false;
        height = 0;
        width = 0;
        angle = 0;
    }
}
