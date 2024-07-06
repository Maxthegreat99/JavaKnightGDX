package com.segfault.games.obj;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

/**
 * Rectangle object, used for collision detection requiring rotated areas, cannot be used as a solid collision
 */
public class Rec {

    /** points of the rectangle, do not rely on the position of the points to know
     * the which corner each points are as the rectangle can be rotated
     */
    public Vector2[] Points = {new Vector2(), new Vector2(), new Vector2(), new Vector2()}; // Array to hold the vertices of the rectangle

    public float Width;
    public float Height;

    public float X;
    public float Y;

    public float OriginX;
    public float OriginY;

    /**
     * current angle of the rectangle, modyfying it wont rotate the rectangle
     */
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

    /**
     * Method to rotate the rectangle around a specified origin point
     * note this will add to the current rotation and not set the rotation
     */
    public void Rotate(float angle, float originX, float originY) {

        if (angle < 0 || angle > 360)
            angle = (angle % 360);

        for (Vector2 point : Points) {
            float tempX = point.x;
            float tempY = point.y;

            float cos = MathUtils.cosDeg(angle);
            float sin = MathUtils.sinDeg(angle);

            point.x = (tempX - originX) * cos - (tempY - originY) * sin + originX;
            point.y = (tempX - originX) * sin + (tempY - originY) * cos + originY;
        }

        this.angle += angle;
        if (this.angle < 0 || this.angle > 360)
            this.angle = (this.angle % 360);
        this.OriginX = originX;
        this.OriginY = originY;

    }

    /**
     * This completely reset calculates the rectangle's points around the set positions
     * and with the set origins for rotation, when possible use Move instead
     * @param X
     * @param Y
     * @param originX
     * @param originY
     * @param angle
     */
    public void MoveTo(float X, float Y, float originX, float originY, float angle) {

        Points[0].set(X - Width / 2f, Y - Height / 2f);
        Points[1].set(X + Width / 2f, Y - Height / 2f);
        Points[2].set(X + Width / 2f, Y + Height / 2f);
        Points[3].set(X - Width / 2f, Y + Height / 2f);

        if (Float.compare(angle, 0f) != 0) {

            for (Vector2 point : Points) {
                float tempX = point.x;
                float tempY = point.y;

                float cos = MathUtils.cosDeg(angle);
                float sin = MathUtils.sinDeg(angle);

                point.x = (tempX - originX) * cos - (tempY - originY) * sin + originX;
                point.y = (tempX - originX) * sin + (tempY - originY) * cos + originY;
            }

        }

        this.X = X;
        this.Y = Y;
        OriginX = originX;
        OriginY = originY;
        this.angle = angle;
    }

    /**
     * adds said values to the rectangle's points
     * @param X
     * @param Y
     */
    public void Move(float X, float Y) {

        for (Vector2 point : Points) {
            point.x += X; // Update X coordinate of each vertex
            point.y += Y; // Update Y coordinate of each vertex
        }

        this.X += X; // Update the X coordinate of the rectangle's position
        this.Y += Y; // Update the Y coordinate of the rectangle's position

    }

    /**
     * black box checking if a collision is made with another rectangle
     */
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

    private final float[] floatArray = new float[Points.length * 2];

    /**
     * drops the (x,y) components of the rectangle's points into a float array ending looking like this:
     * {x, y, x, y ... }
     */
    public float[] ConvertToFloatArray() {
        // Each Vector2 has two float values (x and y), so the resulting float array will have twice the length of the Vector2 array

        for (int i = 0; i < Points.length; i++) {
            floatArray[i * 2] = Points[i].x;     // Store the x value
            floatArray[i * 2 + 1] = Points[i].y; // Store the y value
        }

        return floatArray;
    }

}
