package org.ranobe.hotairballoon.data;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;

public class Wall {
    public Bitmap asteroidBitmap;
    public float x, y, yDiff;
    public Rect position;

    public Wall(Bitmap asteroidBitmap) {
        this.asteroidBitmap = Bitmap.createScaledBitmap(asteroidBitmap, getRandom(), 20, false);
        x = (float) Math.random();
        y = (float) getRandom();
        yDiff = 1;
    }

    public int getRandom() {
        int min = 20;
        int max = 500;
        return (int) (min + Math.random() * (max - min));
    }

    public Matrix next(float speed, int width, int height) {
        if ((y - asteroidBitmap.getHeight()) < height) {
            y += yDiff * speed;
        } else return null;

        float left = x * width, top = y;
        position = new Rect(
                (int) left - (asteroidBitmap.getWidth() / 2),
                (int) top - (asteroidBitmap.getHeight() / 2),
                (int) left + (asteroidBitmap.getWidth() / 2),
                (int) top + (asteroidBitmap.getHeight() / 2)
        );

        Matrix matrix = new Matrix();
        matrix.postTranslate(left, top);
        return matrix;
    }

}
