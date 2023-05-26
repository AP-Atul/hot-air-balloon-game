package org.ranobe.hotairballoon.generator;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import org.ranobe.hotairballoon.C;
import org.ranobe.hotairballoon.R;
import org.ranobe.hotairballoon.data.DrawerData;
import org.ranobe.hotairballoon.entity.Coin;
import org.ranobe.hotairballoon.entity.Wall;
import org.ranobe.hotairballoon.utils.ImageUtils;
import org.ranobe.hotairballoon.utils.MathUtils;

import java.util.ArrayList;
import java.util.List;

public class WallsGenerator extends DrawerData {
    private final List<Wall> walls;
    private final List<Coin> coins;
    private final List<Bitmap> wallBitmaps;
    private final Paint debugPaint;
    private final Bitmap coinBitmap;

    private long generationTime;
    private float generationLength;
    private final int totalCoins = 10;

    private boolean shouldMakeWalls;

    public WallsGenerator(Context context, Paint blackPaint, Paint debugPaint) {
        super(blackPaint);
        this.debugPaint = debugPaint;
        walls = new ArrayList<>();
        coins = new ArrayList<>();
        wallBitmaps = new ArrayList<>();

        List<Integer> bitmaps = new ArrayList<Integer>() {{
            add(R.drawable.small_wall);
            add(R.drawable.big_wall);
        }};
        for(int resource: bitmaps) {
            wallBitmaps.add(ImageUtils.getVectorBitmap(context, resource));
        }

        coinBitmap = ImageUtils.getVectorBitmap(context, R.drawable.coin);
    }

    public Bitmap getRandomBitmap() {
        int random = (int) MathUtils.getRandom(0, 2);
        return wallBitmaps.get(random);
    }

    public void setMakeWalls(boolean shouldMakeAsteroids) {
        this.shouldMakeWalls = shouldMakeAsteroids;
        generationLength = 5000;
        if (shouldMakeAsteroids)
            walls.clear();
    }

    public void makeNew(int width) {
        generationTime = System.currentTimeMillis();
        walls.add(new Wall(getRandomBitmap(), width));
    }

    public void makeNewCoin(int width) {
        if (coins.size() >= totalCoins) return;
        coins.add(new Coin(coinBitmap, width));
    }

    public Wall wallCollisionDetection(Rect position) {
        for (Wall wall : walls) {
            if (wall.position != null && position.intersect(wall.position))
                return wall;
        }
        return null;
    }

    public int coinCollisionDetection(Rect balloonPosition) {
        int count = 0;
        for(Coin coin: new ArrayList<>(coins)) {
            if (coin.position != null && balloonPosition.intersect(coin.position)) {
                count += 1;
                coins.remove(coin);
            }
        }
        return count;
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
                if (C.DEBUG_LINE) canvas.drawRect(position, debugPaint);
                canvas.drawBitmap(wall.wallBitmap, position.left, position.top, paint(0));
            }
            if (wall.y > canvas.getHeight()) {
                isPassed = true;
                destroy(wall);
            }
        }

        for(Coin coin: new ArrayList<>(coins)) {
            Rect position = coin.next(speed, canvas);
            if (position != null) {
                if (C.DEBUG_LINE) canvas.drawRect(position, debugPaint);
                coin.draw(canvas);
            }
            if (coin.y > canvas.getHeight()) {
                coins.remove(coin);
            }
        }

        float time = (System.currentTimeMillis() - generationTime) * speed;
        if (shouldMakeWalls && time > generationLength) {
            makeNew(canvas.getWidth());
        }

        if (MathUtils.getXChance(0.02F)) {
            makeNewCoin(canvas.getWidth());
        }

        return isPassed;
    }
}
