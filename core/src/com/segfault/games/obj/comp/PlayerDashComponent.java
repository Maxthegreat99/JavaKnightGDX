package com.segfault.games.obj.comp;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector4;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.segfault.games.JavaKnight;

/**
 * component for the player's dash ability
 */

public class PlayerDashComponent extends Component{

    /**
     * maximum velocity that the body will reach
     */
    public float maxVelCap = 0f;

    /**
     * whether or not the body is currently dasing
     */
    public boolean isDashing = false;

    /**
     * maxmimum acceleration the body can reach
     */
    public float maxAcce = 0f;

    /**
     * the direction of the dash
     */

    public Vector2 dir = new Vector2();

    /**
     * time the dash lasts
     */
    public float dashTime = 0;

    /**
     * elapsed time the player has dashed
     */
    public float dashElapsedTime = 0f;

    /**
     * cooldown before being able to dash again
     */
    public float dashCooldown = 0;

    /**
     * elapsed time of the cooldown
     */
    public float cooldownElapsedTime = 0;

    /**
     * whether the player can currently dash
     */
    public boolean canDash = false;

    /**
     * dash counter force time
     */
    public float dashCounterTime = 0;

    /**
     * elapsed time of the dash counter force application
     */
    public float dashCounterElapsedTime = 0f;

    /**
     * air resistance while countering the end of the dash
     */
    public float counterDamping = 0f;

    @Override
    public void dispose(JavaKnight instance) {

    }

    @Override
    public Component clone(JavaKnight instance, Entity ent, Vector4 pol, JsonValue properties) {
        PlayerDashComponent comp = instance.GetEntityManager().GetEngine().createComponent(this.getClass());
        comp.maxVelCap = maxVelCap;
        comp.maxAcce = maxAcce;
        comp.dashTime = dashTime;
        comp.dashCooldown = dashCooldown;
        comp.dashCounterTime = dashCounterTime;
        comp.counterDamping = counterDamping;

        return comp;
    }

    @Override
    public void read(JsonValue jsonValue, JavaKnight instance, boolean maploading, Entity ent) {
        maxAcce = jsonValue.getFloat("maxAcce");
        maxVelCap = jsonValue.getFloat("maxVelCap");
        dashTime = jsonValue.getFloat("dashTime");
        dashCooldown = jsonValue.getFloat("dashCooldown");
        dashCounterTime = jsonValue.getFloat("dashCounterTime");
        counterDamping = jsonValue.getFloat("counterDamping");
    }

    @Override
    public void write(Json json) {
        json.writeField(maxAcce, "maxAcce");
        json.writeField(maxVelCap, "maxVelCap");
        json.writeField(dashTime, "dashTime");
        json.writeField(dashCooldown, "dashCooldown");
        json.writeField(dashCounterTime, "dashCounterTime");
        json.writeField(counterDamping, "counterDamping");
    }

    @Override
    public void reset() {
        maxAcce = 0f;
        maxVelCap = 0f;
        isDashing = false;
        dir.set(0, 0);
        dashTime = 0f;
        dashElapsedTime = 0f;
        dashCooldown = 0f;
        cooldownElapsedTime = 0f;
        canDash = false;
        dashCounterTime = 0f;
        dashCounterElapsedTime = 0f;
        counterDamping = 0f;
    }
}
