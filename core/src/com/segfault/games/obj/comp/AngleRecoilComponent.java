package com.segfault.games.obj.comp;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector4;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.segfault.games.JavaKnight;

/**
 * Component to give entities a 'recoil' effect where where we rotate their sprite at
 * a certain speed then get back to it slower when specified
 */
public class AngleRecoilComponent extends Component{
    /**
     * the value the recoil should reach before starting to go back down
     */
    public float maxAngle = 0.0f;
    /**
     * current speed value to increase the angle, note this value is multiplied by deltaTime
     */
    public float angleSpeed = 0.0f;
    /**
     * initial speed value
     */
    public float intialAngleSpeed = 0.0f;

    /**
     * the acceleration at which to increase the speed
     */
    public float angleAcceleration = 0.0f;
    /**
     * value to devide the acceleration when going back to its original angle
     */
    public float divisor = 0.0f;

    /**
     * the currently added angle
     */
    public float angle = 0.0f;

    /**
     * whether the initial recoil is currently trigering
     */
    public boolean trigger = false;

    /**
     * current time the object stays up before going back down
     */
    public float retainTime = 0f;

    /**
     * initial retain time
     */
    public float initialRetainTime = 0f;


    @Override
    public void reset() {
        maxAngle = 0.0f;
        divisor = 0.0f;
        angleSpeed = 0.0f;
        angle = 0.0f;
        trigger = false;
        retainTime = 0f;
        initialRetainTime = 0f;
        angleAcceleration = 0f;
        intialAngleSpeed = 0f;
    }

    @Override
    public void dispose(JavaKnight instance) {

    }

    @Override
    public Component Clone(JavaKnight instance, Entity ent, Vector4 pol, JsonValue properties) {
        AngleRecoilComponent comp = instance.GetEntityManager().GetEngine().createComponent(this.getClass());
        comp.angle = angle;
        comp.maxAngle = maxAngle;
        comp.angleSpeed = angleSpeed;
        comp.divisor = divisor;
        comp.trigger = trigger;
        comp.retainTime = retainTime;
        comp.initialRetainTime = initialRetainTime;
        comp.angleAcceleration = angleAcceleration;
        comp.intialAngleSpeed = intialAngleSpeed;
        return comp;
    }

    @Override
    public void write(Json json) {
        json.writeFields(this);
    }

    @Override
    public void read(JsonValue jsonValue, JavaKnight instance) {
        maxAngle = jsonValue.getFloat("maxAngle");
        intialAngleSpeed = angleSpeed = jsonValue.getFloat("angleSpeed");
        divisor = jsonValue.getFloat("divisor");
        initialRetainTime = jsonValue.getFloat("initialRetainTime");
        angleAcceleration = jsonValue.getFloat("angleAcceleration");
    }
}
