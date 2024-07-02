package com.segfault.games.obj;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;

import java.awt.*;
import java.util.function.Supplier;

public class Rec {

    public Vector2[] Points = new Vector2[4]; // Array to hold the vertices of the rectangle

    public int Width;
    public int Height;

    public int X;
    public int Y;

    public float angle = 0.0f;

    private final Vector2 normal = new Vector2();
    private final Matrix3 tr = new Matrix3();
    // Constructor to initialize the rectangle with position, dimensions, and color
    public Rec(float x, float y, float w, float h) {
        // Define the vertices of the rectangle
        Points[0].set(x - w / 2, y - h / 2); // Bottom-left corner
        Points[1].set(x + w / 2, y - h / 2); // Bottom-right corner
        Points[2].set(x + w / 2, y + h / 2); // Top-right corner
        Points[3].set(x - w / 2, y + h / 2); // Top-left corner

        X = (int) x;    // Set the X-coordinate of the rectangle's position
        Y = (int) y;    // Set the Y-coordinate of the rectangle's position
        Width = (int) w;   // Set the width of the rectangle
        Height = (int) h;   // Set the height of the rectangle

    }

    // Method to rotate the rectangle around a specified origin point
    public void Rotate(float angle, float originX, float originY) {

        tr.translate(-originX, -originY);
        tr.rotate(angle);
        tr.translate(originX, originY);
        for (Vector2 point : Points)
            point.mul(tr); // Update the rotated vertex


        tr.idt();
        this.angle = angle;

    }

    public void MoveTo(float X, float Y, float originX, float originY, float angle) {

        Points[0].set(X - Width / 2f, Y - Height / 2f);
        Points[1].set(X + Width / 2f, Y - Height / 2f);
        Points[2].set(X + Width / 2f, Y + Height / 2f);
        Points[3].set(X - Width / 2f, Y + Height / 2f);

        if (Float.compare(angle, 0f) != 0) {
            tr.translate(-originX, -originY);
            tr.rotate(angle);
            tr.translate(originX, originY);
            for (Vector2 point : Points)
                point.mul(tr);

            tr.idt();
        }

        this.X = (int) X;
        this.Y = (int) Y;

        this.angle = angle;
    }
    // Method to move the rectangle to a new position
    public void Move(double X, double Y) {

        for (Vector2 point : Points) {
            point.x += (float) X; // Update X coordinate of each vertex
            point.y += (float) Y; // Update Y coordinate of each vertex
        }

        this.X += (int) X; // Update the X coordinate of the rectangle's position
        this.Y += (int) Y; // Update the Y coordinate of the rectangle's position

    }
    public boolean IsPolygonsIntersecting(Rec b)
    {
        Rec a = this;

        for ( Rec rect : new Rec[] { a, b })
        {
            for (int i1 = 0; i1 < rect.Points.length; i1++)
            {
                int i2 = (i1 + 1) % rect.Points.length;
                Vector2 p1 = rect.Points[i1];
                Vector2 p2 = rect.Points[i2];

                normal.set(p2.y - p1.y, p1.x - p2.x);

                double minA = Double.NaN;
                double maxA = Double.NaN;
                for (Vector2 p : a.Points)
                {
                    float projected = normal.x * p.x + normal.y * p.y;
                    if (Double.isNaN(minA) || projected < minA)
                        minA = projected;
                    if (Double.isNaN(maxA) || projected > maxA)
                        maxA = projected;
                }

                double minB = Double.NaN;
                double maxB = Double.NaN;
                for (Vector2 p : b.Points)
                {
                    float projected = normal.x * p.x + normal.y * p.y;
                    if (Double.isNaN(minB) || projected < minB)
                        minB = projected;
                    if ( Double.isNaN(maxB) || projected > maxB)
                        maxB = projected;
                }

                if (maxA < minB || maxB < minA)
                    return false;
            }
        }
        return true;
    }


}
