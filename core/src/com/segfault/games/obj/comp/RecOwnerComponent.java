package com.segfault.games.obj.comp;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;
import com.segfault.games.obj.Rec;

public class RecOwnerComponent implements Component, Pool.Poolable {
    public Rec rectangle = null;
    @Override
    public void reset() {
        rectangle = null;
    }
}
