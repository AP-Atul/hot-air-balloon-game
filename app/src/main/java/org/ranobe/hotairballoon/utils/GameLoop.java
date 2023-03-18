package org.ranobe.hotairballoon.utils;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

import org.ranobe.hotairballoon.views.GameView;

public class GameLoop extends Thread {
    private final SurfaceHolder surfaceHolder;
    private final GameView gameView;
    private boolean isRunning = false;
    private boolean isPlaying = false;

    public GameLoop(SurfaceHolder holder, GameView view) {
        this.surfaceHolder = holder;
        this.gameView = view;
    }

    public void start(boolean run) {
        if (!isRunning) {
            this.start();
        }
        isRunning = run;
        isPlaying = true;
    }

    public void end() {
        isPlaying = false;
        isRunning = false;
    }

    public void pause() {
        isPlaying = false;
    }

    public void unpause() {
        isPlaying = true;
    }

    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public void run() {
        long previousTime = System.currentTimeMillis();
        long fps = 60;
        while (isRunning) {

            long currentTimeMillis = System.currentTimeMillis();
            long elapsedTimeMs = currentTimeMillis - previousTime;
            long sleepTimeMs = (long) (1000f/ fps - elapsedTimeMs);
            Canvas canvas = null;

            while (isPlaying) {
                try {
                if (!surfaceHolder.getSurface().isValid()) return;
                canvas = surfaceHolder.lockCanvas();
                if (canvas == null) {
                    Thread.sleep(1);
                    continue;
                } else if (sleepTimeMs > 0) {
                    Thread.sleep(sleepTimeMs);
                }

                gameView.onUpdate(canvas);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    previousTime = System.currentTimeMillis();
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }

    }
}
