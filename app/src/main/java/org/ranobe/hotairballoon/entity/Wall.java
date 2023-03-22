package org.ranobe.hotairballoon.entity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import org.ranobe.hotairballoon.utils.MathUtils;

public class Wall {
    public Bitmap wallBitmap;
    public float x, y, yDiff;
    public Rect position;

    public Wall(Bitmap wallBitmap, int windowWidth) {
//        int width = (int) MathUtils.getRandom(
//                MathUtils.getXPercentOf(windowWidth, 10),
//                MathUtils.getXPercentOf(windowWidth, 70)
//        );
//        this.wallBitmap = Bitmap.createScaledBitmap(wallBitmap, width, 50, false);
        this.wallBitmap = wallBitmap;
        x = MathUtils.getRandom(0, windowWidth);
        y = 0;
        yDiff = 3.2F;
        position = new Rect(left(), top(), right(), bottom());
    }

    public int getWidth() {
        return wallBitmap.getWidth();
    }

    public int getHeight() {
        return wallBitmap.getHeight();
    }

    public int left() {
        return (int) (x - (getWidth() / 2F));
    }

    public int top() {
        return (int) (y - (getHeight() / 2F));
    }

    public int right() {
        return (int) (x + getWidth() / 2F);
    }

    public int bottom() {
        return (int) (y + getHeight() / 2F);
    }

    public Rect next(float speed, Canvas canvas) {
        if ((y - wallBitmap.getHeight()) < canvas.getHeight()) {
            y += yDiff * speed;
        }
        position = new Rect(left(), top(), right(), bottom());
        return position;
    }

}
