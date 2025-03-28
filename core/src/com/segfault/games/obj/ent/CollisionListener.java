package com.segfault.games.obj.ent;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.segfault.games.obj.comp.CollidesComponent;
import com.segfault.games.obj.comp.CollisionEventComponent;
import com.segfault.games.obj.sys.phy.col_event.CollisionEventSystem;

public class CollisionListener implements ContactListener {

    private final EntityManager manager;
    public CollisionListener(EntityManager manager) {
        this.manager = manager;
    }

    @Override
    public void beginContact(Contact contact) {



        CollidesComponent collidesComponentA = (CollidesComponent) contact.getFixtureA().getUserData();
        CollidesComponent collidesComponentB = (CollidesComponent) contact.getFixtureB().getUserData();

        collidesComponentA.collisionCount++;
        collidesComponentB.collisionCount++;

        if (!collidesComponentA.hasCollisionEvent && !collidesComponentB.hasCollisionEvent) return;

        CollisionEventComponent colEventA = manager.GetMappers().CollisionEvent.get(collidesComponentA.entity);
        CollisionEventComponent colEventB = manager.GetMappers().CollisionEvent.get(collidesComponentB.entity);

        CollisionEventSystem[] events = manager.GetCollisionEvents();

        if (colEventA != null && colEventA.collisionEvents.containsKey(collidesComponentB.relationship)) {
            Long flags = colEventA.collisionEvents.get(collidesComponentB.relationship);
            while (flags != 0) {
                long flag = flags & -flags; // Isolate the lowest set bit (get the first flag)
                int index = Long.numberOfTrailingZeros(flag); // Get the index of the flag (0001 is 0, 0010 is 1 etc...)

                // Trigger the corresponding handler
                events[index].HandleCollision(collidesComponentA.entity, collidesComponentB.entity, contact);

                // Clear the processed bit (ex. 1101 & ~0001 becomes 1100)
                flags &= ~flag;
            }
        }

        if (colEventB != null && colEventB.collisionEvents.containsKey(collidesComponentA.relationship)) {
            Long flags = colEventB.collisionEvents.get(collidesComponentA.relationship);
            while (flags != 0) {
                long flag = flags & -flags; // Isolate the lowest set bit (get the first flag)
                int index = Long.numberOfTrailingZeros(flag); // Get the index of the flag (0001 is 0, 0010 is 1 etc...)

                // Trigger the corresponding handler
                events[index].HandleCollision(collidesComponentB.entity, collidesComponentA.entity, contact);

                // Clear the processed bit (ex. 1101 & ~0001 becomes 1100)
                flags &= ~flag;
            }
        }



    }

    @Override
    public void endContact(Contact contact) {
        CollidesComponent collidesComponentA = (CollidesComponent) contact.getFixtureA().getUserData();
        CollidesComponent collidesComponentB = (CollidesComponent) contact.getFixtureB().getUserData();

        collidesComponentA.collisionCount--;
        collidesComponentB.collisionCount--;
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
