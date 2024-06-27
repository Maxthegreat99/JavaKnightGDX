package com.segfault.games.obj.abs;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

// Abstract class representing a game object
public abstract class Object {
    public Sprite sprite = null;  // Image of the object
    public Object() {
        // Default constructor
    }

    // Constructor to initialize object properties
    public Object(Sprite spr, int x, int y) {
        sprite = spr;
        sprite.setOriginCenter();
        sprite.setPosition(x, y);
    }

    // Abstract method for updating object state (to be implemented by subclasses)
    public void update(){
        return;
    }

    // Method to draw the object (rendering)
    public void draw(SpriteBatch b) {
        b.disableBlending();
        if (sprite != null)  // If there's an image, draw it at the specified position and size
            sprite.draw(b);
        b.enableBlending();
    }

    // Abstract method to dispose of the object (to be implemented by subclasses)
    public void dispose() {

    }
}
