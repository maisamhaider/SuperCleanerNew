package com.example.junckcleaner.views.fragments.frags;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.junckcleaner.R;
import com.example.junckcleaner.annotations.MyAnnotations;
import com.example.junckcleaner.duplicatenew.adapters.AdapterDuplicate;
import com.example.junckcleaner.duplicatenew.models.FileDetails;
import com.example.junckcleaner.duplicatenew.utils.Constants;
import com.example.junckcleaner.duplicatenew.utils.DuplicatePreferences;
import com.example.junckcleaner.duplicatenew.utils.StopScanning;
import com.example.junckcleaner.duplicatenew.utils.Functions;
import com.example.junckcleaner.duplicatenew.utils.DuplicateListener;
import com.example.junckcleaner.views.activities.ActivityDuplicateFileScanner;
import com.example.junckcleaner.views.activities.ActivityDuplicateFilesCommon;

import java.util.ArrayList;

public class FragmentAudio extends Fragment implements DuplicateListener {

    private static View view;
    private Activity activity;
    private Context context;

    private LinearLayoutManager mLayoutManager;

    public static RecyclerView recyclerView;
    TextView TextView_delete_click, textView_not_found;
    static TextView TextView_delete;
    private AdapterDuplicate adapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    class updateMarked implements Runnable {
        updateMarked() {
        }

        @SuppressLint("SetTextI18n")
        public void run() {
            Constants.fileToBeDeleted.clear();
            Constants.fileSize = 0;
            for (ArrayList<FileDetails> singleGroup : ActivityDuplicateFileScanner.duplicatesList) {
                for (FileDetails fileDetails :
                        singleGroup) {
                    if (fileDetails.isChecked()) {
                        Constants.fileToBeDeleted.add(fileDetails);
                        Constants.fileSize += fileDetails.getFileSize();
                    }
                }
            }
            TextView_delete.setText("Delete " + "(" + Formatter.formatFileSize(context,
                    Constants.fileSize) + ")");
        }
    }

    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_audios, container, false);
        this.activity = getActivity();
        assert this.activity != null;
        this.context = this.activity.getApplicationContext();
        uncheckFirstItem();
        initAudios();
        new updateMarked().run();

        return view;
    }

    private void initAudios() {

        recyclerView = view.findViewById(R.id.recyclerView);
        TextView_delete = view.findViewById(R.id.TextView_delete);
        TextView_delete_click = view.findViewById(R.id.TextView_delete_click);
        textView_not_found = view.findViewById(R.id.textView_not_found);

        view.findViewById(R.id.imageView_back).
                setOnClickListener(view -> ((ActivityDuplicateFilesCommon) getActivity()).finishCall());


        activity.getWindow().
                setStatusBarColor(ContextCompat.getColor(context, R.color.color_clementine_1));
        adapter = new AdapterDuplicate(getActivity(), this,
                ActivityDuplicateFileScanner.duplicatesList, MyAnnotations.AUDIOS);

        mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        TextView_delete_click.setOnClickListener(v -> {
            if (!ActivityDuplicateFileScanner.duplicatesList.isEmpty()) {
                deleteDuplicate();
            }
        });


        new Handler(Looper.getMainLooper()).post(() -> {

            getActivity().runOnUiThread(() -> {
                if (ActivityDuplicateFileScanner.duplicatesList.size() != 0) {
                    textView_not_found.setVisibility(View.GONE);
                    recyclerView.setAdapter(adapter);
                    return;
                }

                recyclerView.setVisibility(View.GONE);
                textView_not_found.setVisibility(View.VISIBLE);
                TextView_delete_click.setVisibility(View.GONE);
                TextView_delete.setVisibility(View.GONE);
            });
        });


    }


    private void uncheckFirstItem() {
        for (ArrayList<FileDetails> singleGroup : ActivityDuplicateFileScanner.duplicatesList) {
            try {
                singleGroup.get(0).setChecked(false);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    private void deleteDuplicate() {
        if (Constants.fileToBeDeleted.size() <= 0) {
            Functions.showToastMsg(getActivity(), getString(R.string.least_one_audio));

        } else if (Functions.getSDCardPath(getContext()) == null) {
            showDeleteDialog();
        } else if (DuplicatePreferences.getStorageAccessFrameWorkURIPermission(getContext()) != null) {
            showDeleteDialog();
        }
    }



    private void showDeleteDialog() {
        DuplicatePreferences.setDeletedSize(getActivity(), Constants.fileSize);
        new StopScanning(getActivity(), getActivity()).deleteAlertPopUp(
                MyAnnotations.AUDIOS,
                Constants.fileToBeDeleted
        );
    }

    public void onPause() {
        super.onPause();

    }

    public void onResume() {
        super.onResume();
        this.activity = getActivity();
        assert this.activity != null;

    }


    @Override
    public void duplicateListener() {
        try {
            requireActivity().runOnUiThread(new updateMarked());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
