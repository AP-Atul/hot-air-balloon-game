package org.ranobe.hotairballoon.entity;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;

public class Wall {
    public Bitmap wallBitmap;
    public float x, y, yDiff;
    public Rect position;

    public Wall(Bitmap wallBitmap) {
        this.wallBitmap = Bitmap.createScaledBitmap(wallBitmap, getRandom(), 20, false);
        x = (float) Math.random();
        y = 0;
        yDiff = 3.2F;
    }

    public int getRandom() {
        int min = 100;
        int max = 500;
        return (int) (min + Math.random() * (max - min));
    }

    public Matrix next(float speed, int width, int height) {
        if ((y - wallBitmap.getHeight()) < height) {
            y += yDiff * speed;
        } else return null;

        float left = x * width, top = y;
        position = new Rect(
                (int) left - (wallBitmap.getWidth() / 2),
                (int) top - (wallBitmap.getHeight() / 2),
                (int) left + (wallBitmap.getWidth() / 2),
                (int) top + (wallBitmap.getHeight() / 2)
        );

        Matrix matrix = new Matrix();
        matrix.postTranslate(left, top);
        return matrix;
    }

}
