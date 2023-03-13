package org.ranobe.hotairballoon;


import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import org.ranobe.hotairballoon.utils.PreferenceUtils;
import org.ranobe.hotairballoon.views.GameView;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView titleView;
    private TextView highScoreView;
    private ImageView pauseView;
    private ImageView stopView;
    private GameView gameView;

    private ValueAnimator animator;
    private String appName;

    private SharedPreferences prefs;

    private boolean isPaused;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        pauseView.setOnClickListener(view -> {
            isPaused = !isPaused;
            if (isPaused) {
                pauseView.setImageResource(R.drawable.ic_play);
                gameView.onPause();
            } else {
                pauseView.setImageResource(R.drawable.ic_pause);
                gameView.play();
            }
        });

        stopView.setOnClickListener(v -> gameView.stop());

        int highScore = prefs.getInt(PreferenceUtils.PREF_HIGH_SCORE, 0);
        if (highScore > 0)
            highScoreView.setText(String.format(getString(R.string.score_high), highScore));

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
    public void onClick(View view) {
        titleView.setVisibility(View.GONE);
        gameView.setOnClickListener(null);
        gameView.play();
    }
}
