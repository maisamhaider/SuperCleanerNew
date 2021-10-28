package com.example.junckcleaner.views.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.junckcleaner.R;
import com.example.junckcleaner.annotations.MyAnnotations;
import com.example.junckcleaner.interfaces.TrueFalse;
import com.example.junckcleaner.prefrences.AppPreferences;
import com.example.junckcleaner.utils.MyEmail;

public class ActivityVerification extends AppCompatActivity implements View.OnClickListener, TrueFalse {
    View view_1, view_2, view_3, view_4;
    TextView textView_1, textView_2, textView_3, textView_4, textView_5, textView_6, textView_7,
            textView_8, textView_9, textView_0, textView_resend_OTP;
    String password = "";
    int i = 0;

    AlertDialog dialog;
    AppPreferences preferences;
    String whatToDo = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_verification_otf);
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.color_third));

        preferences = new AppPreferences(this);

        whatToDo = getIntent().getStringExtra(MyAnnotations.EXTRA_WHAT_TO_DO);

        view_1 = findViewById(R.id.view_1);
        view_2 = findViewById(R.id.view_2);
        view_3 = findViewById(R.id.view_3);
        view_4 = findViewById(R.id.view_4);


        textView_1 = findViewById(R.id.textView_1);
        textView_2 = findViewById(R.id.textView_2);
        textView_3 = findViewById(R.id.textView_3);
        textView_4 = findViewById(R.id.textView_4);
        textView_5 = findViewById(R.id.textView_5);
        textView_6 = findViewById(R.id.textView_6);
        textView_7 = findViewById(R.id.textView_7);
        textView_8 = findViewById(R.id.textView_8);
        textView_9 = findViewById(R.id.textView_9);
        textView_0 = findViewById(R.id.textView_0);
        textView_resend_OTP = findViewById(R.id.textView_resend_OTP);

        textView_1.setOnClickListener(this);
        textView_2.setOnClickListener(this);
        textView_3.setOnClickListener(this);
        textView_4.setOnClickListener(this);
        textView_5.setOnClickListener(this);
        textView_6.setOnClickListener(this);
        textView_7.setOnClickListener(this);
        textView_8.setOnClickListener(this);
        textView_9.setOnClickListener(this);
        textView_0.setOnClickListener(this);
        textView_resend_OTP.setOnClickListener(this);
        alertDialog();

    }

    public void loadDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_loading_dialog, null, false);

        builder.setView(view).setCancelable(true);

        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textView_0:
                password += textView_0.getText().toString();
                goToNextActivity(password);
                break;
            case R.id.textView_1:
                password += textView_1.getText().toString();
                goToNextActivity(password);
                break;
            case R.id.textView_2:
                password += textView_2.getText().toString();
                goToNextActivity(password);
                break;
            case R.id.textView_3:
                password += textView_3.getText().toString();
                goToNextActivity(password);
                break;
            case R.id.textView_4:
                password += textView_4.getText().toString();
                goToNextActivity(password);
                break;
            case R.id.textView_5:
                password += textView_5.getText().toString();
                goToNextActivity(password);
                break;
            case R.id.textView_6:
                password += textView_6.getText().toString();
                goToNextActivity(password);
                break;
            case R.id.textView_7:
                password += textView_7.getText().toString();
                goToNextActivity(password);
                break;
            case R.id.textView_8:
                password += textView_8.getText().toString();
                goToNextActivity(password);
                break;
            case R.id.textView_9:
                password += textView_9.getText().toString();
                goToNextActivity(password);
                break;
            case R.id.textView_resend_OTP:
                new MyEmail(this, preferences, preferences.getString(MyAnnotations.SECURITY_EMAIL,
                        ""), this);
                loadDialog();
                break;


        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            dialog = null;
        }
    }

    public void setViews(int length) {
        switch (length) {
            case 0:
                view_1.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_empty_circle));
                view_2.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_empty_circle));
                view_3.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_empty_circle));
                view_4.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_empty_circle));
                break;
            case 1:
                view_1.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_filled_circle));
                break;
            case 2:
                view_1.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_filled_circle));
                view_2.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_filled_circle));
                break;
            case 3:
                view_1.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_filled_circle));
                view_2.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_filled_circle));
                view_3.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_filled_circle));
                break;
            case 4:
                view_1.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_filled_circle));
                view_2.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_filled_circle));
                view_3.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_filled_circle));
                view_4.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_filled_circle));
                break;
        }
    }

    private void goToNextActivity(String password) {
        setViews(password.length());
        if (password.length() == 4) {
            if (preferences.getLong(MyAnnotations.OTP, 1) == Long.parseLong(password)) {

                allSetDialog();

            } else {
                setViews(0);
                this.password = "";
                Toast.makeText(this, "Invalid OTP is entered", Toast.LENGTH_SHORT).show();
            }

        }

    }

    public void alertDialog() {

        View view = LayoutInflater.from(this).inflate(R.layout.layout_applock_two_in_on_dialog,
                null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setView(view);

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageView imageView_icon;
        TextView textView_title, textView_message, textView_button;
        imageView_icon = view.findViewById(R.id.imageView_icon);
        textView_title = view.findViewById(R.id.textView_title);
        textView_message = view.findViewById(R.id.textView_message);
        textView_button = view.findViewById(R.id.textView_button);

        imageView_icon.setImageResource(R.drawable.ic_check_email_icon);
        textView_title.setText("Check your Email");
        textView_message.setText("Your request has been sent successfully\nplease check your email");

        textView_button.setOnClickListener(view1 -> {
            dialog.dismiss();
        });


    }

    public void allSetDialog() {

        View view = LayoutInflater.from(this).inflate(R.layout.layout_applock_two_in_on_dialog,
                null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setView(view);

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageView imageView_icon;
        TextView textView_title, textView_message, textView_button;
        imageView_icon = view.findViewById(R.id.imageView_icon);
        textView_title = view.findViewById(R.id.textView_title);
        textView_message = view.findViewById(R.id.textView_message);
        textView_button = view.findViewById(R.id.textView_button);

        imageView_icon.setImageResource(R.drawable.ic_all_set_icon);
        textView_title.setText("You are all set!");
        textView_message.setText("You have successfully recover your\npassword");

        textView_button.setOnClickListener(view1 -> {
            dialog.dismiss();
        });
        dialog.setOnDismissListener(dialogInterface -> {
            if (whatToDo.equals(MyAnnotations.FORGOT_PATTERN)) {
                startActivity(new Intent(this, ActivityCreatePattern.class)
                        .putExtra(MyAnnotations.EXTRA_WHAT_TO_DO, MyAnnotations.CREATE_PATTERN));
            } else if (whatToDo.equals(MyAnnotations.FORGOT_PIN)) {
                startActivity(new Intent(this, ActivityCreatePin.class)
                        .putExtra(MyAnnotations.EXTRA_WHAT_TO_DO, MyAnnotations.CREATE_PIN));
            }
            preferences.addLong(MyAnnotations.OTP, 1);
            finish();
        });


    }


    @Override
    public void isTrue(boolean isTrue) {
        if (isTrue) {
            if (dialog != null) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                dialog = null;
            }
            new Handler(Looper.getMainLooper()).post(this::alertDialog);
        } else {
            if (dialog != null) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                dialog = null;
            }

        }
    }
}
