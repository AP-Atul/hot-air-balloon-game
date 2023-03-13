package org.ranobe.hotairballoon.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import org.ranobe.hotairballoon.entity.Balloon;
import org.ranobe.hotairballoon.GameLoop;
import org.ranobe.hotairballoon.R;
import org.ranobe.hotairballoon.data.Wall;
import org.ranobe.hotairballoon.data.drawer.AsteroidDrawer;

public class GameView extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener {

    private final GameLoop gameLoop;
    private final Balloon balloon;
    private final AsteroidDrawer asteroids;
    public int score;
    private float touchStartPositionX;
    private float touchStartPositionY;
    private float speed = 3;

    public GameView(Context context) {
        this(context, null);
    }

    public GameView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        int colorPrimaryLight = ContextCompat.getColor(getContext(), R.color.colorPrimaryLight);
        int colorPrimary = ContextCompat.getColor(getContext(), R.color.colorPrimary);
        int colorAccent = ContextCompat.getColor(getContext(), R.color.colorAccent);

        gameLoop = new GameLoop(getHolder(), this);
        getHolder().addCallback(this);

        balloon = new Balloon(context, colorAccent, colorPrimary);

        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        Paint accentPaint = new Paint();
        accentPaint.setColor(colorPrimaryLight);
        accentPaint.setStyle(Paint.Style.FILL);
        accentPaint.setAntiAlias(true);

        asteroids = new AsteroidDrawer(getContext(), colorAccent, colorPrimary, paint, accentPaint);
    }

    public void onUpdate(Canvas canvas) {
        canvas.drawColor(Color.WHITE);

        if (asteroids.size() == 0) {
            asteroids.makeNew();
        }

        asteroids.draw(canvas, speed);
        balloon.draw(canvas);

        // collision detection
        float left = balloon.left();
        float top = balloon.top();
        int width = balloon.getWidth();
        Rect position = new Rect(
                (int) left - (width / 2),
                (int) top - (width / 2),
                (int) left + (width / 2),
                (int) top + (width / 2)
        );

        Wall asteroid = asteroids.asteroidAt(position);
        if (asteroid != null) {
            new Handler(Looper.getMainLooper()).post(() -> {
//                        listener.onAsteroidCrashed();
//                        listener.onStop(score);
//                    stop();
            });
        }
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        gameLoop.start(true);
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        // Unused
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
        gameLoop.start(false);
    }

    public void play() {
        score = 0;
        speed = 5;
        balloon.setInitialPosition(getWidth() / 2F, getHeight() - 50);

        setOnTouchListener(this);
        gameLoop.start(true);
        gameLoop.start();
    }

    public void stop() {
        setOnTouchListener(null);
        asteroids.setMakeAsteroids(false);

        ValueAnimator animator = ValueAnimator.ofFloat(balloon.y, getHeight() - 100F);
        animator.setDuration(1000);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(valueAnimator -> {
            balloon.y = (float) valueAnimator.getAnimatedValue();
        });
        animator.start();

        ValueAnimator animator1 = ValueAnimator.ofFloat(speed, 1);
        animator1.setDuration(150);
        animator1.setInterpolator(new DecelerateInterpolator());
        animator1.addUpdateListener(valueAnimator -> speed = (float) valueAnimator.getAnimatedValue());
        animator1.start();
    }

    public boolean isPlaying() {
        return gameLoop.isRunning();
    }

    public void onPause() {
        if (gameLoop != null && gameLoop.isRunning()) {
            gameLoop.start(false);
        }
    }

    public void onResume() {
        onPause();
    }

    public boolean isOutOfGameView(float x, float y) {
        return (x < 0 || x > getWidth() || y < 0 || y > getHeight());
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            touchStartPositionX = event.getX();
            touchStartPositionY = event.getY();
        }

        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            float diffX = touchStartPositionX - event.getX();
            float diffY = touchStartPositionY - event.getY();
            float newX = balloon.x - diffX;
            float newY = balloon.y - diffY;

            if (isOutOfGameView(newX, newY)) {
                return true;
            }

            balloon.x = newX;
            balloon.y = newY;

            touchStartPositionX = event.getX();
            touchStartPositionY = event.getY();
        }

        return true;
    }

}
