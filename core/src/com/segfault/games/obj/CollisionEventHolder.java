package com.segfault.games.obj;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.segfault.games.obj.sys.SubSystem;

public class CollisionEventHolder implements Pool.Poolable {
    public Array<SubSystem> event = new Array<>();
    public boolean hasInvoked = false;

    @Override
    public void reset() {
        event.clear();
        hasInvoked = false;
    }
}
