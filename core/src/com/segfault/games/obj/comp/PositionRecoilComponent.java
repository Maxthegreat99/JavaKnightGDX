package com.segfault.games.obj.comp;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector4;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.segfault.games.JavaKnight;

public class PositionRecoilComponent extends Component{

    /**
     * maxDistance to recoil before starting to go back to its original position
     */
    public float maxDis = 0f;
    /**
     * current distance traveled
     */
    public float dis = 0f;

    /**
     * angle traveling
     */
    public float angle = 0f;

    /**
     * speed at which the distance should move, is multiplied by deltatime
     */
    public float distanceSpeed = 0f;

    /**
     * acceleration at which to increase the distance speed
     */
    public float distanceDecceleration = 0f;

    /**
     * initial distance speed
     */
    public float initialDistanceSpeed = 0f;

    /**
     * whether the recoil is currently triggered
     */
    public boolean trigger = false;
    @Override
    public void dispose(JavaKnight instance) {
    }

    @Override
    public Component clone(JavaKnight instance, Entity ent, Vector4 pol, JsonValue properties) {
        PositionRecoilComponent comp = instance.GetEntityManager().GetEngine().createComponent(this.getClass());
        comp.maxDis = maxDis;
        comp.angle = angle;
        comp.distanceSpeed = distanceSpeed;
        comp.initialDistanceSpeed = initialDistanceSpeed;
        comp.trigger = trigger;
        comp.distanceDecceleration = distanceDecceleration;
        return comp;
    }

    @Override
    public void read(JsonValue jsonValue, JavaKnight instance, boolean maploading, Entity ent) {
        maxDis = jsonValue.getFloat("maxDis");
        initialDistanceSpeed = distanceSpeed = jsonValue.getFloat("initialDistanceSpeed");
        distanceDecceleration = jsonValue.getFloat("distanceDecceleration");
    }

    @Override
    public void write(Json json) {
        json.writeFields(this);
    }

    @Override
    public void reset() {
        maxDis = 0f;
        dis = 0f;
        angle = 0f;
        distanceSpeed = 0f;
        initialDistanceSpeed = 0f;
        distanceDecceleration = 0f;
        trigger = false;
    }
}
