package com.segfault.games.util;

import com.badlogic.ashley.core.ComponentMapper;
import com.segfault.games.obj.comp.*;

public class EntityManager {
    public ComponentMapper<DrawableComponent> Dm = ComponentMapper.getFor(DrawableComponent.class);
    public ComponentMapper<MovingComponent> Mm = ComponentMapper.getFor(MovingComponent.class);
    public ComponentMapper<RecOwnerComponent> Rm = ComponentMapper.getFor(RecOwnerComponent.class);
    public ComponentMapper<CollidesComponent> Cm = ComponentMapper.getFor(CollidesComponent.class);
    public ComponentMapper<LifetimeComponent> Lm = ComponentMapper.getFor(LifetimeComponent.class);
    public ComponentMapper<DecelerationComponent> Sm = ComponentMapper.getFor(DecelerationComponent.class);
    public ComponentMapper<DecreasingAlplaComponent> Am = ComponentMapper.getFor(DecreasingAlplaComponent.class);
    public ComponentMapper<LifeComponent> Lim = ComponentMapper.getFor(LifeComponent.class);
    public ComponentMapper<RectangleCollisionComponent> Rcm = ComponentMapper.getFor(RectangleCollisionComponent.class);
    public ComponentMapper<DamageComponent> Dgm = ComponentMapper.getFor(DamageComponent.class);
    public ComponentMapper<ColRelationshipHolderComponent> Chm = ComponentMapper.getFor(ColRelationshipHolderComponent.class);
}
