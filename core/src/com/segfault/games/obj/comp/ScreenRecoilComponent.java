package com.segfault.games.obj.comp;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector4;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.segfault.games.JavaKnight;

public class ScreenRecoilComponent extends Component {


    /**
     * maxDistance for camera to recoil before starting to go back to its original position
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
     * cyrrent seconds to wait before the recoil goes back to its original
     */
    public float retainTime = 0f;
    /**
     * initial value of retain time
     */
    public float initialRetainTime = 0f;

    /**
     * speed at which the distance should move, is multiplied by deltatime
     */
    public float distanceSpeed = 0f;

    /**
     * acceleration at which to increase the distance speed
     */
    public float distanceAcceleration = 0f;

    /**
     * initial distance speed
     */
    public float initialDistanceSpeed = 0f;

    /**
     * divisor determining the deceleration
     */
    public float divisor = 0f;

    /**
     * whether the recoil is currently triggered
     */
    public boolean trigger = false;
    @Override
    public void dispose(JavaKnight instance) {
    }

    @Override
    public Component clone(JavaKnight instance, Entity ent, Vector4 pol, JsonValue properties) {
        ScreenRecoilComponent comp = instance.GetEntityManager().GetEngine().createComponent(this.getClass());
        comp.maxDis = maxDis;
        comp.divisor = divisor;
        comp.angle = angle;
        comp.retainTime = retainTime;
        comp.distanceSpeed = distanceSpeed;
        comp.distanceAcceleration = distanceAcceleration;
        comp.initialDistanceSpeed = initialDistanceSpeed;
        comp.trigger = trigger;
        comp.initialRetainTime = initialRetainTime;
        return comp;
    }

    @Override
    public void read(JsonValue jsonValue, JavaKnight instance, boolean maploading, Entity ent) {
        maxDis = jsonValue.getFloat("maxDis");
        divisor = jsonValue.getFloat("divisor");
        initialRetainTime = jsonValue.getFloat("initialRetainTime");
        initialDistanceSpeed = distanceSpeed = jsonValue.getFloat("initialDistanceSpeed");
        distanceAcceleration = jsonValue.getFloat("distanceAcceleration");
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
        retainTime = 0f;
        distanceSpeed = 0f;
        distanceAcceleration = 0f;
        initialDistanceSpeed = 0f;
        divisor = 0f;
        initialRetainTime = 0f;
        trigger = false;
    }
}
