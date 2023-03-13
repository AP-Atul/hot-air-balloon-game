package org.ranobe.hotairballoon;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

import org.ranobe.hotairballoon.views.GameView;

public class GameLoop extends Thread {
    private final SurfaceHolder surfaceHolder;
    private final GameView gameView;
    private boolean isRunning = false;

    public GameLoop(SurfaceHolder holder, GameView view) {
        this.surfaceHolder = holder;
        this.gameView = view;
    }

    public void start(boolean run) {
        isRunning = run;
    }

    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public void run() {
        while (isRunning) {
            if (!surfaceHolder.getSurface().isValid()) return;
            Canvas canvas = surfaceHolder.lockCanvas();
            if (canvas == null) return;

            try {
                gameView.onUpdate(canvas);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }

    }
}
