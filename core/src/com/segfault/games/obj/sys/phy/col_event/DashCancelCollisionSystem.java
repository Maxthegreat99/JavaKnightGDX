package com.segfault.games.obj.sys.phy.col_event;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Contact;
import com.segfault.games.JavaKnight;
import com.segfault.games.obj.comp.AcceleratedBodyComponent;
import com.segfault.games.obj.comp.CollidesComponent;
import com.segfault.games.obj.comp.PlayerDashComponent;
import com.segfault.games.obj.ent.Mappers;

/**
 * cancles the player's dash when entering collisions while dashing
 */
public class DashCancelCollisionSystem implements CollisionEventSystem{
    private final Mappers mappers;
    public DashCancelCollisionSystem(JavaKnight instance) {
        mappers = instance.GetEntityManager().GetMappers();
    }
    @Override
    public void HandleCollision(Entity entity, Entity target, Contact collision) {
        PlayerDashComponent dash = mappers.PlayerDash.get(entity);
        AcceleratedBodyComponent movement = mappers.AcceleratedBody.get(entity);
        CollidesComponent colComp = mappers.Collides.get(entity);

        if (!dash.isDashing)
            return;

        dash.isDashing = false;
        dash.cooldownElapsedTime = 0;
        dash.dashCounterElapsedTime = 0;
        movement.ax = 0;
        movement.ay = 0;
        colComp.physicBody.setLinearDamping(dash.counterDamping);
    }

    @Override
    public void HandleEndCollision(Entity entity, Entity target, Contact collision) {

    }
}
