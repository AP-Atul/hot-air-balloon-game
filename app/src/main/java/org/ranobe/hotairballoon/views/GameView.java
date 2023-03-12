package org.ranobe.hotairballoon.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import androidx.core.content.ContextCompat;

import org.ranobe.hotairballoon.R;
import org.ranobe.hotairballoon.data.AsteroidData;
import org.ranobe.hotairballoon.data.drawer.AsteroidDrawer;
import org.ranobe.hotairballoon.data.drawer.BackgroundDrawer;
import org.ranobe.hotairballoon.data.drawer.MessageDrawer;
import org.ranobe.hotairballoon.utils.ImageUtils;

public class GameView extends SurfaceView implements Runnable, View.OnTouchListener {

    private static final int TUTORIAL_NONE = 0;
    private static final int TUTORIAL_MOVE = 1;
    private static final int TUTORIAL_UPGRADE = 2;
    public int score;
    private Paint paint;
    private Paint accentPaint;
    private SurfaceHolder surfaceHolder;
    private boolean isRunning;
    private Thread thread;
    private Bitmap shipBitmap;
    private float shipPositionX = 0.5f;
    private float shipPositionY = -1f;
    private float shipPositionStartX;
    private float shipRotation;
    private long projectileTime;
    private BackgroundDrawer background;
    private AsteroidDrawer asteroids;
    private MessageDrawer messages;
    private ValueAnimator animator;
    private GameListener listener;
    private boolean isPlaying;
    private float speed = 5;
    private int tutorial;

    public GameView(Context context) {
        this(context, null);
    }

    public GameView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        surfaceHolder = getHolder();

        int colorPrimaryLight = ContextCompat.getColor(getContext(), R.color.colorPrimaryLight);
        int colorPrimary = ContextCompat.getColor(getContext(), R.color.colorPrimary);
        int colorAccent = ContextCompat.getColor(getContext(), R.color.colorAccent);

        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        accentPaint = new Paint();
        accentPaint.setColor(colorPrimaryLight);
        accentPaint.setStyle(Paint.Style.FILL);
        accentPaint.setAntiAlias(true);

        Paint cloudPaint = new Paint();
        cloudPaint.setColor(colorPrimaryLight);
        cloudPaint.setAlpha(50);
        cloudPaint.setStyle(Paint.Style.FILL);
        cloudPaint.setAntiAlias(true);

        Paint textPaint = new Paint();
        textPaint.setColor(colorPrimaryLight);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(20);

        background = new BackgroundDrawer(paint, cloudPaint);
        asteroids = new AsteroidDrawer(getContext(), colorAccent, colorPrimary, paint, accentPaint);
        messages = new MessageDrawer(textPaint);
        shipBitmap = ImageUtils.gradientBitmap(ImageUtils.getVectorBitmap(getContext(), R.drawable.ic_ship), colorAccent, colorPrimary);
    }

    public void setListener(GameListener listener) {
        this.listener = listener;
    }

    @Override
    public void run() {
        while (isRunning) {
            if (surfaceHolder.getSurface().isValid()) {
                Canvas canvas = surfaceHolder.lockCanvas();
                if (canvas == null)
                    return;

                canvas.drawColor(Color.BLACK);

                if (asteroids.size() == 0) {
                    asteroids.makeNew();
                    messages.clear();
                }

                background.draw(canvas, speed);
                asteroids.draw(canvas, speed);

                float left = canvas.getWidth() * shipPositionX;
                float top = canvas.getHeight() - (shipBitmap.getHeight() * shipPositionY);

                Matrix matrix = new Matrix();
                matrix.postTranslate(-shipBitmap.getWidth() / 2, -shipBitmap.getHeight() / 2);
                matrix.postRotate(shipRotation);
                matrix.postTranslate(left, top);
                canvas.drawBitmap(shipBitmap, matrix, paint);

                Rect position = new Rect(
                        (int) left - (shipBitmap.getWidth() / 2),
                        (int) top - (shipBitmap.getWidth() / 2),
                        (int) left + (shipBitmap.getWidth() / 2),
                        (int) top + (shipBitmap.getWidth() / 2)
                );


                if (isPlaying) {
                    AsteroidData asteroid = asteroids.asteroidAt(position);
                    if (asteroid != null) {
                        new Handler(Looper.getMainLooper()).post(() -> {
                            if (listener != null) {
                                listener.onAsteroidCrashed();
                                listener.onStop(score);
                            }
                            stop();
                        });
                    }
                }

                messages.draw(canvas, speed);
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    /**
     * Start a new game! Wheeeeeeeeee!
     *
     * @param isTutorial Should this game start with a tutorial?
     */
    public void play(boolean isTutorial) {
        isPlaying = true;
        score = 0;
        speed = 5;
        shipPositionX = 0.5f;
        shipRotation = 0;
        asteroids.setMakeAsteroids(!isTutorial);

        if (animator != null && animator.isStarted())
            animator.cancel();

        ValueAnimator animator = ValueAnimator.ofFloat(shipPositionY, 1);
        animator.setDuration(150);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(valueAnimator -> shipPositionY = (float) valueAnimator.getAnimatedValue());
        animator.start();

        setOnTouchListener(this);

        if (listener != null)
            listener.onStart(isTutorial);
    }

    /**
     * Stop the game, reset everything.
     */
    public void stop() {
        isPlaying = false;
        setOnTouchListener(null);
        asteroids.setMakeAsteroids(false);

        if (animator != null && animator.isStarted())
            animator.cancel();

        ValueAnimator animator = ValueAnimator.ofFloat(shipPositionY, -1f);
        animator.setDuration(1000);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(valueAnimator -> {
            shipPositionY = (float) valueAnimator.getAnimatedValue();
            shipRotation = 720 * valueAnimator.getAnimatedFraction();
        });
        if (tutorial > TUTORIAL_NONE) { // probably also tutorial nonsense
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    messages.clear();
                    messages.drawMessage(getContext(), R.string.msg_dont_get_hit);
                    play(true);
                }
            });
        }
        animator.start();

        ValueAnimator animator1 = ValueAnimator.ofFloat(speed, 1);
        animator1.setDuration(150);
        animator1.setInterpolator(new DecelerateInterpolator());
        animator1.addUpdateListener(valueAnimator -> speed = (float) valueAnimator.getAnimatedValue());
        animator1.start();
    }

    /**
     * Determine if a game is being played.
     *
     * @return Whether a game is being played.
     */
    public boolean isPlaying() {
        return isPlaying;
    }

    /**
     * Determine whether the tutorial is currently
     * being played.
     *
     * @return Whether the tutorial is currently being played.
     */
    public boolean isTutorial() {
        return tutorial > TUTORIAL_NONE;
    }

    public void onPause() {
        if (thread == null)
            return;

        isRunning = false;
        try {
            thread.join();
        } catch (InterruptedException ignored) {
        }
        thread = null;
    }

    public void onResume() {
        if (thread != null)
            onPause();

        isRunning = true;
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (System.currentTimeMillis() - projectileTime < 350 && (tutorial == TUTORIAL_NONE || tutorial > TUTORIAL_UPGRADE)) {
                    if (listener != null) {
                        messages.drawMessage(getContext(), R.string.msg_out_of_ammo);
                        listener.onOutOfAmmo();
                    }
                    return false;
                } else projectileTime = System.currentTimeMillis();

                if (animator != null && animator.isStarted())
                    animator.cancel();

                if (event.getX() > getWidth() / 2) {
                    if (shipPositionX < 1)
                        animator = ValueAnimator.ofFloat(shipPositionX, shipPositionX + 1);
                    else return false;
                } else if (shipPositionX > 0)
                    animator = ValueAnimator.ofFloat(shipPositionX, shipPositionX - 1);

                animator.setDuration((long) (1000 / speed));
                animator.setStartDelay(50);
                animator.setInterpolator(new AccelerateInterpolator());
                animator.addUpdateListener(valueAnimator -> {
                    float newX = (float) valueAnimator.getAnimatedValue();
                    if (newX <= 0)
                        shipPositionX = 0;
                    else if (newX >= 1)
                        shipPositionX = 1;
                    else shipPositionX = newX;
                });

                animator.start();
                shipPositionStartX = shipPositionX;
                break;
            case MotionEvent.ACTION_UP:
                if (animator != null && animator.isStarted())
                    animator.cancel();

                if (tutorial == TUTORIAL_MOVE) { // tutorial nonsense! yay!
                    if (System.currentTimeMillis() - projectileTime > 500)
                        tutorial++;
                    else {
                        messages.clear();
                        messages.drawMessage(getContext(), R.string.msg_hold_distance);
                    }
                }

                float newX = shipPositionX + ((shipPositionX - shipPositionStartX) / 1.5f);
                if (newX <= 0)
                    newX = 0;
                else if (newX >= 1)
                    newX = 1;

                animator = ValueAnimator.ofFloat(shipPositionX, newX);
                animator.setInterpolator(new DecelerateInterpolator());
                animator.setDuration((long) (500 / speed));
                animator.addUpdateListener(valueAnimator -> {
                    float newX1 = (float) valueAnimator.getAnimatedValue();
                    if (newX1 <= 0)
                        shipPositionX = 0;
                    else if (newX1 >= 1)
                        shipPositionX = 1;
                    else shipPositionX = newX1;
                });

                animator.start();
                break;
        }
        return true;
    }

    public interface GameListener {
        void onStart(boolean isTutorial);

        void onTutorialFinish();

        void onStop(int score);

        void onAsteroidPassed();

        void onAsteroidCrashed();

        void onAmmoReplenished();

        void onOutOfAmmo();

        void onAsteroidHit(int score);
    }
}
