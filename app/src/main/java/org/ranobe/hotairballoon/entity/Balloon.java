package org.ranobe.hotairballoon.entity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import org.ranobe.hotairballoon.R;
import org.ranobe.hotairballoon.utils.ImageUtils;

public class Balloon {
    private final Bitmap balloonBitmap;
    private final Paint paint;

    public float x;
    public float y;

    public Balloon(Context context) {
        balloonBitmap = ImageUtils.getVectorBitmap(context, R.drawable.balloon);
        paint = new Paint();
        paint.setColor(Color.GRAY);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
    }

    public void setInitialPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void draw(Canvas canvas) {
        float left = x - (balloonBitmap.getWidth() / 2F);
        float top = y - (balloonBitmap.getHeight() / 2F);
        canvas.drawBitmap(balloonBitmap, left, top, paint);
    }

    public Rect getPosition() {
        return new Rect(left(), top(), right(), bottom());
    }

    public int getWidth() {
        return balloonBitmap.getWidth();
    }

    public int getHeight() {
        return balloonBitmap.getHeight();
    }

    public int left() {
        return (int) (x - (getWidth() / 2F));
    }

    public int top() {
        return (int) (y - (getHeight() / 2F));
    }

    public int right() {
        return (int) (x + getWidth() /2F);
    }

    public int bottom() {
        return (int) (y + getHeight() / 2F);
    }
}
