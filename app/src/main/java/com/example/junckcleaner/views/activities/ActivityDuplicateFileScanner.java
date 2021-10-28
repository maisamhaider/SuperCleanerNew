package com.example.junckcleaner.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.example.junckcleaner.R;
import com.example.junckcleaner.annotations.MyAnnotations;
import com.example.junckcleaner.duplicatenew.models.FileDetails;
import com.example.junckcleaner.duplicatenew.utils.DuplicatePreferences;
import com.example.junckcleaner.duplicatenew.utils.algorathm.ObserveFilesExecutor;
import com.example.junckcleaner.interfaces.AdClosed;
import com.example.junckcleaner.interfaces.DuplicateScanningListener;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.List;

public class ActivityDuplicateFileScanner extends BaseActivity implements DuplicateScanningListener,
        AdClosed {
    public static String whichButtonClicked = "";
    ConstraintLayout clLoading;
    LottieAnimationView lottie1, lottie2, lottie3, lottie4, lottie5;
    boolean scanning = false;

    public static List<ArrayList<FileDetails>> duplicatesList;
    TextView textView_progress, textView_heading_scanning, textViewScanning;
    ImageView imageView_back_scanning;
    ObserveFilesExecutor observeFilesExecutor;
    ShimmerFrameLayout shimmerViewContainer;
    public boolean adPlayed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duplicate_file_scanner);
        clLoading = findViewById(R.id.cl_loading);
        lottie1 = findViewById(R.id.lottie1);
        lottie2 = findViewById(R.id.lottie2);
        lottie3 = findViewById(R.id.lottie3);
        lottie4 = findViewById(R.id.lottie4);
        lottie5 = findViewById(R.id.lottie5);
        textView_heading_scanning = findViewById(R.id.textView_heading_scanning);
        imageView_back_scanning = findViewById(R.id.imageView_back_scanning);
        textViewScanning = findViewById(R.id.textViewScanning);
        shimmerViewContainer = findViewById(R.id.shimmer_view_container);

        duplicatesList = new ArrayList<>();
        imageView_back_scanning.setOnClickListener(view -> {
            observeFilesExecutor.stopAsyncTask();
            finish();
        });

        findViewById(R.id.imageView_back).setOnClickListener(view -> finish());
        textView_progress = findViewById(R.id.textView_progress);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.color_main));
        shimmerViewContainer.startShimmer();

        findViewById(R.id.textView_scan_all).setOnClickListener(v -> {
            textView_heading_scanning.setText("");
            clLoading.setVisibility(View.VISIBLE);
            lottie1.setVisibility(View.VISIBLE);
            lottie2.setVisibility(View.GONE);
            lottie3.setVisibility(View.GONE);
            lottie4.setVisibility(View.GONE);
            lottie5.setVisibility(View.GONE);
            textView_heading_scanning.setText("Deep Scan");
            clLoading.setBackgroundResource(R.drawable.shape_gradient_maincolor_1);
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.color_third));
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.color_main));
            startScanning(MyAnnotations.ALL_SCAN);
            whichButtonClicked = MyAnnotations.ALL_SCAN;
            clLoading.setVisibility(View.VISIBLE);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            scanning = true;


        });
        findViewById(R.id.textView_images_scan).setOnClickListener(v -> {
            clLoading.setVisibility(View.VISIBLE);
            lottie2.setVisibility(View.VISIBLE);
            lottie1.setVisibility(View.GONE);
            lottie3.setVisibility(View.GONE);
            lottie4.setVisibility(View.GONE);
            lottie5.setVisibility(View.GONE);
            textView_heading_scanning.setText("Image Scan");
            clLoading.setBackgroundResource(R.drawable.shape_header_duplicate_iamge);
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.color_mustard_1));
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.color_yellow_sea_1));
            startScanning(MyAnnotations.IMAGES);

            whichButtonClicked = MyAnnotations.IMAGES;
            scanning = true;


        });
        findViewById(R.id.textView_videos_scan).setOnClickListener(v -> {
            clLoading.setVisibility(View.VISIBLE);
            lottie3.setVisibility(View.VISIBLE);
            lottie1.setVisibility(View.GONE);
            lottie2.setVisibility(View.GONE);
            lottie4.setVisibility(View.GONE);
            lottie5.setVisibility(View.GONE);
            textView_heading_scanning.setText("Video Scan");
            clLoading.setBackgroundResource(R.drawable.shape_header_duplicate_videos);
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.color_malibu_2));
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.color_cerulean_1));
            startScanning(MyAnnotations.VIDEOS);

            whichButtonClicked = MyAnnotations.VIDEOS;
            scanning = true;

        });
        findViewById(R.id.textView_audios_scan).setOnClickListener(v -> {
            clLoading.setVisibility(View.VISIBLE);
            lottie4.setVisibility(View.VISIBLE);
            lottie1.setVisibility(View.GONE);
            lottie3.setVisibility(View.GONE);
            lottie2.setVisibility(View.GONE);
            lottie5.setVisibility(View.GONE);
            textView_heading_scanning.setText("Audio Scan");
            clLoading.setBackgroundResource(R.drawable.shape_header_duplicate_audios);
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.color_atomic_tangerine_1));
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.color_clementine_1));
            startScanning(MyAnnotations.AUDIOS);

            whichButtonClicked = MyAnnotations.AUDIOS;


        });
        findViewById(R.id.textView_documents_scan).setOnClickListener(v -> {
            clLoading.setVisibility(View.VISIBLE);
            lottie5.setVisibility(View.VISIBLE);
            lottie1.setVisibility(View.GONE);
            lottie3.setVisibility(View.GONE);
            lottie2.setVisibility(View.GONE);
            lottie4.setVisibility(View.GONE);
            textView_heading_scanning.setText("Document Scan");
            clLoading.setBackgroundResource(R.drawable.shape_header_duplicate_docs);
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.color_pink_salmon_1));
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.color_maroon_flush_1));
            startScanning(MyAnnotations.DOCUMENTS);
            whichButtonClicked = MyAnnotations.DOCUMENTS;
            scanning = true;

        });

        refreshAdSmallNative(findViewById(R.id.fl_adplaceholder));

    }

    private void startScanning(String type) {
        observeFilesExecutor = new ObserveFilesExecutor(this, this, type);
        observeFilesExecutor.execute();
    }


    @Override
    public void onBackPressed() {
//        if (!scanning) {
        observeFilesExecutor.stopAsyncTask();
        super.onBackPressed();
//        } else {
//            Toast.makeText(this, "During Scanning you can not back.Sorry", Toast.LENGTH_SHORT).show();
//        }
    }

    @Override
    public void checkScanning() {
        if (whichButtonClicked.equals(MyAnnotations.IMAGES) ||
                whichButtonClicked.equals(MyAnnotations.VIDEOS) ||
                whichButtonClicked.equals(MyAnnotations.AUDIOS) ||
                whichButtonClicked.equals(MyAnnotations.DOCUMENTS) ||
                whichButtonClicked.equals(MyAnnotations.ALL_SCAN)) {
            return;
        }
        DuplicatePreferences.setStopScanForNotification(this, true);
        DuplicatePreferences.setNavigateFromHome(this, true);


    }

    @Override
    public void publishProgress(String... count) {
        try {
            if (whichButtonClicked.equals(MyAnnotations.IMAGES) ||
                    whichButtonClicked.equals(MyAnnotations.VIDEOS) ||
                    whichButtonClicked.equals(MyAnnotations.AUDIOS) ||
                    whichButtonClicked.equals(MyAnnotations.DOCUMENTS) ||
                    whichButtonClicked.equals(MyAnnotations.ALL_SCAN)) {
                if (count != null)
                    if (count.length > 1) {
                        if (count[0].equalsIgnoreCase("Sorting")) {
                            return;
                        }
                        String counter = count[0] + " / " + count[1];
                        String msg = "Files " + counter;
                        tvUpdates(msg);
                    }
            } else {
                if (count[0].equalsIgnoreCase("Sorting")) {
                    return;
                }
                String msg = "Files " + count[0];
                tvUpdates(msg);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void tvUpdates(String msg) {

        textViewScanning.setText(msg);
    }

    @Override
    public void publishProgress(List<ArrayList<FileDetails>> duplicatesList) {
        if (duplicatesList != null) {
            ActivityDuplicateFileScanner.duplicatesList = duplicatesList;
            if (adPlayed) {
                adPlayed = false;
                if (!observeFilesExecutor.stopped) {
                    showInterstitialActivity(this, false);
                }
            } else {
                adPlayed = true;
                if (!observeFilesExecutor.stopped) {
                    showInterstitialActivity(this, true);
                }
            }

        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (observeFilesExecutor == null) {
            observeFilesExecutor = new ObserveFilesExecutor(this, this, whichButtonClicked);
        }
        if (observeFilesExecutor.stopped) {
            whichButtonClicked = "";
        }
    }

    public void goToNext() {
        if (!observeFilesExecutor.stopped) {
            clLoading.setVisibility(View.INVISIBLE);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.color_main));
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.white));
            scanning = false;
            switch (whichButtonClicked) {
                case MyAnnotations.ALL_SCAN:
                    startActivity(new Intent(ActivityDuplicateFileScanner.this,
                            ActivityDuplicateFilesCommon.class).putExtra(MyAnnotations.DATA_TYPE,
                            MyAnnotations.ALL_SCAN));
                    break;
                case MyAnnotations.IMAGES:
                    startActivity(new Intent(ActivityDuplicateFileScanner.this,
                            ActivityDuplicateFilesCommon.class).putExtra(MyAnnotations.DATA_TYPE,
                            MyAnnotations.IMAGES));
                    break;
                case MyAnnotations.VIDEOS:
                    startActivity(new Intent(ActivityDuplicateFileScanner.this,
                            ActivityDuplicateFilesCommon.class).putExtra(MyAnnotations.DATA_TYPE,
                            MyAnnotations.VIDEOS));
                    break;
                case MyAnnotations.AUDIOS:
                    startActivity(new Intent(ActivityDuplicateFileScanner.this,
                            ActivityDuplicateFilesCommon.class).putExtra(MyAnnotations.DATA_TYPE,
                            MyAnnotations.AUDIOS));
                    break;
                case MyAnnotations.DOCUMENTS:
                    startActivity(new Intent(ActivityDuplicateFileScanner.this,
                            ActivityDuplicateFilesCommon.class).putExtra(MyAnnotations.DATA_TYPE,
                            MyAnnotations.DOCUMENTS));
                    break;
            }
        }
    }

    @Override
    public void addDismissed(boolean closed) {
        goToNext();
    }

    @Override
    public void addFailed(boolean closed) {
        goToNext();
    }
}