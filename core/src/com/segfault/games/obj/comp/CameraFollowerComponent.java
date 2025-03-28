package com.segfault.games.obj.comp;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector4;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.segfault.games.JavaKnight;

/**
 * Objects with these follow the camera, uses a priority index to
 * determine which object to prioritize
 */
public class CameraFollowerComponent extends Component {

    /** x value being interpolated towards
     */
    public float targetX = 0.0f;

    /** y value being interpolated towards
     */
    public float targetY = 0.0f;


    /** current interpolated x value
     */
    public float currentX = 0.0f;

    /** current interpolated y value
     */
    public float currentY = 0.0f;


    /** priority taken over camera control
     */
    public int priority = 0;


    /** object just got loaded
     */
    public boolean fresh = true;


    /** percentage interpolated every frame
     */
    public float alpha = 0.15f;

    /**
     * maximum distance that the camera can reach from the target
     */

    public float maxDis = 0f;

    @Override
    public void reset() {
        targetX = targetY = currentY = currentX = 0f;
        priority = 0;
        fresh = true;
        alpha = 0.15f;
        maxDis = 0f;
    }

    @Override
    public void dispose(JavaKnight instance) {

    }

    @Override
    public Component clone(JavaKnight instance, Entity ent, Vector4 pol, JsonValue properties) {
        CameraFollowerComponent comp = instance.GetEntityManager().GetEngine().createComponent(this.getClass());
        comp.targetX = targetX;
        comp.targetY = targetY;
        comp.currentX = currentX;
        comp.currentY = currentY;
        comp.fresh = true;
        comp.alpha = alpha;
        comp.maxDis = maxDis;
        return comp;
    }

    @Override
    public void write(Json json) {
        json.writeField(priority, "priority");
        json.writeField(alpha, "alpha");
        json.writeField(maxDis, "maxDis");
    }

    @Override
    public void read(JsonValue jsonValue, JavaKnight instance, boolean maploading, Entity ent) {
        priority = jsonValue.getInt("priority");
        alpha = jsonValue.getFloat("alpha");
        maxDis = jsonValue.getFloat("maxDis");
    }
}
