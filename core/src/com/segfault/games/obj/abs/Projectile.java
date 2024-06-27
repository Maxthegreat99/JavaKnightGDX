package com.segfault.games.obj.abs;

import com.badlogic.gdx.graphics.g2d.Sprite;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class Projectile extends Object {
    public double speed;
    public double angle;
    public Object owner;

    // Constructor with parameters
    public Projectile(Sprite spr, int x, int y) {
        super(spr, x, y); // Call super constructor from PhysicObject

        // Initialize instance variables
        this.speed = 0;
        this.angle = 0;
        this.owner = null;
    }

    // Default constructor
    public Projectile() {
        // No additional initialization needed
    }
}
