package org.ranobe.hotairballoon.data.drawer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;

import org.ranobe.hotairballoon.R;
import org.ranobe.hotairballoon.data.Wall;
import org.ranobe.hotairballoon.data.DrawerData;
import org.ranobe.hotairballoon.data.ParticleData;
import org.ranobe.hotairballoon.utils.ImageUtils;

import java.util.ArrayList;
import java.util.List;

public class AsteroidDrawer extends DrawerData {

    List<ParticleData> particles;
    private final List<Wall> walls;
    private final Bitmap wallBitmap;

    private long asteroidTime;
    private float asteroidLength;

    private boolean shouldMakeAsteroids;

    public AsteroidDrawer(Context context, int colorAccent, int colorPrimary, Paint asteroidPaint, Paint particlePaint) {
        super(asteroidPaint, particlePaint);
        walls = new ArrayList<>();
        particles = new ArrayList<>();
        wallBitmap = ImageUtils.gradientBitmap(ImageUtils.getVectorBitmap(context, R.drawable.ic_wall), colorAccent, colorPrimary);
    }

    /**
     * Set whether the drawer should generate its own asteroids at
     * a set interval.
     *
     * @param shouldMakeAsteroids Whether the drawer should generate
     *                            its own asteroids.
     */
    public void setMakeAsteroids(boolean shouldMakeAsteroids) {
        this.shouldMakeAsteroids = shouldMakeAsteroids;
        asteroidLength = 3000;
        if (shouldMakeAsteroids)
            walls.clear();
    }

    /**
     * Make a new asteroid. Like magic.
     */
    public void makeNew() {
        asteroidTime = System.currentTimeMillis();
        walls.add(new Wall(wallBitmap));
    }

    /**
     * @return The amount of asteroids currently visible on the screen.
     */
    public int size() {
        return walls.size();
    }

    /**
     * Determine if there is an asteroid intersecting the given position
     * on the canvas; if so, return it.
     *
     * @param position The position Rect to check if an asteroid intersects.
     * @return The Wall if it intersects the given position;
     * null if there is nothing there.
     */
    public Wall asteroidAt(Rect position) {
        for (Wall asteroid : walls) {
            if (asteroid.position != null && position.intersect(asteroid.position))
                return asteroid;
        }

        return null;
    }

    /**
     * Destroy a given asteroid; generate an explosion of particles in its place.
     *
     * @param asteroid The asteroid to obliterate. Kaboom! Kablowie! Kapow!
     *                 Badabadoosh!
     */
    public void destroy(Wall asteroid) {
        walls.remove(asteroid);

        for (int i = 0; i < 50; i++) {
            particles.add(new ParticleData(paint(1), asteroid.x, asteroid.y));
        }
    }

    @Override
    public boolean draw(Canvas canvas, float speed) {
        boolean isPassed = false;

        for (Wall asteroid : new ArrayList<>(walls)) {
            Matrix matrix = asteroid.next(speed, canvas.getWidth(), canvas.getHeight());
            if (matrix != null) {
                canvas.drawBitmap(asteroid.asteroidBitmap, matrix, paint(0));
            } else {
                isPassed = true;
                walls.remove(asteroid);
                if (asteroidLength > 750)
                    asteroidLength -= (asteroidLength * 0.1);
            }
        }

        for (ParticleData particle : new ArrayList<>(particles)) {
            if (!particle.draw(canvas, 1))
                particles.remove(particle);
        }

        if (shouldMakeAsteroids && System.currentTimeMillis() - asteroidTime > asteroidLength)
            makeNew();

        return isPassed;
    }
}
