package org.ranobe.hotairballoon;


import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.ranobe.hotairballoon.utils.PreferenceUtils;
import org.ranobe.hotairballoon.views.GameView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GameView.GameListener {

    private TextView highScore;
    private GameView gameView;
    private boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        gameView = findViewById(R.id.game);
        gameView.setListener(this);

        highScore = findViewById(R.id.high_score);

        findViewById(R.id.play_pause).setOnClickListener(this);
        findViewById(R.id.quit).setOnClickListener(this);

        setScore(PreferenceUtils.getScore(this));
    }

    private void setScore(int score) {
        if (score < 1) return;
        highScore.setText(String.format(Locale.getDefault(), "HI-SCORE\n%d", score));
        highScore.setVisibility(View.VISIBLE);
        highScore.setTextColor(getColor(R.color.gray_text));
    }

    @Override
    public void onPause() {
        if (gameView != null && isPlaying)
            gameView.onPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (gameView != null && isPlaying)
            gameView.onResume();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.play_pause) {
            findViewById(R.id.main_controls).setVisibility(View.GONE);
            gameView.setVisibility(View.VISIBLE);
            gameView.play();
            isPlaying = true;
        } else if (id == R.id.quit) {
            finishAffinity();
        }
    }

    @Override
    public void died(int score) {
        findViewById(R.id.title).setVisibility(View.VISIBLE);
        PreferenceUtils.storeScore(this, score);
        setScore(score);
        Toast.makeText(getApplicationContext(), "Game Over", Toast.LENGTH_SHORT).show();
        isPlaying = false;
        gameView.stop();
        gameView.setVisibility(View.INVISIBLE);
        findViewById(R.id.main_controls).setVisibility(View.VISIBLE);
    }
}
