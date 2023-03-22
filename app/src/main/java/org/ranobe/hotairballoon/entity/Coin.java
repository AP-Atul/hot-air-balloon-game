package org.ranobe.hotairballoon.entity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import org.ranobe.hotairballoon.utils.MathUtils;

public class Coin {
    public Bitmap coinBitmap;
    public float x, y, yDiff;
    public Rect position;

    public Coin(Bitmap coinBitmap, int windowWidth) {
        this.coinBitmap = coinBitmap;
        x = MathUtils.getRandom(0, windowWidth);
        y = 0;
        yDiff = 3.2F;
        position = new Rect(left(), top(), right(), bottom());
    }

    public int getWidth() {
        return coinBitmap.getWidth();
    }

    public int getHeight() {
        return coinBitmap.getHeight();
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
        if ((y - coinBitmap.getHeight()) < canvas.getHeight()) {
            y += yDiff * speed;
        }
        position = new Rect(left(), top(), right(), bottom());
        return position;
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(coinBitmap, left(), top(), null);
    }

}
