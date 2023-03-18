package org.ranobe.hotairballoon;


import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.ranobe.hotairballoon.views.GameView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GameView.GameListener {

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

        findViewById(R.id.play_pause).setOnClickListener(this);
        findViewById(R.id.quit).setOnClickListener(this);
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
    public void died() {
        Toast.makeText(getApplicationContext(), "Game Over", Toast.LENGTH_LONG).show();
        isPlaying = false;
        gameView.stop();
        gameView.setVisibility(View.INVISIBLE);
        findViewById(R.id.main_controls).setVisibility(View.VISIBLE);
    }
}
