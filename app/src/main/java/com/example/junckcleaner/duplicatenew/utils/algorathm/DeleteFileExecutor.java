package com.example.junckcleaner.duplicatenew.utils.algorathm;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.example.junckcleaner.duplicatenew.models.FileDetails;
import com.example.junckcleaner.duplicatenew.utils.Constants;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DeleteFileExecutor {
    @SuppressLint("StaticFieldLeak")
    Activity activity;
    @SuppressLint("StaticFieldLeak")
    Context context;
    private ProgressDialog deleteDialog;
    ArrayList<FileDetails> fileToDeleted;

    public DeleteFileExecutor(Context context, Activity activity, ArrayList<FileDetails> fileToDeleted) {
        this.context = context;
        this.activity = activity;
        this.fileToDeleted = fileToDeleted;
    }

    public void execute() {
        new Handler().post(() -> {
            onPreExecute();

            Executor executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler();
            executor.execute(() -> {
                doInBackground();
                handler.post(() -> {
                    if (deleteDialog.isShowing())
                    {
                        deleteDialog.dismiss();
                    }
                });

            });

        });
    }

    protected void onPreExecute() {
        this.deleteDialog = new ProgressDialog(this.activity);
        this.deleteDialog.setMessage("Files will be deleted");
        this.deleteDialog.setCancelable(true);
        this.deleteDialog.show();

    }

    protected void doInBackground() {
        if (this.fileToDeleted != null) {
            deletePhotosByPosition();
        }
     }

    private void deletePhotosByPosition() {
        for (int i = 0; i < fileToDeleted.size(); i++) {
            FileDetails fileDetails = fileToDeleted.get(i);
            try {
                String imagePath = fileDetails.getFilePath();
                Log.e("deletePhotosByPosition", "---asdfasd-----" + imagePath);
                Constants.deleteFile(activity, this.context, imagePath);
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }
    }



}
