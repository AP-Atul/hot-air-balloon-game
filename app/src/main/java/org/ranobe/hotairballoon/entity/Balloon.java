package org.ranobe.hotairballoon.entity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import org.ranobe.hotairballoon.R;
import org.ranobe.hotairballoon.utils.ImageUtils;

public class Balloon {
    private final Bitmap balloonBitmap;
    private final Paint paint;

    public float x;
    public float y;

    public Balloon(Context context, int colorAccent, int colorPrimary) {
        balloonBitmap = ImageUtils.gradientBitmap(
                ImageUtils.getVectorBitmap(context, R.drawable.ic_ship),
                colorAccent,
                colorPrimary
        );
        paint = new Paint();
        paint.setColor(Color.WHITE);
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

    public int getWidth() {
        return balloonBitmap.getWidth();
    }

    public int getHeight() {
        return balloonBitmap.getHeight();
    }

    public float left() {
        return x - (balloonBitmap.getWidth() / 2F);
    }

    public float top() {
        return y - (balloonBitmap.getHeight() / 2F);
    }
}
