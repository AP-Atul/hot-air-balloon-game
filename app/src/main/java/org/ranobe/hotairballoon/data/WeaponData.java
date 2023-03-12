package org.ranobe.hotairballoon.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.SoundPool;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

import org.ranobe.hotairballoon.R;
import org.ranobe.hotairballoon.utils.ImageUtils;

import java.util.List;


public class WeaponData {

    public static final WeaponData[] WEAPONS = new WeaponData[]{
            new WeaponData(R.string.weapon_pellet, R.drawable.ic_weapon_pellet, 1, 1, 15),
            new WeaponData(R.string.weapon_watermelon, R.drawable.ic_weapon_watermelon, 2, 1, 20),
            new WeaponData(R.string.weapon_grape, R.drawable.ic_weapon_grape, 1, 3, 20),
            new WeaponData(R.string.weapon_watermelons, R.drawable.ic_weapon_watermelon_sprayer, 2, 3, 25),
            new WeaponData(R.string.weapon_pellets, R.drawable.ic_weapon_pellet_sprayer, 1, 5, 25),
            new WeaponData(R.string.weapon_laser, R.drawable.ic_weapon_laser, 3, 5, 20),
            new WeaponData(R.string.weapon_seed, R.drawable.ic_weapon_grape_sprayer, 1, 10, 15),
            new WeaponData(R.string.weapon_brick, R.drawable.ic_weapon_brick, 5, 3, 25),
            new WeaponData(R.string.weapon_lasers, R.drawable.ic_weapon_laser_sprayer, 3, 8, 20),
            new WeaponData(R.string.weapon_particle, R.drawable.ic_weapon_particle, 10, 1, 40),
            new WeaponData(R.string.weapon_particles, R.drawable.ic_weapon_particles, 10, 3, 45),
            new WeaponData(R.string.weapon_particles2, R.drawable.ic_weapon_particless, 10, 5, 50)
    };
    public int capacity;
    public int soundId;
    private int nameRes;
    private int drawableRes;
    private int strength;
    private int spray;
    private Bitmap bitmap;

    public WeaponData(@StringRes int nameRes, @DrawableRes int drawableRes, int strength, int spray, int capacity) {
        this.nameRes = nameRes;
        this.drawableRes = drawableRes;
        this.strength = strength;
        this.spray = spray;
        this.capacity = capacity;
    }

    /**
     * Load the weapon sounds to be played when they are fired.
     *
     * @param context   An active context instance.
     * @param soundPool The SoundPool to load the sounds into.
     */
    public static void loadSounds(Context context, SoundPool soundPool) {
        for (WeaponData weapon : WEAPONS) {
            weapon.loadSoundRes(context, soundPool);
        }
    }

    /**
     * Gets the user-facing name of the weapon.
     *
     * @param context An active context instance.
     * @return The name of the weapon; a String.
     */
    public String getName(Context context) {
        return context.getString(nameRes);
    }

    /**
     * Get the Bitmap image of the weapon.
     *
     * @param context An active context instance.
     * @return The Bitmap image of the weapon.
     */
    public Bitmap getBitmap(Context context) {
        if (bitmap == null)
            bitmap = ImageUtils.gradientBitmap(ImageUtils.getVectorBitmap(context, drawableRes), ContextCompat.getColor(context, R.color.colorAccent), ContextCompat.getColor(context, R.color.colorPrimary));

        return bitmap;
    }

    /**
     * Fire the weapon; generate an amount of ProjectileDatas at the given
     * x/y coordinates and add them to the passed List instance.
     *
     * @param projectiles The current list of projectiles being drawn.
     * @param x           The current x coordinate of the player.
     * @param y           The current y coordinate of the player.
     */
    public void fire(List<ProjectileData> projectiles, float x, float y) {
        for (int i = 0; i < spray; i++) {
            float xDiff = (((float) (i + 1) / (spray + 1)) - 0.5f) / 2;
            for (int i2 = 0; i2 < strength; i2++) {
                projectiles.add(new ProjectileData(x, y + (3 * i2), xDiff * 0.008f, 4));
            }
        }
    }

    private void loadSoundRes(Context context, SoundPool soundPool) {
//        soundId = soundPool.load(context, soundRes, 1);
    }

}
