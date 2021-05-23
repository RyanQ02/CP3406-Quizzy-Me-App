package au.edu.jcu.cp3406.educationalapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_QUIZ = 1;
    // TODO: 22/05/2021 Implement high score in HighScoreActivity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        // High Score Text View

        // Functionality for buttons in activity_main.xml.
        Button play_button = (Button) findViewById(R.id.play_button);
        Button about_button = (Button) findViewById(R.id.about_button);
        Button high_score_button = (Button) findViewById(R.id.high_score_button);
        Button settings_button = (Button) findViewById(R.id.settings_button);

        // Pressing play button will start Quiz!
        play_button.setOnClickListener(view -> startQuiz());

        // Pressing High Score button takes you to the High Score Screen.
        high_score_button.setOnClickListener(view -> startHighScore());

    }

    private void startQuiz() {
        Intent intent = new Intent(MainActivity.this, GameActivity.class);
        startActivity(intent);
    }

/*        about_button.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
        });*/

    private void startHighScore() {
        Intent intent = new Intent(MainActivity.this, HighScoreActivity.class);
        startActivity(intent);
    }

/*       settings_button.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(intent);
        });*/
}
