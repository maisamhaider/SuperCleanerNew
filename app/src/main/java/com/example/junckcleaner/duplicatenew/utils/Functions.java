package com.example.junckcleaner.duplicatenew.utils;

import android.content.Context;
import android.widget.Toast;

import com.example.junckcleaner.duplicatenew.utils.algorathm.ObserveFilesExecutor;

import java.io.File;

public class Functions {

    public static void showToastMsg(Context context, String str) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }


    public static String getSDCardPath(Context context) {
        File file1 = new File(Constants.SDCARD1);
        File file2 = new File(Constants.SDCARD2);
        File file3 = new File(Constants.SDCARD3);
        File file4 = new File(Constants.SDCARD4);
        File file5 = new File(Constants.SDCARD5);
        File file6 = new File(Constants.SDCARD6);

        if (file1.exists()) {
            return String.valueOf(file1);
        }
        if (file2.exists()) {
            return String.valueOf(file2);
        }
        if (file3.exists()) {
            return String.valueOf(file3);
        }
        if (file4.exists()) {
            return String.valueOf(file4);
        }
        if (file5.exists()) {
            return String.valueOf(file5);
        }
        if (file6.exists()) {
            return String.valueOf(file6);
        }

        return ObserveFilesExecutor.getSD_CardPath_M(context);
    }
}
