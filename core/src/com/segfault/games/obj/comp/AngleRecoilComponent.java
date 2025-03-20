package com.segfault.games.obj.comp;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector4;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.segfault.games.JavaKnight;

/**
 * Component to give entities a 'recoil' effect where we rotate their sprite at
 * a certain speed then get back to it slower when specified
 */
public class AngleRecoilComponent extends Component{
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
    public float angleDecceleration = 0.0f;

    /**
     * the currently added angle
     */
    public float angle = 0.0f;

    /**
     * whether the initial recoil is currently trigering
     */
    public boolean trigger = false;

    /**
     * Max angle the object can reach
     */
    public float maxAngle = 0f;


    @Override
    public void reset() {
        angleSpeed = 0.0f;
        angle = 0.0f;
        trigger = false;
        angleDecceleration = 0f;
        intialAngleSpeed = 0f;
        maxAngle = 0f;
    }

    @Override
    public void dispose(JavaKnight instance) {

    }

    @Override
    public Component clone(JavaKnight instance, Entity ent, Vector4 pol, JsonValue properties) {
        AngleRecoilComponent comp = instance.GetEntityManager().GetEngine().createComponent(this.getClass());
        comp.angle = angle;
        comp.angleSpeed = angleSpeed;
        comp.trigger = trigger;
        comp.angleDecceleration = angleDecceleration;
        comp.intialAngleSpeed = intialAngleSpeed;
        comp.maxAngle = maxAngle;
        return comp;
    }

    @Override
    public void write(Json json) {
        json.writeFields(this);
    }

    @Override
    public void read(JsonValue jsonValue, JavaKnight instance) {
        intialAngleSpeed = angleSpeed = jsonValue.getFloat("angleSpeed");
        angleDecceleration = jsonValue.getFloat("angleDecceleration");
        maxAngle = jsonValue.getFloat("maxAngle");
    }
}
