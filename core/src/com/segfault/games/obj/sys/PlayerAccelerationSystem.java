package com.segfault.games.obj.sys;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.segfault.games.JavaKnight;
import com.segfault.games.obj.comp.AcceleratedBodyComponent;
import com.segfault.games.obj.comp.CollidesComponent;
import com.segfault.games.obj.comp.PlayerAcceleratedComponent;
import com.segfault.games.obj.ent.Mappers;

/**
 * System for player movement, that being jumping and horizontal movement
 */
public class PlayerAccelerationSystem extends IteratingSystem {
    private final Mappers mappers;
    private final World physicWorld;

    private final Vector2 start = new Vector2();
    private final Vector2 end = new Vector2();

    private PlayerAcceleratedComponent acceComp;

    private final RayCastCallback ceilRaycast = new RayCastCallback() {


        @Override
        public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {

            acceComp.hitCeil = true;
            return 0;
        }
    };

    private final RayCastCallback groundRaycast = new RayCastCallback() {

        @Override
        public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {

            acceComp.onGround = true;
            return 0;
        }

    };

    public PlayerAccelerationSystem(JavaKnight instance, int priority) {
        super(Family.all(PlayerAcceleratedComponent.class).get(), priority);

        physicWorld = instance.GetEntityManager().GetPhysicWorld();
        mappers = instance.GetEntityManager().GetMappers();
    }

    /**
     * - Check if player is on ground
     * - Check horizontal input
     * - if is on ground apply horizontal input based on current velocity
     * - if not apply minimal horizontal input in the air
     * - if no horizontal input is being apply and is on ground draw character to a halt
     * - if on ground apply initial verticle jump velocity + constant velocity based on input
     * - if ceiling was hit, cancle verticle constant velocity and apply reaction force
     *
     *
     * @param entity The current Entity being processed
     * @param deltaTime The delta time between the last and current frame
     */
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        CollidesComponent colComp = mappers.Collides.get(entity);
        acceComp = mappers.PlayerAcceleration.get(entity);

        acceComp.onGround = false;
        acceComp.hitCeil = false;

        AcceleratedBodyComponent movement = mappers.AcceleratedBody.get(entity);

        start.set(colComp.physicBody.getWorldCenter()).add(0, -colComp.physicBody.getFixtureList().first().getShape().getRadius());
        end.set(start).add(0, -0.01f);

        /* is player on ground */
        physicWorld.rayCast(groundRaycast, start, end);

        Vector2 vel = colComp.physicBody.getLinearVelocity();

        float dirX = 0;

        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT))
            dirX += 1;

        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT))
            dirX -= 1;

        /*
         * horizontal acceleration
         */
        if (acceComp.onGround && dirX == 0) {
            movement.ax = (-vel.x / acceComp.frictionTime);
        }
        else if (acceComp.onGround) {
            if (Math.abs(vel.x) >= acceComp.velcapX && Math.signum(vel.x) == Math.signum(dirX))
                movement.ax = MathUtils.lerp(movement.ax, 0, acceComp.acceAlphaX);
            else {
                movement.ax = MathUtils.lerp(movement.ax, acceComp.maxAcceX * dirX, acceComp.acceAlphaX);

                if (Math.signum(dirX) != Math.signum(vel.x))
                    movement.ax += (-vel.x / acceComp.frictionTime) / 2;
            }
        }
        else { /* in air */
            if (Math.abs(vel.x) >= acceComp.velcapX && Math.signum(vel.x) == Math.signum(dirX))
                movement.ax = MathUtils.lerp(movement.ax, 0, acceComp.acceAlphaX) * acceComp.airControl;
            else
                movement.ax = MathUtils.lerp(movement.ax, acceComp.maxAcceX * dirX, acceComp.acceAlphaX) * acceComp.airControl;
        }

        start.set(colComp.physicBody.getWorldCenter()).add(0, colComp.physicBody.getFixtureList().first().getShape().getRadius());
        end.set(start).add(0, 0.01f);

        /* has player hit ceiling */
        physicWorld.rayCast(ceilRaycast, start, end);

        /*
         * vertical acceleration
         */

        if (acceComp.hitCeil) {
            movement.ay = -Math.abs(movement.ay / 2.5f);
            acceComp.isJumping = false;
            acceComp.acceTimeYElapsed = 0f;
            return;
        }


        if (acceComp.onGround) {
            movement.ay = 0f;
            acceComp.isJumping = false;
            acceComp.acceTimeYElapsed = 0f;
        }


        if (acceComp.onGround && Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            movement.ay = acceComp.initAcceY;
            acceComp.isJumping = true;
            acceComp.acceTimeYElapsed = 0f;

            return;
        }

        if (acceComp.isJumping && Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            movement.ay = MathUtils.lerp(acceComp.constantAcceY, 0, acceComp.acceTimeYElapsed / acceComp.acceTimeY);

            acceComp.acceTimeYElapsed += deltaTime;

            if (acceComp.acceTimeYElapsed >= acceComp.acceTimeY) {
                acceComp.isJumping = false;
                acceComp.acceTimeYElapsed = 0f;
                movement.ay = 0;
            }

            return;
        }

        if (acceComp.isJumping && !Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            acceComp.isJumping = false;
            acceComp.acceTimeYElapsed = 0f;
            movement.ay = 0;
            return;
        }

    }
}
