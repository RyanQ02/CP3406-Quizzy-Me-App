package au.edu.jcu.cp3406.educationalapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        preferences = getSharedPreferences("config", MODE_PRIVATE);

        // Uses AppCompatDelegate so Night mode is used Globally in the app.

        AppCompatDelegate.setDefaultNightMode(preferences.getInt("Default Night Mode",
                AppCompatDelegate.MODE_NIGHT_UNSPECIFIED));

        Button setDarkLightMode = findViewById(R.id.set_dark_light_mode);

        setDarkLightMode.setOnClickListener(v -> {
            if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
        });
    }
    // Destroys Night mode method.
    @Override
    protected void onDestroy() {
        preferences.edit().putInt("Default Night Mode", AppCompatDelegate.getDefaultNightMode()).apply();
        super.onDestroy();
    }
}
