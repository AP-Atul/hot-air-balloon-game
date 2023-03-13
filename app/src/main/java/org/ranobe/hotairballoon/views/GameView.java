package org.ranobe.hotairballoon.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.core.content.ContextCompat;

import org.ranobe.hotairballoon.R;
import org.ranobe.hotairballoon.entity.Balloon;
import org.ranobe.hotairballoon.entity.Wall;
import org.ranobe.hotairballoon.generator.WallsGenerator;
import org.ranobe.hotairballoon.utils.GameLoop;

public class GameView extends SurfaceView implements View.OnTouchListener {
    private final Balloon balloon;
    private final WallsGenerator wallsGenerator;
    public int score;
    private GameLoop gameLoop;
    private float touchStartPositionX;
    private float touchStartPositionY;
    private float speed = 1;

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
        balloon = new Balloon(context, colorAccent, colorPrimary);
        balloon.setInitialPosition(getWidth() / 2F, getHeight() - 50);

        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        Paint accentPaint = new Paint();
        accentPaint.setColor(colorPrimaryLight);
        accentPaint.setStyle(Paint.Style.FILL);
        accentPaint.setAntiAlias(true);

        wallsGenerator = new WallsGenerator(getContext(), paint, accentPaint);
        wallsGenerator.setMakeWalls(true);
    }

    public void onUpdate(Canvas canvas) {
        canvas.drawColor(Color.WHITE);

        balloon.draw(canvas);
        boolean isPassed = wallsGenerator.draw(canvas, speed);

        if (isPassed) {
            speed += 0.2;
            score += 1;
        }

        Rect position = balloon.getPosition();
        Wall collision = wallsGenerator.wallCollisionDetection(position);
        if (collision != null) {
            // bro we lose;
            score = 0;
        }
    }

    public void play() {
        setOnTouchListener(this);
        wallsGenerator.setMakeWalls(true);

        if (gameLoop == null) {
            gameLoop = new GameLoop(getHolder(), this);
        }

        if (!gameLoop.isRunning()) {
            balloon.setInitialPosition(getWidth() / 2F, getHeight() - 50);
            gameLoop.start(true);
        } else {
            gameLoop.unpause();
        }
    }

    public void stop() {
        setOnTouchListener(null);
        wallsGenerator.setMakeWalls(false);

        ValueAnimator animator = ValueAnimator.ofFloat(balloon.y, getHeight() - 100F);
        animator.setDuration(1000);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(valueAnimator -> balloon.y = (float) valueAnimator.getAnimatedValue());
        animator.start();

        ValueAnimator animator1 = ValueAnimator.ofFloat(speed, 1);
        animator1.setDuration(150);
        animator1.setInterpolator(new DecelerateInterpolator());
        animator1.addUpdateListener(valueAnimator -> speed = (float) valueAnimator.getAnimatedValue());
        animator1.start();

        if (gameLoop != null) {
            gameLoop.end();
            gameLoop = null;
        }
    }

    public void onPause() {
        if (gameLoop != null && gameLoop.isRunning()) {
            gameLoop.pause();
        }
    }

    public void onResume() {
        if (gameLoop != null && gameLoop.isRunning()) {
            gameLoop.unpause();
        }
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
