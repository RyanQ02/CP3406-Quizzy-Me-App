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

import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class GameActivity extends AppCompatActivity {

    public static final String EXTRA_SCORE = "extraScore";
    private static final long COUNTDOWN_IN_MILLISECONDS = 45000;

    private TextView textViewQuestion;
    private TextView textViewScore;
    private TextView textViewQuestionCount;
    private TextView textViewCountDown;
    private RadioGroup rbGroup;
    private RadioButton rb1;
    private RadioButton rb2;
    private RadioButton rb3;
    private Button buttonConfirmNext;
    private Button highScoreActivityButton;
    private Button backButton;

    private ColorStateList textColorDefaultRb; // Text colour for radio button.
    // TODO: 22/05/2021  Could implement button here instead of radio buttons, correct green colour incorrect red colour.

    private ColorStateList textColorDefaultCd;

    private CountDownTimer countDownTimer;
    private long timeLeftInMilliseconds;

    private List<Question> questionList;
    private int questionCounter;
    private int questionCountTotal;
    private Question currentQuestion;

    private int score;
    private boolean ifAnswered; // Automatically select answer if time has run out or show next question if answered.

    private long backPressedTime;

    private int countDownInterval = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        textViewQuestion = findViewById(R.id.text_view_question);
        textViewScore = findViewById(R.id.text_view_score);
        textViewQuestionCount = findViewById(R.id.text_view_question_count);
        textViewCountDown = findViewById(R.id.text_view_countdown);
        rbGroup = findViewById(R.id.radio_group);
        rb1 = findViewById(R.id.radio_button1);
        rb2 = findViewById(R.id.radio_button2);
        rb3 = findViewById(R.id.radio_button3);
        buttonConfirmNext = findViewById(R.id.button_confirm_next);

        highScoreActivityButton = findViewById(R.id.high_score_activity_button);
        highScoreActivityButton.setVisibility(View.GONE);

        backButton = findViewById(R.id.back_button);
        backButton.setVisibility(View.GONE);

        textColorDefaultRb = rb1.getTextColors();
        textColorDefaultCd = textViewCountDown.getTextColors();

        QuizDb dbHelper = new QuizDb(this);
        questionList = dbHelper.getAllQuestions();
        questionCountTotal = questionList.size();
        Collections.shuffle(questionList);

        showNextQuestion();

        // When confirm button is pressed, option buttons are checked to see if users has selected a answer.

        buttonConfirmNext.setOnClickListener(view -> {
            if (!ifAnswered) {
                if (rb1.isChecked() || rb2.isChecked() || rb3.isChecked()) {
                    checkAnswer();
                } else {
                    Toast.makeText(GameActivity.this, "Please choose a answer", Toast.LENGTH_SHORT).show();
                }
            } else {
                showNextQuestion();
            }
        });
        // Go to High Score Activity after game has finished.
        highScoreActivityButton.setOnClickListener(view -> {
            Intent intent = new Intent(GameActivity.this, HighScoreActivity.class);
            GameActivity.this.startActivity(intent);
        });

        // Back button call onFinish rather then an intent to adhere Activity LifeCycles.
        backButton.setOnClickListener(view -> finishGame());
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
            highScoreActivityButton.setVisibility(View.VISIBLE);
            backButton.setVisibility(View.VISIBLE);
            buttonConfirmNext.setVisibility(View.INVISIBLE);

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
        if (timeLeftInMilliseconds < 10000) {
            textViewCountDown.setTextColor(Color.RED);
        } else {
            textViewCountDown.setTextColor(textColorDefaultCd);
        }
        if (timeLeftInMilliseconds == 0) {
            highScoreActivityButton.setVisibility(View.VISIBLE);
            buttonConfirmNext.setVisibility(View.VISIBLE);
        }
    }

    // if answer_number is == to currentQuestion, add score + 1

    private void checkAnswer() {
        ifAnswered = true;

        // Cancels countdown timer when true.
        countDownTimer.cancel();

        RadioButton rbSelected = findViewById(rbGroup.getCheckedRadioButtonId());
        int answer_number = rbGroup.indexOfChild(rbSelected) + 1;
        if (answer_number == currentQuestion.getAnswer_number()) {
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

    // Save score so that it is checked with high score in HighScoreActivity.
    // Game Activity has ended and moves back to MainActivity.
    private void finishGame() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_SCORE, score);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    // If User chooses to exit Quiz before the Quiz is finished.
    // Score is still stored and used if it's a High Score.

    @Override
    public void onBackPressed() {
        if (backPressedTime + 3500 > System.currentTimeMillis()) {
            finishGame();
        } else {
            Toast.makeText(this, "Press back button again to confirm.", Toast.LENGTH_LONG).show();
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
}