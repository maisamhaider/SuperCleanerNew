package com.example.junckcleaner.views.activities;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.format.Formatter;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.junckcleaner.R;
import com.example.junckcleaner.utils.Internet;

import java.util.ArrayList;

public class ActivityFeedback extends AppCompatActivity {

    EditText editText_feedback_input, edittext_email;
    TextView textView_done, textView_add_sc, textView_add_second_image, textView_add_third_image;
    ConstraintLayout cl_first_add;
    ArrayList<Uri> imagesUriArrayList = new ArrayList<>();
    ImageView imageView_first, imageView_second, imageView_third;
    CardView card_first, card_second, card_third;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        editText_feedback_input = findViewById(R.id.editText_feedback_input);
        edittext_email = findViewById(R.id.edittext_email);
        textView_done = findViewById(R.id.textView_done);
        TextView textView_settings_info = findViewById(R.id.textView_settings_info);

        cl_first_add = findViewById(R.id.cl_first_add);
        textView_add_sc = findViewById(R.id.textView_add_sc);
        textView_add_second_image = findViewById(R.id.textView_add_second_image);
        textView_add_third_image = findViewById(R.id.textView_add_third_image);

        imageView_first = findViewById(R.id.imageView_first);
        imageView_second = findViewById(R.id.imageView_second);
        imageView_third = findViewById(R.id.imageView_third);

        card_first = findViewById(R.id.card_first);
        card_second = findViewById(R.id.card_second);
        card_third = findViewById(R.id.card_third);


        String text = getString(R.string.system_info);
        SpannableString spannableString = new SpannableString(text);
        ClickableSpan system_info = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {

                systemInfoDialog();
            }
        };
        ForegroundColorSpan purple = new ForegroundColorSpan(
                ContextCompat.getColor(this, R.color.color_second));
        spannableString.setSpan(purple, 5, 16, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(system_info, 5, 16, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView_settings_info.setText(spannableString);
        textView_settings_info.setMovementMethod(LinkMovementMethod.getInstance());

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        editText_feedback_input.setImeOptions(EditorInfo.IME_ACTION_DONE);
        edittext_email.setImeOptions(EditorInfo.IME_ACTION_DONE);


        findViewById(R.id.imageView_back).setOnClickListener(v -> finish());
        editText_feedback_input.setOnClickListener(v -> {
            editText_feedback_input.requestFocus();
            imm.showSoftInput(editText_feedback_input, InputMethodManager.SHOW_IMPLICIT);

        });

        edittext_email.setOnClickListener(v -> {
            edittext_email.requestFocus();
            imm.showSoftInput(edittext_email, InputMethodManager.SHOW_IMPLICIT);

        });


        //pick images
        textView_add_sc.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            openSomeActivityForResult(Intent.createChooser(intent, "Select Picture"));
        });

        textView_add_second_image.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            openSomeActivityForResult(Intent.createChooser(intent, "Select Picture"));
        });
        textView_add_third_image.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            openSomeActivityForResult(Intent.createChooser(intent, "Select Picture"));
        });


        //send email
        textView_done.setOnClickListener(v -> {
            if (new Internet(this).isConnected()) {
                sendEmail(editText_feedback_input.getText().toString(), edittext_email.getText().toString());
            } else {
                Toast.makeText(this, "Check your internet connection",
                        Toast.LENGTH_SHORT).show();
            }
        });

    }

    protected void sendEmail(String feedback, String from_email_optional) {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);

        if (feedback == null || feedback.isEmpty()) {

            Toast.makeText(this, "provide feedback", Toast.LENGTH_SHORT).show();

        } else if (from_email_optional != null && !from_email_optional.isEmpty() &&
                !Patterns.EMAIL_ADDRESS.matcher(from_email_optional).matches()) {
            Toast.makeText(this, "Invalid Email provided", Toast.LENGTH_SHORT).show();
        } else {

            feedback = "Feedback: " + feedback + "\n" +
                    "Model: " + Build.MODEL +
                    "\n" +
                    "Brand: " + Build.BRAND +
                    "\n" +
                    "OS Version: " + Build.VERSION.SDK_INT +
                    "\n" +
                    "Manufacturer: " + Build.MANUFACTURER +
                    "\n" +
                    "RAM: " + Formatter.formatFileSize(this, memoryInfo.totalMem)
            ;

            String[] TO = {"hr@syncmedia.net"};
            Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);

            emailIntent.setType("image/*");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "feedback");
            emailIntent.putExtra(Intent.EXTRA_TEXT, feedback);
            if (!imagesUriArrayList.isEmpty()) {
                emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imagesUriArrayList);
            }
            try {
                startActivity(Intent.createChooser(emailIntent, "Send mail"));
                cl_first_add.setVisibility(View.VISIBLE);
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(ActivityFeedback.this,
                        "There is no email client installed.", Toast.LENGTH_SHORT).show();
            }
        }


    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK &&
                            result.getData() != null) {
                        Uri image = result.getData().getData();
                        cl_first_add.setVisibility(View.GONE);
                        imagesUriArrayList.add(image);
                        if (!imagesUriArrayList.isEmpty()) {
                            if (imagesUriArrayList.size() == 1) {

                                card_first.setVisibility(View.VISIBLE);
                                textView_add_second_image.setVisibility(View.VISIBLE);

                                Glide.with(ActivityFeedback.this)
                                        .load(imagesUriArrayList.get(0)).into(imageView_first);

                            } else if (imagesUriArrayList.size() == 2) {

                                textView_add_second_image.setVisibility(View.GONE);
                                textView_add_third_image.setVisibility(View.VISIBLE);

                                card_first.setVisibility(View.VISIBLE);
                                card_second.setVisibility(View.VISIBLE);

                                Glide.with(ActivityFeedback.this)
                                        .load(imagesUriArrayList.get(0)).into(imageView_first);
                                Glide.with(ActivityFeedback.this)
                                        .load(imagesUriArrayList.get(1)).into(imageView_second);
                            } else {

                                card_first.setVisibility(View.VISIBLE);
                                card_second.setVisibility(View.VISIBLE);
                                card_third.setVisibility(View.VISIBLE);

                                textView_add_second_image.setVisibility(View.GONE);
                                textView_add_third_image.setVisibility(View.GONE);


                                Glide.with(ActivityFeedback.this)
                                        .load(imagesUriArrayList.get(0)).into(imageView_first);
                                Glide.with(ActivityFeedback.this)
                                        .load(imagesUriArrayList.get(1)).into(imageView_second);
                                Glide.with(ActivityFeedback.this)
                                        .load(imagesUriArrayList.get(2)).into(imageView_third);
                            }
                        }

                    }
                }
            });

    public void openSomeActivityForResult(Intent intent) {
        someActivityResultLauncher.launch(intent);
    }

    public void systemInfoDialog() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);

        View view = LayoutInflater.from(this).inflate(R.layout.layout_feedback_info_dialog,
                null,
                false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view).setCancelable(true);
        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        TextView textViewModel = view.findViewById(R.id.textViewModel);
        TextView textViewBrand = view.findViewById(R.id.textViewBrand);
        TextView textViewOS = view.findViewById(R.id.textViewOS);
        TextView textViewManufacturer = view.findViewById(R.id.textViewManufacturer);
        TextView textViewRam = view.findViewById(R.id.textViewRam);

        textViewModel.setText(Build.MODEL);
        textViewBrand.setText(Build.BRAND);
        textViewOS.setText(String.valueOf(Build.VERSION.SDK_INT));
        textViewManufacturer.setText(Build.MANUFACTURER);
        textViewRam.setText(Formatter.formatFileSize(this, memoryInfo.totalMem));
    }
}