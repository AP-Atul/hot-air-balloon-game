package org.ranobe.hotairballoon.utils;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

import org.ranobe.hotairballoon.views.GameView;

public class GameLoop extends Thread {
    private SurfaceHolder surfaceHolder;
    private GameView gameView;
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
        surfaceHolder = null;
        gameView = null;
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
        while (isRunning) {
            while (isPlaying) {
                if(surfaceHolder == null) return;
                if (!surfaceHolder.getSurface().isValid()) return;
                Canvas canvas = surfaceHolder.lockCanvas();
                if (canvas == null) return;

                try {
                    gameView.onUpdate(canvas);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if(surfaceHolder != null) {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }

    }
}
