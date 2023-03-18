package org.ranobe.hotairballoon.generator;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import org.ranobe.hotairballoon.R;
import org.ranobe.hotairballoon.data.DrawerData;
import org.ranobe.hotairballoon.entity.Wall;
import org.ranobe.hotairballoon.utils.ImageUtils;

import java.util.ArrayList;
import java.util.List;

public class WallsGenerator extends DrawerData {
    private final List<Wall> walls;
    private final Bitmap wallBitmap;
    private final Paint debugPaint;

    private long generationTime;
    private float generationLength;

    private boolean shouldMakeWalls;

    public WallsGenerator(Context context, Paint blackPaint, Paint debugPaint) {
        super(blackPaint);
        this.debugPaint = debugPaint;
        walls = new ArrayList<>();
        wallBitmap = ImageUtils.getVectorBitmap(context, R.drawable.ic_wall);
    }

    public void setMakeWalls(boolean shouldMakeAsteroids) {
        this.shouldMakeWalls = shouldMakeAsteroids;
        generationLength = 5000;
        if (shouldMakeAsteroids)
            walls.clear();
    }

    public void makeNew(int width) {
        generationTime = System.currentTimeMillis();
        walls.add(new Wall(wallBitmap, width));
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

        for (Wall wall : new ArrayList<>(walls)) {
            Rect position = wall.next(speed, canvas);
            if (position != null) {
                canvas.drawRect(position, debugPaint);
                canvas.drawBitmap(wall.wallBitmap, position.left, position.top, paint(0));
            } else {
                isPassed = true;
                destroy(wall);
            }
        }

        float time = (System.currentTimeMillis() - generationTime) * speed;
        if (shouldMakeWalls && time > generationLength) {
            makeNew(canvas.getWidth());
        }

        return isPassed;
    }
}
