package com.segfault.games.obj.sys.phy;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.segfault.games.JavaKnight;
import com.segfault.games.obj.comp.AcceleratedBodyComponent;
import com.segfault.games.obj.comp.CollidesComponent;
import com.segfault.games.obj.comp.GroundCheckComponent;
import com.segfault.games.obj.comp.PlayerDashComponent;
import com.segfault.games.obj.ent.Mappers;
import com.segfault.games.obj.sys.SubSystem;

/**
 * movement system for dashing based on input and grounc collision
 */

public class PlayerDashSystem implements SubSystem {

    private final Mappers mappers;

    private final Vector2 speed = new Vector2();

    public PlayerDashSystem(JavaKnight instance) {

        mappers = instance.GetEntityManager().GetMappers();
    }

    /**
     * - check dash cooldown
     * - if on decrease and return
     * - check dash time
     * - if on increase movement and return
     * - check if on ground
     * - if yes player set canDash
     * - check input for direction
     * - if any input and canDash start dashing
     *
     */
    @Override
    public void processEntity(Entity entity, float interval, float accumulator) {
        PlayerDashComponent dash = mappers.PlayerDash.get(entity);
        AcceleratedBodyComponent movement = mappers.AcceleratedBody.get(entity);
        CollidesComponent colComp = mappers.Collides.get(entity);

        if (dash.dashCounterElapsedTime < dash.dashCounterTime) {
            dash.dashCounterElapsedTime += interval;

            movement.ax = 0;
            movement.ay = 0;
        }
        else {
            colComp.physicBody.setLinearDamping(0);
            colComp.physicBody.setGravityScale(1f);
        }


        if (dash.cooldownElapsedTime < dash.dashCooldown) {
            dash.cooldownElapsedTime += interval;
            return;
        }

        if (dash.dashElapsedTime < dash.dashTime && dash.isDashing) {
            dash.dashElapsedTime += interval;

            colComp.physicBody.setGravityScale(0f);

            movement.ax = 0;
            movement.ay = 0;


            speed.set(dash.dir);
            speed.setLength(dash.maxVelCap);

            colComp.physicBody.setLinearVelocity(speed);

            return;
        } else if (dash.isDashing) {
            dash.isDashing = false;
            dash.cooldownElapsedTime = 0;
            dash.dashCounterElapsedTime = 0;
            movement.ax = 0;
            movement.ay = 0;
            colComp.physicBody.setLinearDamping(dash.counterDamping);

            return;
        }

        GroundCheckComponent groundCheckComponent = mappers.GroundCheck.get(entity);

        if (groundCheckComponent.isOnGround)
            dash.canDash = true;

        if (!dash.canDash)
            return;

        dash.dir.set(0,0);

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D))
            dash.dir.x += 1;

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A))
            dash.dir.x -= 1;

        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W))
            dash.dir.y += 1;

        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S))
            dash.dir.y -= 1;

        dash.dir.nor();

        if (Float.compare(dash.dir.len2(), 0f) == 0)
            return;

        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
            dash.isDashing = true;
            dash.dashElapsedTime = 0f;
            dash.canDash = false;
        }



    }
}
