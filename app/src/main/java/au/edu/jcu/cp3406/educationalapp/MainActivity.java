package au.edu.jcu.cp3406.educationalapp;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1;

    public static final String EXTRA_CATEGORY_ID = "extraCategoryID";
    public static final String EXTRA_CATEGORY_NAME = "extraCategoryName";

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String KEY_HIGHSCORE = "keyHighscore";

    private Spinner spinnerCategory;
    private TextView textViewHighScore;

    private int highScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Functionality for buttons in activity_main.xml.
        Button play_button = (Button) findViewById(R.id.play_button);
        Button settings_button = (Button) findViewById(R.id.settings_button);

        // Spinner for Categories IT, Science and Japanese.
        spinnerCategory = findViewById(R.id.category_spinner);
        loadCategories();

        // High Score Text view with stored High Score.
        textViewHighScore = findViewById(R.id.text_view_high_score);
        loadHighscore();

        // Pressing play button will start Quiz!
        play_button.setOnClickListener(view -> startQuiz());

        // Pressing High Score button takes you to the High Score Screen.
        settings_button.setOnClickListener(view -> startSettings());

    }

    // Starts Quiz GameActivity.
    private void startQuiz() {
        Category selectedCategory = (Category) spinnerCategory.getSelectedItem();
        int categoryID = selectedCategory.getId();
        String categoryName = selectedCategory.getName();

        Intent intent = new Intent(MainActivity.this, GameActivity.class);
        intent.putExtra(EXTRA_CATEGORY_ID, categoryID);
        intent.putExtra(EXTRA_CATEGORY_NAME, categoryName);
        startActivityForResult(intent, REQUEST_CODE);
    }

    // Starts Settings Activity
    private void startSettings() {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    // Loads Categories from Database to Spinner.
    private void loadCategories() {
        QuizDb dbHelper = QuizDb.getInstance(this);
        List<Category> categories = dbHelper.getAllCategories();
        ArrayAdapter<Category> adapterCategories = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categories);
        adapterCategories.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapterCategories);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                int score = data.getIntExtra(GameActivity.EXTRA_SCORE, 0);
                if (score > highScore) {
                    updateHighscore(score);
                }
            }
        }
    }

    // Loads HighScore from Shared Preferences.
    @SuppressLint("SetTextI18n")
    private void loadHighscore() {
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        highScore = prefs.getInt(KEY_HIGHSCORE, 0);
        textViewHighScore.setText("Highscore: " + highScore);
    }

    // Updates score with High Score from Shared Preferences.
    @SuppressLint("SetTextI18n")
    private void updateHighscore(int highscoreNew) {
        highScore = highscoreNew;
        textViewHighScore.setText("Highscore: " + highScore);
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_HIGHSCORE, highScore);
        editor.apply();
    }
}
