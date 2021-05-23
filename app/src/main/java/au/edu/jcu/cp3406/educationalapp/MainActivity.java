package au.edu.jcu.cp3406.educationalapp;


import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_QUIZ = 1;

    public static final String EXTRA_CATEGORY_ID = "extraCategoryID";
    public static final String EXTRA_CATEGORY_NAME = "extraCategoryName";

    private Spinner spinnerCategory;

    // TODO: 22/05/2021 Implement high score in HighScoreActivity

    // TODO: 23/05/2021 Set Spinner to Settings instead of MainActivity.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        // High Score Text View

        // Functionality for buttons in activity_main.xml.
        Button play_button = (Button) findViewById(R.id.play_button);
        Button settings_button = (Button) findViewById(R.id.settings_button);

        spinnerCategory = findViewById(R.id.category_spinner);
        loadCategories();

        // Pressing play button will start Quiz!
        play_button.setOnClickListener(view -> startQuiz());

        // Pressing High Score button takes you to the High Score Screen.
        settings_button.setOnClickListener(view -> startSettings());

    }

    private void startQuiz() {
        Category selectedCategory = (Category) spinnerCategory.getSelectedItem();
        int categoryID = selectedCategory.getId();
        String categoryName = selectedCategory.getName();

        Intent intent = new Intent(MainActivity.this, GameActivity.class);
        intent.putExtra(EXTRA_CATEGORY_ID, categoryID);
        intent.putExtra(EXTRA_CATEGORY_NAME, categoryName);
        startActivity(intent);
    }

    private void startSettings() {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    private void loadCategories() {
        QuizDb dbHelper = QuizDb.getInstance(this);
        List<Category> categories = dbHelper.getAllCategories();
        ArrayAdapter<Category> adapterCategories = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categories);
        adapterCategories.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapterCategories);
    }

/*       settings_button.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(intent);
        });*/
}
