package org.ranobe.hotairballoon.generator;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import org.ranobe.hotairballoon.C;
import org.ranobe.hotairballoon.R;
import org.ranobe.hotairballoon.data.DrawerData;
import org.ranobe.hotairballoon.entity.Wall;
import org.ranobe.hotairballoon.utils.ImageUtils;

import java.util.ArrayList;
import java.util.List;

public class WallsGenerator extends DrawerData {
    private final List<Wall> walls;
    private final Bitmap wallBitmap;

    private long generationTime;
    private float generationLength;

    private boolean shouldMakeWalls;

    public WallsGenerator(Context context, Paint blackPaint) {
        super(blackPaint);
        walls = new ArrayList<>();
        wallBitmap = ImageUtils.getVectorBitmap(context, R.drawable.ic_wall);
    }

    public void setMakeWalls(boolean shouldMakeAsteroids) {
        this.shouldMakeWalls = shouldMakeAsteroids;
        generationLength = 6000;
        if (shouldMakeAsteroids)
            walls.clear();
    }

    public void makeNew() {
        generationTime = System.currentTimeMillis();
        walls.add(new Wall(wallBitmap));
    }

    public Wall wallCollisionDetection(Rect position) {
        for (Wall wall : walls) {
            if (wall.position != null && position.intersect(wall.position))
                return wall;
        }
        return null;
    }

    public void destroy(Wall asteroid) {
        walls.remove(asteroid);
    }

    @Override
    public boolean draw(Canvas canvas, float speed) {
        boolean isPassed = false;

        for (Wall asteroid : new ArrayList<>(walls)) {
            Matrix matrix = asteroid.next(speed, canvas.getWidth(), canvas.getHeight());
            if (matrix != null) {
                canvas.drawBitmap(asteroid.wallBitmap, matrix, paint(0));
            } else {
                isPassed = true;
                destroy(asteroid);
            }
        }

        float time = (System.currentTimeMillis() - generationTime) * speed;
        if (shouldMakeWalls && time > generationLength) {
            makeNew();
        }

        return isPassed;
    }
}
