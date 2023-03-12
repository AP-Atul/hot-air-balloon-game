package org.ranobe.hotairballoon;


import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import org.ranobe.hotairballoon.utils.ImageUtils;
import org.ranobe.hotairballoon.utils.PreferenceUtils;
import org.ranobe.hotairballoon.views.GameView;


public class MainActivity extends AppCompatActivity implements GameView.GameListener, View.OnClickListener {

    private TextView titleView;
    private TextView highScoreView;
    private ImageView pauseView;
    private ImageView stopView;
    private GameView gameView;

    private ValueAnimator animator;
    private String appName;

    private SharedPreferences prefs;

    private boolean isPaused;
    private Bitmap play;
    private Bitmap pause;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        titleView = findViewById(R.id.title);
        highScoreView = findViewById(R.id.highScore);
        pauseView = findViewById(R.id.pause);
        stopView = findViewById(R.id.stop);
        gameView = findViewById(R.id.game);

        int colorPrimary = ContextCompat.getColor(this, R.color.colorPrimary);
        int colorAccent = ContextCompat.getColor(this, R.color.colorAccent);

        titleView.setPaintFlags(titleView.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);
        titleView.getPaint().setShader(new LinearGradient(
                0, 0, 0,
                titleView.getLineHeight(),
                colorAccent,
                colorPrimary,
                Shader.TileMode.REPEAT
        ));

        highScoreView.getPaint().setShader(new LinearGradient(
                0, 0, 0,
                highScoreView.getLineHeight(),
                colorAccent,
                colorPrimary,
                Shader.TileMode.REPEAT
        ));

        appName = getString(R.string.app_name);

        play = ImageUtils.gradientBitmap(ImageUtils.getVectorBitmap(this, R.drawable.ic_play), colorAccent, colorPrimary);
        pause = ImageUtils.gradientBitmap(ImageUtils.getVectorBitmap(this, R.drawable.ic_pause), colorAccent, colorPrimary);
        Bitmap stop = ImageUtils.gradientBitmap(ImageUtils.getVectorBitmap(this, R.drawable.ic_stop), colorAccent, colorPrimary);

        pauseView.setImageBitmap(pause);
        pauseView.setOnClickListener(view -> {
            isPaused = !isPaused;
            if (isPaused) {
                pauseView.setImageBitmap(play);
                if (!gameView.isTutorial())
                    stopView.setVisibility(View.VISIBLE);
                gameView.onPause();
            } else {
                pauseView.setImageBitmap(pause);
                pauseView.setAlpha(1f);
                stopView.setVisibility(View.GONE);
                gameView.onResume();
            }
        });

        stopView.setImageBitmap(stop);
        stopView.setOnClickListener(view -> {
            if (isPaused) {
                pauseView.setImageBitmap(pause);
                pauseView.setAlpha(1f);
                gameView.onResume();
                isPaused = false;
            }

            onStop(gameView.score);
            gameView.stop();
        });

        int highScore = prefs.getInt(PreferenceUtils.PREF_HIGH_SCORE, 0);
        if (highScore > 0)
            highScoreView.setText(String.format(getString(R.string.score_high), highScore));


        gameView.setListener(this);
        gameView.setOnClickListener(this);
        animateTitle(true);
    }

    private void animateTitle(final boolean isVisible) {
        highScoreView.setVisibility(View.GONE);

        animator = ValueAnimator.ofFloat(isVisible ? 0 : 1, isVisible ? 1 : 0);
        animator.setDuration(750);
        animator.setStartDelay(500);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.addUpdateListener(valueAnimator -> {
            titleView.setText(appName.substring(0, (int) ((float) valueAnimator.getAnimatedValue() * appName.length())));
        });
        animator.start();

        if (isVisible) {
            pauseView.setVisibility(View.GONE);
            stopView.setVisibility(View.GONE);
        } else {
            pauseView.setVisibility(View.VISIBLE);
            stopView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPause() {
        if (gameView != null && !isPaused)
            gameView.onPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (gameView != null && !isPaused)
            gameView.onResume();

        if (Settings.Global.getFloat(getContentResolver(), Settings.Global.ANIMATOR_DURATION_SCALE, 1) != 1) {
            try {
                ValueAnimator.class.getMethod("setDurationScale", float.class).invoke(null, 1f);
            } catch (Throwable t) {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.title_animation_speed)
                        .setMessage(R.string.desc_animation_speed)
                        .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
                            try {
                                startActivity(new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS));
                            } catch (Exception ignored) {
                            }
                            dialogInterface.dismiss();
                        })
                        .setNegativeButton(android.R.string.cancel, (dialogInterface, i) ->
                                dialogInterface.dismiss())
                        .create()
                        .show();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onStart(boolean isTutorial) {
    }

    @Override
    public void onTutorialFinish() {
        prefs.edit().putBoolean(PreferenceUtils.PREF_TUTORIAL, false).apply();
    }

    @Override
    public void onStop(int score) {
        animateTitle(true);
        gameView.setOnClickListener(this);

        int highScore = prefs.getInt(PreferenceUtils.PREF_HIGH_SCORE, 0);
        if (score > highScore) {
            //TODO: awesome high score animation or something
            highScore = score;
            prefs.edit().putInt(PreferenceUtils.PREF_HIGH_SCORE, score).apply();

        }

        highScoreView.setText(String.format(getString(R.string.score_high), highScore));

    }

    @Override
    public void onAsteroidPassed() {
    }

    @Override
    public void onAsteroidCrashed() {
    }

    @Override
    public void onAmmoReplenished() {
    }

    @Override
    public void onOutOfAmmo() {
    }

    @Override
    public void onAsteroidHit(int score) {
        titleView.setText(String.valueOf(score));
    }

    @Override
    public void onClick(View view) {
        if (!gameView.isPlaying() && (animator == null || !animator.isStarted())) {
            gameView.setOnClickListener(null);

            gameView.play(prefs.getBoolean(PreferenceUtils.PREF_TUTORIAL, true));

            animateTitle(false);
        }
    }

}
