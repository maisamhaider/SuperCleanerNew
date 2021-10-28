package com.example.junckcleaner.views.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.junckcleaner.R;
import com.example.junckcleaner.annotations.MyAnnotations;
import com.example.junckcleaner.interfaces.TrueFalse;
import com.example.junckcleaner.prefrences.AppPreferences;
import com.example.junckcleaner.utils.Internet;
import com.example.junckcleaner.utils.MyEmail;

public class ActivityAddSecurityEmail extends AppCompatActivity implements TrueFalse {
    AppPreferences preferences;

    String string;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_security_email);
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.color_third));

        preferences = new AppPreferences(this);


        string = getIntent().getStringExtra(MyAnnotations.EXTRA_WHAT_TO_DO);

        EditText editText_email = findViewById(R.id.editText_email);
        TextView textView_button = findViewById(R.id.textView_button);


        findViewById(R.id.imageView_back).setOnClickListener(v -> {
            finish();
        });

        if (!string.isEmpty() && string.equals(MyAnnotations.FORGOT_PATTERN) ||
                string.equals(MyAnnotations.FORGOT_PIN)) {
//            updateUI(currentUser);
            textView_button.setText("Send");

        } else {

        }

        findViewById(R.id.textView_done).setOnClickListener(v -> {
            String email = editText_email.getText().toString();

            if (!string.isEmpty() && string.equals(MyAnnotations.FORGOT_PATTERN) ||
                    string.equals(MyAnnotations.FORGOT_PIN)) {
                if (!email.isEmpty()) {
                    if (!preferences.getString(MyAnnotations.SECURITY_EMAIL, "").isEmpty()
                            && preferences.getString(MyAnnotations.SECURITY_EMAIL, "").equals(email)) {
                        if (new Internet(this).isConnected()) {

                            new MyEmail(this, preferences, email, this);
                            loadDialog();
                        } else {
                            Toast.makeText(this, "Check your internet connection",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "You don not have any saved email.sorry",
                                Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(this, "Email not provided", Toast.LENGTH_SHORT).show();
                }

            } else {
                if (!email.isEmpty()) {
                    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        Toast.makeText(this, "Invalid Email", Toast.LENGTH_SHORT).show();
                    } else {
                        if (!preferences.getString(MyAnnotations.SECURITY_EMAIL, "").isEmpty()) {
                            if (preferences.getString(MyAnnotations.SECURITY_EMAIL, "").matches(email)) {
                                Toast.makeText(this, "Email Exists", Toast.LENGTH_SHORT).show();

                            } else {
                                //change Email
                                preferences.addString(MyAnnotations.SECURITY_EMAIL, email);
                                Toast.makeText(this, "Email successfully changed", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            // add first time
                            preferences.addString(MyAnnotations.SECURITY_EMAIL, email);
                            Toast.makeText(this, "Email successfully added", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(this, "Email not provided", Toast.LENGTH_SHORT).show();
                }
            }


        });


    }

    public void loadDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_loading_dialog, null, false);

        builder.setView(view).setCancelable(true);

        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

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

    @Override
    public void isTrue(boolean isTrue) {
        if (isTrue) {
            if (dialog != null) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                dialog = null;
            }
            if (string.equals(MyAnnotations.FORGOT_PATTERN)) {
                startActivity(new Intent(ActivityAddSecurityEmail.this,
                        ActivityVerification.class).putExtra(MyAnnotations.EXTRA_WHAT_TO_DO,
                        MyAnnotations.FORGOT_PATTERN));
            } else {
                startActivity(new Intent(ActivityAddSecurityEmail.this,
                        ActivityVerification.class).putExtra(MyAnnotations.EXTRA_WHAT_TO_DO,
                        MyAnnotations.FORGOT_PIN));
            }
            finish();
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