package org.ranobe.hotairballoon.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;

import androidx.core.content.ContextCompat;

import org.ranobe.hotairballoon.R;
import org.ranobe.hotairballoon.entity.Balloon;
import org.ranobe.hotairballoon.entity.Performance;
import org.ranobe.hotairballoon.entity.Wall;
import org.ranobe.hotairballoon.generator.WallsGenerator;
import org.ranobe.hotairballoon.utils.GameLoop;
import org.ranobe.hotairballoon.utils.MathUtils;

public class GameView extends SurfaceView implements View.OnTouchListener {
    private final Balloon balloon;
    private final WallsGenerator wallsGenerator;
    private GameListener listener;
    public int score;
    private GameLoop gameLoop;
    private final Performance performance;
    private float touchStartPositionX;
    private float speed = 5;
    private final Paint debugPaint;
    private final Paint paint;
    private final int backgroundColor;

    public GameView(Context context) {
        this(context, null);
    }

    public GameView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        gameLoop = new GameLoop(getHolder(), this);
        balloon = new Balloon(context);
        balloon.setInitialPosition(getWidth() / 2F, getHeight() - 50);
        backgroundColor = ContextCompat.getColor(context, R.color.background);

        paint = new Paint();
        paint.setColor(Color.GRAY);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(50);
        paint.setAntiAlias(true);

        debugPaint = new Paint();
        debugPaint.setColor(Color.RED);
        debugPaint.setStyle(Paint.Style.STROKE);
        debugPaint.setStrokeWidth(2);
        debugPaint.setAntiAlias(true);

        performance = new Performance(getContext(), gameLoop);
        wallsGenerator = new WallsGenerator(getContext(), paint, debugPaint);
        wallsGenerator.setMakeWalls(true);
    }

    public void setListener(GameListener listener) {
        this.listener = listener;
    }

    public void onUpdate(Canvas canvas) {
        canvas.drawColor(backgroundColor);
        performance.draw(canvas);
        boolean isPassed = wallsGenerator.draw(canvas, speed);
        if (isPassed) {
            speed += 0.01;
            score += 1;
        }
        Rect position = balloon.getPosition();
        int coinCollected = wallsGenerator.coinCollisionDetection(position);
        if (coinCollected > 0) {
            score += coinCollected * 2;
        }

        canvas.drawRect(position, debugPaint);
        Wall collision = wallsGenerator.wallCollisionDetection(position);
        if (collision != null) {
            new Handler(Looper.getMainLooper()).post(() -> listener.died());
        }

        balloon.draw(canvas);
        canvas.drawText("Score: " + score, 100, 300, paint);
    }

    public void play() {
        setOnTouchListener(this);
        wallsGenerator.setMakeWalls(true);

        if (gameLoop == null) {
            gameLoop = new GameLoop(getHolder(), this);
        }

        if (!gameLoop.isRunning()) {
            balloon.setInitialPosition(getWidth() / 2F, MathUtils.getXPercentOf(getHeight(), 80));
        }

        gameLoop.startLoop();
    }

    public void stop() {
        setOnTouchListener(null);
        wallsGenerator.setMakeWalls(false);

        if (gameLoop != null) {
            gameLoop.stopLoop();
            gameLoop = null;
        }
    }

    public void onPause() {
        if (gameLoop != null && gameLoop.isRunning()) {
            gameLoop.stopLoop();
        }
    }

    public void onResume() {
        if (gameLoop != null && gameLoop.isRunning()) {
            gameLoop.stopLoop();
        }
    }

    public boolean isOutOfGameViewWidth(float x) {
        return (x < 0 || x > getWidth());
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            touchStartPositionX = event.getX();
        }

        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            float diffX = touchStartPositionX - event.getX();
            float newX = balloon.x - diffX;

            if (isOutOfGameViewWidth(newX)) {
                return true;
            }

            balloon.x = newX;
            touchStartPositionX = event.getX();
        }

        return true;
    }

    public interface GameListener {
        void died();
    }
}
