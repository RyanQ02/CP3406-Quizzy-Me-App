package au.edu.jcu.cp3406.educationalapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class GameActivity extends AppCompatActivity{

    public static final String EXTRA_SCORE = "extraScore";

    private static final long COUNTDOWN_IN_MILLISECONDS = 20000;
    private final int countDownInterval = 1000;

    private static final String KEY_SCORE = "keyScore";
    private static final String KEY_QUESTION_COUNT = "keyQuestionCount";
    private static final String KEY_MILLIS_LEFT = "keyMillisLeft";
    private static final String KEY_ANSWERED = "keyAnswered";
    private static final String KEY_QUESTION_LIST = "keyQuestionList";

    private TextView textViewQuestion;
    private TextView textViewScore;
    private TextView textViewQuestionCount;
    private TextView textViewCountDown;
    private RadioGroup rbGroup;
    private RadioButton rb1;
    private RadioButton rb2;
    private RadioButton rb3;
    private Button buttonConfirmNext;
    private Button twitterShareButton;
    private Button backButton;

    private ColorStateList textColorDefaultRb; // Text colour for radio button.

    private ColorStateList textColorDefaultCd;

    private CountDownTimer countDownTimer;
    private long timeLeftInMilliseconds;

    private ArrayList<Question> questionList;
    private int questionCounter;
    private int questionCountTotal;
    private Question currentQuestion;

    private int score;
    private boolean ifAnswered; // Automatically select answer if time has run out or show next question if answered.

    private long backPressedTime;

    private SensorManager sensorManager;
    private float accel;
    private float accelCurrent;
    private float accelLast;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        textViewQuestion = findViewById(R.id.text_view_question);
        textViewScore = findViewById(R.id.text_view_score);
        textViewQuestionCount = findViewById(R.id.text_view_question_count);
        TextView textViewCategory = findViewById(R.id.text_view_category);
        textViewCountDown = findViewById(R.id.text_view_countdown);

        rbGroup = findViewById(R.id.radio_group);
        rb1 = findViewById(R.id.radio_button1);
        rb2 = findViewById(R.id.radio_button2);
        rb3 = findViewById(R.id.radio_button3);

        buttonConfirmNext = findViewById(R.id.button_confirm_next);
        textColorDefaultRb = rb1.getTextColors();
        textColorDefaultCd = textViewCountDown.getTextColors();

        sensorManager = (SensorManager)this.getSystemService(SENSOR_SERVICE);
        Objects.requireNonNull(sensorManager).registerListener(sensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        accel = 10f;
        accelCurrent = SensorManager.GRAVITY_EARTH;
        accelLast = SensorManager.GRAVITY_EARTH;

        // Functionality to share score to Twitter.
        // This will open up Twitter either in the device's web browser or Twitter app
        // if installed.

        twitterShareButton = findViewById(R.id.twitter_share_button);
        twitterShareButton.setVisibility(View.GONE);
        twitterShareButton.setOnClickListener(v -> shareOnTwitter());

        // Functionality to go back to the main menu after quiz is completed.
        // Button is hidden until quiz completion.
        // Uses finish() instead of intent.
        backButton = findViewById(R.id.back_button);
        backButton.setVisibility(View.GONE);
        backButton.setOnClickListener(view -> finishGame());


        Intent intent = getIntent();
        int categoryID = intent.getIntExtra(MainActivity.EXTRA_CATEGORY_ID, 0);
        String categoryName = intent.getStringExtra(MainActivity.EXTRA_CATEGORY_NAME);
        textViewCategory.setText("Category: " + categoryName);
        if (savedInstanceState == null) {

            QuizDb dbHelper = QuizDb.getInstance(this);
            questionList = dbHelper.getQuestions(categoryID);
            questionCountTotal = questionList.size();
            Collections.shuffle(questionList);
            showNextQuestion();

        } else {
            questionList = savedInstanceState.getParcelableArrayList(KEY_QUESTION_LIST);
            questionCountTotal = questionList.size();
            questionCounter = savedInstanceState.getInt(KEY_QUESTION_COUNT);
            currentQuestion = questionList.get(questionCounter - 1);
            score = savedInstanceState.getInt(KEY_SCORE);
            timeLeftInMilliseconds = savedInstanceState.getLong(KEY_MILLIS_LEFT);
            ifAnswered = savedInstanceState.getBoolean(KEY_ANSWERED);
            if (!ifAnswered) {
                startCountDown();
            } else {
                updateCountDownText();
                showSolution();
            }
        }

        // When confirm button is pressed, option buttons are checked to see if users has selected a answer.

        buttonConfirmNext.setOnClickListener(v -> {
            if (!ifAnswered) {
                if (rb1.isChecked() || rb2.isChecked() || rb3.isChecked()) {
                    checkAnswer();
                } else {
                    Toast.makeText(GameActivity.this, "Please select an answer", Toast.LENGTH_SHORT).show();
                }
            } else {
                showNextQuestion();
            }
        });
        // Toast to notify user of Shake gesture feature to quickly head back to the main menu.
        Toast.makeText(getApplicationContext(), "Tip: Shake device for easy access back to the main menu", Toast.LENGTH_LONG).show();
    }

    @SuppressLint("SetTextI18n")
    private void showNextQuestion() {
        rb1.setTextColor(textColorDefaultRb);
        rb2.setTextColor(textColorDefaultRb);
        rb3.setTextColor(textColorDefaultRb);
        rbGroup.clearCheck();

        // If confirm button is pressed, locks answer from one of the selected options.

        if (questionCounter < questionCountTotal) {
            currentQuestion = questionList.get(questionCounter);

            textViewQuestion.setText(currentQuestion.getQuestion());
            rb1.setText(currentQuestion.getOption1());
            rb2.setText(currentQuestion.getOption2());
            rb3.setText(currentQuestion.getOption3());

            questionCounter++;
            textViewQuestionCount.setText("Question: " + questionCounter + "/" + questionCountTotal);
            ifAnswered = false;
            buttonConfirmNext.setText("Confirm");

            timeLeftInMilliseconds = COUNTDOWN_IN_MILLISECONDS;
            startCountDown();
        } else {

            onQuizFinished();
        }
    }

    // Countdown Timer functionality.
    private void startCountDown() {
        countDownTimer = new CountDownTimer(timeLeftInMilliseconds, countDownInterval) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMilliseconds = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                timeLeftInMilliseconds = 0;
                updateCountDownText();
                checkAnswer();
            }
        }.start();
    }

    // Countdown timer minutes and seconds logic is then formatted to 00:00.
    private void updateCountDownText() {
        int minutes = (int) (timeLeftInMilliseconds / 1000) / 60;
        int seconds = (int) (timeLeftInMilliseconds / 1000) % 60;
        String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        textViewCountDown.setText(timeFormatted);
        if (timeLeftInMilliseconds < 5000) {
            textViewCountDown.setTextColor(Color.RED);
        } else {
            textViewCountDown.setTextColor(textColorDefaultCd);
        }
    }

    // if answer_number is == to currentQuestion, add score + 1

    @SuppressLint("SetTextI18n")
    private void checkAnswer() {
        ifAnswered = true;

        // Stops and Cancels countdown timer when answered is true.
        countDownTimer.cancel();

        RadioButton rbSelected = findViewById(rbGroup.getCheckedRadioButtonId());
        int answerNumber = rbGroup.indexOfChild(rbSelected) + 1;
        if (answerNumber == currentQuestion.getAnswer_number()) {
            score++;
            textViewScore.setText("Score: " + score);
        }
        showSolution();
    }

    // Shows solutions after boolean ifAnswered = true.

    @SuppressLint("SetTextI18n")
    private void showSolution() {
        rb1.setTextColor(Color.RED);
        rb2.setTextColor(Color.RED);
        rb3.setTextColor(Color.RED);
        switch (currentQuestion.getAnswer_number()) {
            case 1:
                rb1.setTextColor(Color.GREEN);
                textViewQuestion.setText("Answer 1 is correct");
                break;
            case 2:
                rb2.setTextColor(Color.GREEN);
                textViewQuestion.setText("Answer 2 is correct");
                break;
            case 3:
                rb3.setTextColor(Color.GREEN);
                textViewQuestion.setText("Answer 3 is correct");
                break;
        }
        if (questionCounter < questionCountTotal) {
            buttonConfirmNext.setText("Next");
        } else {
            buttonConfirmNext.setText("Finish");
        }
    }

    // Shake gesture only used in GameActivity. If user shakes device, the sensor will detect that
    // and send user back to Main Activity with a Toast to verify.
    private final SensorEventListener sensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            accelLast = accelCurrent;
            accelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
            float delta = accelCurrent - accelLast;
            accel = accel * 0.9f + delta;
            if (accel > 24) {

                // Finish activity and if score is high score, high score will be set in MainActivity.

                finishGame();
                Toast.makeText(getApplicationContext(), "Heading back to Main Menu.", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    @Override
    protected void onResume() {
        sensorManager.registerListener(sensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        super.onResume();
    }

    // Unregisters sensor when paused.
    // Sensors Android Guidelines state
    // To always unregister sensors as best practice to avoid using substantial amount of power.
    @Override
    protected void onPause() {
        sensorManager.unregisterListener(sensorListener);
        super.onPause();
    }



    // Save score so that it is checked with high score in MainActivity.
    private void onQuizFinished() {
        Toast.makeText(this, "Shake the Device or Tap the button below to Head back to the Main Menu.", Toast.LENGTH_LONG).show();

        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_SCORE, score);
        setResult(RESULT_OK, resultIntent);

        buttonConfirmNext.setVisibility(View.GONE);
        twitterShareButton.setVisibility(View.VISIBLE);
        backButton.setVisibility(View.VISIBLE);
    }

    // Calls finish() rather then an intent to adhere Activity LifeCycles.
    private void finishGame() {
        onQuizFinished();
        finish();
    }

    // If User chooses to exit Quiz before the Quiz is finished.
    // Score is still stored and used if it's a High Score.

    @Override
    public void onBackPressed() {
        if (backPressedTime + 3500 > System.currentTimeMillis()) {
            finishGame();
        } else {
            Toast.makeText(this, "Press back again to finish Quiz early", Toast.LENGTH_LONG).show();
        }
        backPressedTime = System.currentTimeMillis();
    }

    // Cancels CountDownTimer when activity is finished.
    // If not called, CountDownTimer will keep running.

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    // Pre-populates tweet with name and score.

    public void shareOnTwitter() {
        String tweetUrl = String.format("https://twitter.com/intent/tweet",
                urlEncode("I just scored " + score + "in Quizzy Me!"));
        Intent intent_twitter = new Intent(Intent.ACTION_VIEW, Uri.parse(tweetUrl));

        // Goes to Twitter App if available on device.
        @SuppressLint("QueryPermissionsNeeded") List<ResolveInfo> matches = getPackageManager().queryIntentActivities(intent_twitter, 0);
        for (ResolveInfo info : matches) {
            if (info.activityInfo.packageName.toLowerCase().startsWith("com.twitter")) {
                intent_twitter.setPackage(info.activityInfo.packageName);
            }
        }

        startActivity(intent_twitter);
    }

    public static String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("URLEncoder.encode() failed for " + s);
        }
    }

    // Lifecycle Method
    // Stores data below into outState. Since activities are destroyed and remade when rotated.

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_SCORE, score);
        outState.putInt(KEY_QUESTION_COUNT, questionCounter);
        outState.putLong(KEY_MILLIS_LEFT, timeLeftInMilliseconds);
        outState.putBoolean(KEY_ANSWERED, ifAnswered);
        outState.putParcelableArrayList(KEY_QUESTION_LIST, questionList);
    }
}
