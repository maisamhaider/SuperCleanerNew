package com.example.junckcleaner.views.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.example.junckcleaner.R;
import com.example.junckcleaner.annotations.MyAnnotations;
import com.example.junckcleaner.prefrences.AppPreferences;

public class ActivitySetSecurityQuestion extends AppCompatActivity {


    View view_questions, view_spinner_bg;
    ImageView imageView_arrow;
    ConstraintLayout cl_spinner;
    TextView textView_answer_heading, textViewQuestion,
            textView_q_1,
            textView_q_2,
            textView_q_3,
            textView_q_4,
            textView_q_5;
    TextView textView_next, textView_OTP, textView_OTP_1, textView_header, appCompatTextView25;
    EditText textView_answer;
    boolean spinnerOpened = false;

    AppPreferences preferences;
    int i = 0;
    String string;
    boolean inApp = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_security_question);
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.color_third));

        preferences = new AppPreferences(this);


        string = getIntent().getStringExtra(MyAnnotations.EXTRA_WHAT_TO_DO);
        inApp = getIntent().getBooleanExtra(MyAnnotations.IN_APP, false);
        imageView_arrow = findViewById(R.id.imageView_arrow);
        view_spinner_bg = findViewById(R.id.view_spinner_bg);
        view_questions = findViewById(R.id.view_questions);
        cl_spinner = findViewById(R.id.cl_spinner);
        textView_answer_heading = findViewById(R.id.textView_answer_heading);
        textView_answer = findViewById(R.id.textView_answer);

        textView_OTP = findViewById(R.id.textView_OTP);
        textView_OTP_1 = findViewById(R.id.textView_OTP_1);
        textViewQuestion = findViewById(R.id.textViewQuestion);
        textView_q_1 = findViewById(R.id.textView_q_1);
        textView_q_2 = findViewById(R.id.textView_q_2);
        textView_q_3 = findViewById(R.id.textView_q_3);
        textView_q_4 = findViewById(R.id.textView_q_4);
        textView_q_5 = findViewById(R.id.textView_q_5);

        textView_header = findViewById(R.id.textView_header);
        appCompatTextView25 = findViewById(R.id.appCompatTextView25);
        textView_next = findViewById(R.id.textView_next);
        android:


        if (string != null && string.equals(MyAnnotations.FORGOT_PIN) || string.equals(MyAnnotations.FORGOT_PATTERN)) {
            if (!preferences.getString(MyAnnotations.SECURITY_Q, "").isEmpty()) {
                textViewQuestion.setText(preferences.getString(MyAnnotations.SECURITY_Q, ""));
                textView_header.setText("Security Question");
                imageView_arrow.setVisibility(View.GONE);
            } else {
                view_questions.setEnabled(true);

            }

            view_questions.setEnabled(false);
        } else if ((!string.isEmpty()) &&
                (string.equals(MyAnnotations.CHANGE_PATTERN) ||
                        string.equals(MyAnnotations.CREATE_PATTERN) ||
                        string.equals(MyAnnotations.CHANGE_PIN) ||
                        string.equals(MyAnnotations.CREATE_PIN))) {
            appCompatTextView25.setText("Please answer your Security Question then you can perform next action");

            view_questions.setEnabled(true);
            textView_header.setText("Security Question");
        } else {
            view_questions.setEnabled(true);

        }

        findViewById(R.id.imageView_back).setOnClickListener(v -> {
            if (string.equals(MyAnnotations.FORGOT_PATTERN) && inApp) {
                startActivity(new Intent(this, ActivityCreatePattern.class)
                        .putExtra(MyAnnotations.EXTRA_WHAT_TO_DO, ""));
                finish();
            } else if (string.equals(MyAnnotations.FORGOT_PIN) && inApp) {
                startActivity(new Intent(this, ActivityCreatePin.class)
                        .putExtra(MyAnnotations.EXTRA_WHAT_TO_DO, ""));
                finish();
            } else {
                finish();
            }
        });

        view_questions.setOnClickListener(view -> {
            if (!spinnerOpened) {
                upSpinner();
            } else {
                downSpinner();

            }

        });


        textView_q_1.setOnClickListener(view -> {
            textViewQuestion.setText(textView_q_1.getText().toString());
            downSpinner();
        });
        textView_q_2.setOnClickListener(view -> {
            textViewQuestion.setText(textView_q_2.getText().toString());
            downSpinner();
        });
        textView_q_3.setOnClickListener(view -> {
            textViewQuestion.setText(textView_q_3.getText().toString());
            downSpinner();
        });
        textView_q_4.setOnClickListener(view -> {
            textViewQuestion.setText(textView_q_4.getText().toString());
            downSpinner();
        });
        textView_q_5.setOnClickListener(view -> {
            textViewQuestion.setText(textView_q_5.getText().toString());
            downSpinner();
        });
        textView_OTP.setOnClickListener(view -> {

            if (i >= 5) {
                if (!string.isEmpty() && string.equals(MyAnnotations.FORGOT_PIN)) {
                    startActivity(new Intent(ActivitySetSecurityQuestion.this, ActivityAddSecurityEmail.class)
                            .putExtra(MyAnnotations.EXTRA_WHAT_TO_DO, MyAnnotations.FORGOT_PIN));
                } else {
                    startActivity(new Intent(ActivitySetSecurityQuestion.this, ActivityAddSecurityEmail.class)
                            .putExtra(MyAnnotations.EXTRA_WHAT_TO_DO, MyAnnotations.FORGOT_PATTERN));
                }

            }
        });


        textView_next.setOnClickListener(view -> {
            String question = textViewQuestion.getText().toString();
            String answer = textView_answer.getText().toString();
            if ((!string.isEmpty()) && string.equals(MyAnnotations.CREATE_PATTERN)) {
                goToChangePattern(question, answer, MyAnnotations.CREATE_PATTERN);

            } else if ((!string.isEmpty()) && string.equals(MyAnnotations.CREATE_PIN)) {
                goToChangePattern(question, answer, MyAnnotations.CREATE_PIN);

            } else if ((!string.isEmpty()) && string.equals(MyAnnotations.CHANGE_PATTERN)) {
                goToChangePattern(question, answer, MyAnnotations.CHANGE_PATTERN);

            } else if ((!string.isEmpty()) && string.equals(MyAnnotations.CHANGE_PIN)) {
                goToChangePattern(question, answer, MyAnnotations.CHANGE_PIN);

            } else if ((!string.isEmpty()) && string.equals(MyAnnotations.FORGOT_PIN) ||
                    string.equals(MyAnnotations.FORGOT_PATTERN)) {
                if (preferences.getString(MyAnnotations.SECURITY_Q, "").equals(question)
                        && preferences.getString(MyAnnotations.SECURITY_A, "").equals(answer)) {

                    if (string.equals(MyAnnotations.FORGOT_PIN)) {
                        startActivity(new Intent(this, ActivityCreatePin.class)
                                .putExtra(MyAnnotations.EXTRA_WHAT_TO_DO, MyAnnotations.CREATE_PIN));
                    } else {
                        startActivity(new Intent(this, ActivityCreatePattern.class)
                                .putExtra(MyAnnotations.EXTRA_WHAT_TO_DO, MyAnnotations.CREATE_PATTERN));
                    }
                    finish();
                } else {
                    Toast.makeText(this, "wrong try", Toast.LENGTH_SHORT).show();
                    i++;
                    if (i >= 5) {
                        textView_next.setEnabled(false);
                        textView_OTP.setVisibility(View.VISIBLE);
                        textView_OTP_1.setVisibility(View.VISIBLE);
                    }
                }
            } else {

                if (!answer.isEmpty()) {
                    if (preferences.getString(MyAnnotations.SECURITY_Q, "").equals(question)
                            && preferences.getString(MyAnnotations.SECURITY_A, "").equals(answer)) {
                        Toast.makeText(this, "Question and answer already exists", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Question and answer successfully saved", Toast.LENGTH_SHORT).show();
                        preferences.addString(MyAnnotations.SECURITY_Q, question);
                        preferences.addString(MyAnnotations.SECURITY_A, answer);
                        finish();
                    }

                } else {
                    Toast.makeText(this, "Provide valid answer", Toast.LENGTH_SHORT).show();
                }
            }


        });

    }

    public void upSpinner() {
        spinnerOpened = true;
        view_spinner_bg.setBackgroundResource(R.drawable.shape_stroke_curved_white_opacity_10);
        cl_spinner.setVisibility(View.VISIBLE);
        view_questions.setBackgroundColor(Color.TRANSPARENT);
        imageView_arrow.setImageResource(R.drawable.ic_arrow_up);
        textView_answer_heading.setVisibility(View.GONE);
        textView_answer.setVisibility(View.GONE);
    }

    public void downSpinner() {
        view_questions.setBackgroundResource(R.drawable.shape_stroke_curved_white_opacity_10);
        view_spinner_bg.setBackgroundColor(Color.TRANSPARENT);
        imageView_arrow.setImageResource(R.drawable.ic_arrow_down);
        textView_answer_heading.setVisibility(View.VISIBLE);
        textView_answer.setVisibility(View.VISIBLE);
        cl_spinner.setVisibility(View.GONE);
        spinnerOpened = false;
    }

    @Override
    public void onBackPressed() {
        if (string.equals(MyAnnotations.FORGOT_PATTERN) && inApp) {
            startActivity(new Intent(this, ActivityCreatePattern.class)
                    .putExtra(MyAnnotations.EXTRA_WHAT_TO_DO, ""));
            finish();
        } else if (string.equals(MyAnnotations.FORGOT_PIN) && inApp) {
            startActivity(new Intent(this, ActivityCreatePin.class)
                    .putExtra(MyAnnotations.EXTRA_WHAT_TO_DO, ""));
            finish();
        } else {
            finish();
        }


    }

    public void goToChangePattern(String question, String answer, String where) {
        if (preferences.getString(MyAnnotations.SECURITY_Q, "").equals(question)
                && preferences.getString(MyAnnotations.SECURITY_A, "").equals(answer)) {

            if (where.equals(MyAnnotations.CHANGE_PATTERN)) {
                startActivity(new Intent(this, ActivityCreatePattern.class)
                        .putExtra(MyAnnotations.EXTRA_WHAT_TO_DO, MyAnnotations.CHANGE_PATTERN));
                finish();
            } else if (where.equals(MyAnnotations.CREATE_PATTERN)) {
                startActivity(new Intent(this, ActivityCreatePattern.class)
                        .putExtra(MyAnnotations.EXTRA_WHAT_TO_DO, MyAnnotations.CREATE_PATTERN));
                finish();
            } else if (where.equals(MyAnnotations.CHANGE_PIN)) {
                startActivity(new Intent(this, ActivityCreatePin.class)
                        .putExtra(MyAnnotations.EXTRA_WHAT_TO_DO, MyAnnotations.CHANGE_PIN));
                finish();
            } else if (where.equals(MyAnnotations.CREATE_PIN)) {
                startActivity(new Intent(this, ActivityCreatePin.class)
                        .putExtra(MyAnnotations.EXTRA_WHAT_TO_DO, MyAnnotations.CREATE_PIN));
                finish();
            }

        } else {
            i++;
            textView_OTP.setVisibility(View.VISIBLE);
            textView_OTP.setText("Incorrect answer or question");
        }
    }
}