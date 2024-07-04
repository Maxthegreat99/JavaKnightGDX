package com.segfault.games.obj.sys;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
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

        if (s2 > -decelerationInfo.stopSpeed2 && s2 < decelerationInfo.stopSpeed2) return;

        float dx = MathUtils.cosDeg(speed.angleDeg()) * decelerationInfo.decelerationValue * Gdx.graphics.getDeltaTime();
        float dy = MathUtils.sinDeg(speed.angleDeg()) * decelerationInfo.decelerationValue * Gdx.graphics.getDeltaTime();

        if (s2 > decelerationInfo.comparator2)
            speed.sub(dx * 2, dy * 2);

        else if (s2 > decelerationInfo.comparator2 / 2)
            speed.sub(dx, dy);

        else speed.sub(dx / 2, dy / 2);

        movingInfo.dx = speed.x;
        movingInfo.dy = speed.y;

    }
}
