package com.segfault.games.obj.sys.phy;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.segfault.games.JavaKnight;
import com.segfault.games.obj.comp.MovingComponent;
import com.segfault.games.obj.comp.SpeedDecreaseComponent;
import com.segfault.games.obj.sys.SubSystem;

public class SpeedDecreaseSystem implements SubSystem {
    private final ComponentMapper<SpeedDecreaseComponent> speedDecreaseMapper;
    private final ComponentMapper<MovingComponent> movementMapper;
    private final Vector2 speed = new Vector2();
    public SpeedDecreaseSystem (JavaKnight ins) {
        movementMapper = ins.GetEntityManager().GetMappers().Moving;
        speedDecreaseMapper = ins.GetEntityManager().GetMappers().SpeedDecrease;
    }

    public void processEntity(Entity entity, float interval, float accumulator) {
        if (entity.isScheduledForRemoval()) return;

        SpeedDecreaseComponent decelerationInfo = speedDecreaseMapper.get(entity);
        MovingComponent movingInfo = movementMapper.get(entity);

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
