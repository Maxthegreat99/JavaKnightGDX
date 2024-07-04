package com.segfault.games.obj.comp;

import com.badlogic.ashley.core.Entity;
import com.segfault.games.JavaKnight;

/**
 * Component used to create protoype objects
 * for spawning, no real other uses in the engine
 */
public class PrototypeComp extends Component{
    @Override
    public void dispose(JavaKnight instance) {

    }

    @Override
    public Component Clone(JavaKnight instance, Entity ent) {
        return null;
    }

    @Override
    public void reset() {

    }
}
