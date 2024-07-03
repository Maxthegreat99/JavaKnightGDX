package com.segfault.games.obj;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;

public class Rec {

    public Vector2[] Points = {new Vector2(), new Vector2(), new Vector2(), new Vector2()}; // Array to hold the vertices of the rectangle

    public float Width;
    public float Height;

    public float X;
    public float Y;

    public float OriginX;
    public float OriginY;

    public float angle = 0.0f;

    private final Vector2 normal = new Vector2();

    // Constructor to initialize the rectangle with position, dimensions, and color
    public Rec(float x, float y, float w, float h) {
        // Define the vertices of the rectangle
        Points[0].set(x - w / 2, y - h / 2); // Bottom-left corner
        Points[1].set(x + w / 2, y - h / 2); // Bottom-right corner
        Points[2].set(x + w / 2, y + h / 2); // Top-right corner
        Points[3].set(x - w / 2, y + h / 2); // Top-left corner

        OriginX = X = x;    // Set the X-coordinate of the rectangle's position
        OriginY = Y = y;    // Set the Y-coordinate of the rectangle's position
        Width = w;   // Set the width of the rectangle
        Height = h;   // Set the height of the rectangle

    }

    // Method to rotate the rectangle around a specified origin point
    public void Rotate(float angle, float originX, float originY) {


        for (Vector2 point : Points) {
            float tempX = point.x;
            float tempY = point.y;

            point.x = (tempX - originX) * MathUtils.cos((float) Math.toRadians(angle)) - (tempY - originY) * MathUtils.sin((float) Math.toRadians(angle)) + originX;
            point.y = (tempX - originX) * MathUtils.sin((float) Math.toRadians(angle)) + (tempY - originY) * MathUtils.cos((float) Math.toRadians(angle)) + originY;
        }


        this.angle += angle;
        this.OriginX = originX;
        this.OriginY = originY;



    }

    public void MoveTo(float X, float Y, float originX, float originY, float angle) {

        Points[0].set(X - Width / 2f, Y - Height / 2f);
        Points[1].set(X + Width / 2f, Y - Height / 2f);
        Points[2].set(X + Width / 2f, Y + Height / 2f);
        Points[3].set(X - Width / 2f, Y + Height / 2f);

        if (Float.compare(angle, 0f) != 0) {

            for (Vector2 point : Points) {
                float tempX = point.x;
                float tempY = point.y;

                point.x = (tempX - originX) * MathUtils.cos((float) Math.toRadians(angle)) - (tempY - originY) * MathUtils.sin((float) Math.toRadians(angle)) + originX;
                point.y = (tempX - originX) * MathUtils.sin((float) Math.toRadians(angle)) + (tempY - originY) * MathUtils.cos((float) Math.toRadians(angle)) + originY;
            }

        }

        this.X = X;
        this.Y = Y;

        this.angle = angle;
    }
    // Method to move the rectangle to a new position
    public void Move(float X, float Y) {

        for (Vector2 point : Points) {
            point.x += X; // Update X coordinate of each vertex
            point.y += Y; // Update Y coordinate of each vertex
        }

        this.X += X; // Update the X coordinate of the rectangle's position
        this.Y += Y; // Update the Y coordinate of the rectangle's position

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
    public float[] ConvertToFloatArray() {
        // Each Vector2 has two float values (x and y), so the resulting float array will have twice the length of the Vector2 array
        float[] floatArray = new float[Points.length * 2];

        for (int i = 0; i < Points.length; i++) {
            floatArray[i * 2] = Points[i].x;     // Store the x value
            floatArray[i * 2 + 1] = Points[i].y; // Store the y value
        }

        return floatArray;
    }

}
