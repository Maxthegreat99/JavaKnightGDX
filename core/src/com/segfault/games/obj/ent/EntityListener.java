package com.segfault.games.obj.ent;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;
import com.segfault.games.JavaKnight;

public class EntityListener implements com.badlogic.ashley.core.EntityListener {
    private JavaKnight instance;
    public EntityListener(JavaKnight instance) {
        this.instance = instance;
    }
    @Override
    public void entityAdded(Entity entity) {

    }

    @Override
    public void entityRemoved(Entity entity) {
        for (Component c : entity.getComponents())
            ((com.segfault.games.obj.comp.Component)c).dispose(instance);

    }
}
