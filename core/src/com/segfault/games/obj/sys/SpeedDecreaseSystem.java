package com.segfault.games.obj.sys;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.segfault.games.JavaKnight;
import com.segfault.games.obj.comp.SpeedDecreaseComponent;
import com.segfault.games.obj.comp.MovingComponent;

public class SpeedDecreaseSystem extends IteratingSystem {
    private final ComponentMapper<SpeedDecreaseComponent> speedDecreaseMapper;
    private final ComponentMapper<MovingComponent> movementMapper;
    private final Vector2 speed = new Vector2();
    public SpeedDecreaseSystem (JavaKnight ins, int priority) {
        super(Family.all(SpeedDecreaseComponent.class, MovingComponent.class).get());
        movementMapper = ins.EntityManager.Mm;
        speedDecreaseMapper = ins.EntityManager.Sm;
        this.priority = priority;
    }
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        if (entity.isScheduledForRemoval()) return;

        MovingComponent movingInfo = movementMapper.get(entity);
        SpeedDecreaseComponent decelerationInfo = speedDecreaseMapper.get(entity);

        speed.set(movingInfo.dx, movingInfo.dy);

        float s2 = speed.len2();

        if (Float.compare(s2, 0f) == 0) return;

        float comparator = (decelerationInfo.comparator * decelerationInfo.comparator);
        float cos = MathUtils.cosDeg(speed.angleDeg());
        float sin = MathUtils.sinDeg(speed.angleDeg());

        if (s2 > comparator)
            speed.sub(cos * decelerationInfo.decelerationValue * 2,
                      sin * decelerationInfo.decelerationValue * 2);

        else if (s2 > comparator / 2)
            speed.sub(cos * decelerationInfo.decelerationValue,
                      sin * decelerationInfo.decelerationValue);

        else speed.sub(cos / 2 * decelerationInfo.decelerationValue,
             sin / 2 * decelerationInfo.decelerationValue);

        movingInfo.dx = speed.x;
        movingInfo.dy = speed.y;

    }
}
