package com.segfault.games.obj;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.segfault.games.obj.abs.Object;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.function.Supplier;

public class Rec extends Object {

    public Vector2[] Points = new Vector2[4]; // Array to hold the vertices of the rectangle
    public Supplier update; // Supplier function for updating the rectangle
    public int Width;
    public int Height;

    public int X;
    public int Y;
    public double angle;
    // Constructor to initialize the rectangle with position, dimensions, and color
    public Rec(float x, float y, float w, float h, Color clr) {
        // Define the vertices of the rectangle
        Points[0] = new Vector2(x - w / 2, y - h / 2); // Bottom-left corner
        Points[1] = new Vector2(x + w / 2, y - h / 2); // Bottom-right corner
        Points[2] = new Vector2(x + w / 2, y + h / 2); // Top-right corner
        Points[3] = new Vector2(x - w / 2, y + h / 2); // Top-left corner

        X = (int) x;    // Set the X-coordinate of the rectangle's position
        Y = (int) y;    // Set the Y-coordinate of the rectangle's position
        Width = (int) w;   // Set the width of the rectangle
        Height = (int) h;   // Set the height of the rectangle

        if (sprite != null) {
            sprite.setOriginCenter();
            sprite.setPosition(X, Y);
            sprite.setSize(Width, Height);
        }
    }

    // Method to rotate the rectangle around a specified origin point
    public void Rotate(double angle, double originX, double originY) {

        AffineTransform tr = new AffineTransform(); // Create a new AffineTransform for rotation
        tr.rotate(angle, originX, originY); // Rotate around the specified origin point

        for (int i = 0; i < Points.length; i++) {
            Point2D.Double src = new Point2D.Double(Points[i].x, Points[i].y); // Source point
            Point2D.Double dst = new Point2D.Double(); // Destination point
            tr.transform(src, dst); // Apply the transformation
            Points[i] = new Vector2((float) dst.getX(), (float) dst.getY()); // Update the rotated vertex
        }
    }

    public void MoveTo(double X, double Y, int originX, int originY, double angle) {

        Points[0] = new Vector2((float) (X - (double) Width / 2), (float) (Y - (double) Height / 2));
        Points[1] = new Vector2((float) (X + (double) Width / 2), (float) (Y - (double) Height / 2));
        Points[2] = new Vector2((float) (X + (double) Width / 2), (float) (Y + (double) Height / 2));
        Points[3] = new Vector2((float) (X - (double) Width / 2), (float) (Y + (double) Height / 2));

        if (angle != 0) {
            AffineTransform tr = new AffineTransform();
            tr.rotate(angle, originX, originY);
            for (int i = 0; i < Points.length; i++) {
                Point2D.Double src = new Point2D.Double(Points[i].x, Points[i].y);
                Point2D.Double dst = new Point2D.Double();
                tr.transform(src, dst);
                Points[i] = new Vector2((float) dst.getX(), (float) dst.getY());
            }
        }

        this.X = (int) X;
        this.Y = (int) Y;

        if (sprite != null) sprite.setPosition((float) X, (float) Y);
    }
    // Method to move the rectangle to a new position
    public void Move(double X, double Y) {

        for (int i = 0; i < Points.length; i++) {
            Points[i].x += (float) X; // Update X coordinate of each vertex
            Points[i].y += (float) Y; // Update Y coordinate of each vertex
        }

        this.X += (int) X; // Update the X coordinate of the rectangle's position
        this.Y += (int) Y; // Update the Y coordinate of the rectangle's position

        sprite.setPosition(this.X, this.Y);
    }


    // Override the draw method to draw the rectangle
    @Override
    public void draw(SpriteBatch b) {
        super.draw(b); // Call the superclass draw method (draws the image if available)
    }

    @Override
    public void dispose() {

    }

    // Override the update method to update the rectangle's state
    @Override
    public void update() {
        if (update == null) return;
        update.get(); // Invoke the supplier function for updating the rectangle
    }
}
