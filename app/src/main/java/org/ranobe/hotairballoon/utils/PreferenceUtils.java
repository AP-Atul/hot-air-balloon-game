package org.ranobe.hotairballoon.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceUtils {
    public static final String PREF_HIGH_SCORE = "highScore";
    public static final String PACKAGE_NAME = "org.ranobe.hotairballoon";

    private static SharedPreferences.Editor getEditor(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                PACKAGE_NAME, Context.MODE_PRIVATE
        );
        return sharedPreferences.edit();
    }

    private static SharedPreferences getSharedPref(Context context) {
        return context.getSharedPreferences(
                PACKAGE_NAME, Context.MODE_PRIVATE
        );
    }

    public static void storeScore(Context context, int score) {
        getEditor(context).putInt(PREF_HIGH_SCORE, score).apply();
    }

    public static int getScore(Context context) {
        return getSharedPref(context).getInt(PREF_HIGH_SCORE, -1);
    }
}
