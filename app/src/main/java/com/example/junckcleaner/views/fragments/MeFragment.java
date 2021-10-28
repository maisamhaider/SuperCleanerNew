package com.example.junckcleaner.views.fragments;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.junckcleaner.BuildConfig;
import com.example.junckcleaner.R;
import com.example.junckcleaner.utils.Internet;
import com.example.junckcleaner.utils.Utils;
import com.example.junckcleaner.utils.Version;
import com.example.junckcleaner.views.activities.ActivityFeedback;
import com.example.junckcleaner.views.activities.ActivityMoreApps;
import com.example.junckcleaner.views.activities.ActivitySettings;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class MeFragment extends Fragment {

    Utils utils;
    Internet internet;

    public MeFragment() {
        // Required empty public constructor
    }

    public static MeFragment newInstance() {
        return new MeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_me, container, false);
        utils = new Utils(getActivity());
        internet = new Internet(getContext());
        ConstraintLayout cl_settings,
                cl_feedback,
                cl_upgrade,
                cl_share,
                cl_otherApp,
                cl_likeOnFacebook;

        cl_settings = root.findViewById(R.id.cl_settings);
        cl_feedback = root.findViewById(R.id.cl_feedback);
        cl_upgrade = root.findViewById(R.id.cl_upgrade);
        cl_share = root.findViewById(R.id.cl_share);
        cl_otherApp = root.findViewById(R.id.cl_otherApp);
        cl_likeOnFacebook = root.findViewById(R.id.cl_likeOnFacebook);

        cl_settings.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), ActivitySettings.class));

        });
        cl_feedback.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), ActivityFeedback.class));

        });
        cl_upgrade.setOnClickListener(v -> {
            if (new Internet(getActivity()).isConnected()) {
                rateUs();
            } else {
                Toast.makeText(getContext(), "No active internet connection", Toast.LENGTH_SHORT).show();
            }

//            if (new Internet(getActivity()).isConnected()) {
//                ReviewManager manager = ReviewManagerFactory.create(getContext());
//
//                Task<ReviewInfo> request = manager.requestReviewFlow();
//                request.addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        // We can get the ReviewInfo object
//                        ReviewInfo reviewInfo = task.getResult();
//                        Task<Void> flow = manager.launchReviewFlow(getActivity(), reviewInfo);
//                        flow.addOnCompleteListener(task1 -> {
//                            // The flow has finished. The API does not indicate whether the user
//                            // reviewed or not, or even whether the review dialog was shown. Thus, no
//                            // matter the result, we continue our app flow.
//                            if (task1.isSuccessful()) {
//                                Toast.makeText(getContext(), "Successful", Toast.LENGTH_SHORT).show();
//
//                            } else {
//                                Toast.makeText(getContext(), "not successful", Toast.LENGTH_SHORT).show();
//
//                            }
//                        });
//                    } else {
//                        // There was some problem, log or handle the error code.
//                        Toast.makeText(getContext(), "some problem", Toast.LENGTH_SHORT).show();
//                    }
//                });
//            } else {
//            }

//            if (new Internet(getActivity()).isConnected()) {
//                AppUpdateManager updateManager = AppUpdateManagerFactory.create(getContext());
//                Task<AppUpdateInfo> appUpdateInfoTask = updateManager.getAppUpdateInfo();
//                appUpdateInfoTask.addOnSuccessListener(result -> {
//                    if (result.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
//                        try {
//                            updateManager.startUpdateFlowForResult(
//                                    // Pass the intent that is returned by 'getAppUpdateInfo()'.
//                                    result,
//                                    // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
//                                    AppUpdateType.FLEXIBLE,
//                                    // The current activity making the update request.
//                                    getActivity(),
//                                    // Include a request code to later monitor this update request.
//                                    1111);
//                        } catch (IntentSender.SendIntentException e) {
//                            e.printStackTrace();
//                            Toast.makeText(getContext(), "unable to update now", Toast.LENGTH_SHORT).show();
//                        }
//                    } else {
//                        Toast.makeText(getContext(), "Update not available", Toast.LENGTH_SHORT).show();
//
//                    }
//
//                }).addOnFailureListener(e -> {
//                    e.printStackTrace();
//                    Toast.makeText(getContext(), "unable to update now", Toast.LENGTH_SHORT).show();
//
//                });
//
//            } else {
//                Toast.makeText(getContext(), "No active internet connection", Toast.LENGTH_SHORT).show();
//
//            }

//            if (new Internet(getActivity()).isConnected()) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                View view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_loading_dialog, null, false);
//
//                builder.setView(view);
//
//                AlertDialog dialog = builder.create();
//                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                dialog.show();
//                AtomicReference<String> newVer = new AtomicReference<>("");
//                final String pack = getContext().getPackageName();
//                final String currentVersion = utils.appInfo(pack, MyAnnotations.APP_VERSION);
//                final String appName = utils.appInfo(pack, MyAnnotations.APP_NAME);
//                Thread thread = new Thread(() -> {
//                    // do background
//                    newVer.set(update(appName));
////                    newVer.set("1. 2");
//                    // do onPostExecute
//                    getActivity().runOnUiThread(() -> {
//                        if (!newVer.get().equals("")) {
//                            if (checkUpdate(newVer.get(), currentVersion)) {
//                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" +
//                                        getActivity().getPackageName())));
//                            } else {
//                                Toast.makeText(getActivity(), "No new update found ",
//                                        Toast.LENGTH_SHORT).show();
//                            }
//
//                        } else {
//                            Toast.makeText(getActivity(), "No new update found ",
//                                    Toast.LENGTH_SHORT).show();
//                        }
//                        dialog.dismiss();
//                    });
//                });
//                thread.start();
//            } else {
//                Toast.makeText(getActivity(), "Check your internet connection",
//                        Toast.LENGTH_SHORT).show();
//            }
        });
        cl_share.setOnClickListener(v -> {
            share();
        });
        cl_otherApp.setOnClickListener(v -> {
            if (internet.isConnected()) {
                startActivity(new Intent(getContext(), ActivityMoreApps.class));
            } else {
                Toast.makeText(getActivity(), "Check your internet connection",
                        Toast.LENGTH_SHORT).show();
            }

        });
        cl_likeOnFacebook.setOnClickListener(v -> {
            if (new Internet(getActivity()).isConnected()) {
                likeOnFacebook();
            } else {
                Toast.makeText(getActivity(), "Check your internet connection",
                        Toast.LENGTH_SHORT).show();
            }
        });

        return root;
    }

    public void likeOnFacebook() {

        String facebookId = "fb://page/621045061409279";
        String urlPage = "https://www.facebook.com/HyderMeessam/?ref=pages_you_manage";

        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(facebookId)));
        } catch (Exception e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(urlPage)));
        }
    }

    public String update(String appName) {
        String newVersion = "";

        try {
            String url = "http://www.google.com";
            Uri uri = Uri.parse(
                    "https://play.google.com/store/apps/details?id=");
            Document doc = Jsoup.connect(uri + appName + "&hl=en")
                    .timeout(30000)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer(url)
                    .ignoreHttpErrors(true)
                    .get();

            Elements element = doc.getElementsContainingOwnText("Current Version");
            for (Element elem : element) {
                elem.siblingElements();
                Elements sibElemets = elem.siblingElements();
                for (Element sibElemet : sibElemets) {
                    newVersion = sibElemet.text();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return newVersion;
    }

    private boolean checkUpdate(String newV, String currentV) {

        try {
            if (newV != null && !newV.isEmpty()) {
                Version versionCurrent = new Version(currentV);
                Version versionNew = new Version(newV);
                if (!versionCurrent.equals(versionNew)) {
                    if (versionNew.compareTo(versionCurrent) > 0) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    private void rateUs() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = this.getLayoutInflater().inflate(R.layout.layout_rate_us_dialog, null);
        builder.setView(view);
        builder.setCancelable(true);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        TextView rateUse = view.findViewById(R.id.rateUse);
        ImageView imageVIewCross = view.findViewById(R.id.imageVIewCross);
        rateUse.setOnClickListener(view1 -> {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="
                        + getContext().getPackageName())));
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" +
                        getContext().getPackageName())));
            }
            dialog.dismiss();
        });
        imageVIewCross.setOnClickListener(view1 -> {

            dialog.dismiss();
        });

    }

    public void share() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                "Hey check out my app at: https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

}