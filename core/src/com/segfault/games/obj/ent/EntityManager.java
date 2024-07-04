package com.segfault.games.obj.ent;

import com.badlogic.ashley.core.ComponentMapper;
import com.segfault.games.obj.comp.*;

public class EntityManager {
    public ComponentMapper<DrawableComponent> Dm = ComponentMapper.getFor(DrawableComponent.class);
    public ComponentMapper<MovingComponent> Mm = ComponentMapper.getFor(MovingComponent.class);
    public ComponentMapper<RecOwnerComponent> Rm = ComponentMapper.getFor(RecOwnerComponent.class);
    public ComponentMapper<CollidesComponent> Cm = ComponentMapper.getFor(CollidesComponent.class);
    public ComponentMapper<LifetimeComponent> Lm = ComponentMapper.getFor(LifetimeComponent.class);
    public ComponentMapper<SpeedDecreaseComponent> Sm = ComponentMapper.getFor(SpeedDecreaseComponent.class);
    public ComponentMapper<AlphaDecreaseComponent> Am = ComponentMapper.getFor(AlphaDecreaseComponent.class);
    public ComponentMapper<LifeComponent> Lim = ComponentMapper.getFor(LifeComponent.class);
    public ComponentMapper<RectangleCollisionComponent> Rcm = ComponentMapper.getFor(RectangleCollisionComponent.class);
    public ComponentMapper<DamageComponent> Dgm = ComponentMapper.getFor(DamageComponent.class);
    public ComponentMapper<CooldownComponent> Cdm = ComponentMapper.getFor(CooldownComponent.class);
    public ComponentMapper<DisposeOnCollisionComponent> Dim = ComponentMapper.getFor(DisposeOnCollisionComponent.class);
    public ComponentMapper<BounceComponent> Bm = ComponentMapper.getFor(BounceComponent.class);
    public ComponentMapper<TrailComponent> Tm = ComponentMapper.getFor(TrailComponent.class);
    public ComponentMapper<MovementInputComponent> Mim = ComponentMapper.getFor(MovementInputComponent.class);
    public ComponentMapper<PointingComponent> Pm = ComponentMapper.getFor(PointingComponent.class);

}
