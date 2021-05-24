package au.edu.jcu.cp3406.educationalapp;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class GameActivity extends AppCompatActivity {

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
    private TextView textViewCategory;
    private RadioGroup rbGroup;
    private RadioButton rb1;
    private RadioButton rb2;
    private RadioButton rb3;
    private Button buttonConfirmNext;
    private Button twitterShareButton;
    private Button backButton;

    private ColorStateList textColorDefaultRb; // Text colour for radio button.
    // TODO: 22/05/2021  Could implement button here instead of radio buttons, correct green colour incorrect red colour.

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        textViewQuestion = findViewById(R.id.text_view_question);
        textViewScore = findViewById(R.id.text_view_score);
        textViewQuestionCount = findViewById(R.id.text_view_question_count);
        textViewCategory = findViewById(R.id.text_view_category);
        textViewCountDown = findViewById(R.id.text_view_countdown);

        rbGroup = findViewById(R.id.radio_group);
        rb1 = findViewById(R.id.radio_button1);
        rb2 = findViewById(R.id.radio_button2);
        rb3 = findViewById(R.id.radio_button3);

        buttonConfirmNext = findViewById(R.id.button_confirm_next);
        textColorDefaultRb = rb1.getTextColors();
        textColorDefaultCd = textViewCountDown.getTextColors();


        // Functionality to go to Twitter Activity after quiz is completed.
        // Button is hidden until quiz completion.
        twitterShareButton = findViewById(R.id.twitter_share_button);
        twitterShareButton.setVisibility(View.GONE);
        twitterShareButton.setOnClickListener(view -> startTwitterActivity());

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
    }

    private void startTwitterActivity(){
        Intent intent = new Intent(GameActivity.this, TwitterActivity.class);
        startActivity(intent);
    }

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

    // Save score so that it is checked with high score in MainActivity.
    private void onQuizFinished() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_SCORE, score);
        setResult(RESULT_OK, resultIntent);
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
            Toast.makeText(this, "Press back again to finish", Toast.LENGTH_LONG).show();
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

    // Lifecycle Method
    // Stores data below into outState. Since activities are destroyed and remade when rotated.

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_SCORE, score);
        outState.putInt(KEY_QUESTION_COUNT, questionCounter);
        outState.putLong(KEY_MILLIS_LEFT, timeLeftInMilliseconds);
        outState.putBoolean(KEY_ANSWERED, ifAnswered);
        outState.putParcelableArrayList(KEY_QUESTION_LIST, questionList);
    }
}
