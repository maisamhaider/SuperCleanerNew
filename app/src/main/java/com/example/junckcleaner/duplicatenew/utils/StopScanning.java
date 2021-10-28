package com.example.junckcleaner.duplicatenew.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.junckcleaner.R;
import com.example.junckcleaner.annotations.MyAnnotations;
import com.example.junckcleaner.duplicatenew.models.FileDetails;
import com.example.junckcleaner.duplicatenew.utils.algorathm.DeleteFileExecutor;
import com.example.junckcleaner.views.activities.DeleteActivity;

import java.util.ArrayList;


public class StopScanning {
    private final Activity popUpActivity;
    private final Context popUpContext;

    public StopScanning(Context context, Activity activity) {
        this.popUpContext = context;
        this.popUpActivity = activity;
    }


    public void deleteAlertPopUp(String fileType, ArrayList<FileDetails> fileToBeDeletedImages) {
        final ArrayList<FileDetails> arrayList = fileToBeDeletedImages;


        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this.popUpActivity);
        LayoutInflater inflater = this.popUpActivity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_duplicate_file_delete_dialog, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);


        TextView textView_delete = dialogView.findViewById(R.id.textView_delete);
        TextView textView_cancel = dialogView.findViewById(R.id.textView_cancel);

        AlertDialog dialog = dialogBuilder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        textView_delete.setOnClickListener(view -> {
            dialog.dismiss();
            Intent intent = new Intent(StopScanning.this.popUpContext, DeleteActivity.class);
            if (arrayList != null && fileType.equals(MyAnnotations.IMAGES)) {

                intent.putExtra(MyAnnotations.SCAN_TYPE, MyAnnotations.IMAGES);
                new DeleteFileExecutor(popUpContext, popUpActivity, arrayList).execute();

            } else if (arrayList != null && fileType.equals(MyAnnotations.VIDEOS)) {

                intent.putExtra(MyAnnotations.SCAN_TYPE, MyAnnotations.VIDEOS);
                new DeleteFileExecutor((Activity) popUpContext, popUpActivity, arrayList).execute();

            } else if (arrayList != null && fileType.equals(MyAnnotations.AUDIOS)) {

                intent.putExtra(MyAnnotations.SCAN_TYPE, MyAnnotations.AUDIOS);
                new DeleteFileExecutor((Activity) popUpContext, popUpActivity, arrayList).execute();
            } else if (arrayList != null && fileType.equals(MyAnnotations.DOCUMENTS)) {

                intent.putExtra(MyAnnotations.SCAN_TYPE, MyAnnotations.DOCUMENTS);
                new DeleteFileExecutor((Activity) popUpContext, popUpActivity, arrayList).execute();
            } else if (arrayList != null && fileType.equals(MyAnnotations.ALL_SCAN)) {

                intent.putExtra(MyAnnotations.SCAN_TYPE, MyAnnotations.ALL_SCAN);
                new DeleteFileExecutor((Activity) popUpContext, popUpActivity, arrayList).execute();
            }
            popUpContext.startActivity(intent);
            popUpActivity.finish();
        });

        textView_cancel.setOnClickListener(view -> dialog.dismiss());

    }

}
