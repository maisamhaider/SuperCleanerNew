package com.example.junckcleaner.duplicatenew.utils;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore.Files;
import android.util.Log;

import androidx.documentfile.provider.DocumentFile;

import com.example.junckcleaner.duplicatenew.models.FileDetails;
import com.example.junckcleaner.duplicatenew.utils.algorathm.MD5;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Constants {

    public static final String DATE_DOWN = "dateDown";

    public static boolean ONE_TIME_DUPLICATE_INFORMATION_POPUP = false;
    public static boolean ONE_TIME_POPUP_AUDIOS = false;
    public static boolean ONE_TIME_POPUP_DOCUMENTS = false;
    public static boolean ONE_TIME_POPUP_PHOTOS = false;
    public static boolean ONE_TIME_POPUP_VIDEOS = false;

    public static final String SDCARD1 = "/ext_card/";
    public static final String SDCARD2 = "/mnt/sdcard/external_sd/";
    public static final String SDCARD3 = "/storage/extSdCard/";
    public static final String SDCARD4 = "/mnt/extSdCard/";
    public static final String SDCARD5 = "/mnt/external_sd/";
    public static final String SDCARD6 = "/storage/sdcard1/";

    private static final String TAG = "GlobalVarsAndFunctions";


    public static final ArrayList<FileDetails> fileToBeDeleted = new ArrayList();
    public static long fileSize;
    public static final Set<String> audiosExtensionHashSet = new HashSet();
    public static final Set<String> documentsExtensionHashSet = new HashSet();
    public static final Set<String> extensionHashSet = new HashSet();
    public static final HashMap<String, String> uniqueMd5Value = new HashMap();
    public static final Set<String> photosExtensionHashSet = new HashSet();
    public static final Set<String> videosExtensionHashSet = new HashSet();


    public static String getExtension(String path) {
        String[] fileNameArray = path.split("\\.");
        return fileNameArray[fileNameArray.length - 1].toLowerCase();
    }

    public static String getMd5Checksum(String path, Context context) {
        return new MD5().fileToMD5(path, context);
    }


    public static void deleteFile(Activity deleteActivity, Context context, String path) {
        File file = new File(path);
        List<Boolean> checkSDCardFile = new ArrayList();
        if (Functions.getSDCardPath(context) != null) {
            try {
                DocumentFile documentFile =
                        DocumentFile.fromTreeUri(deleteActivity,
                                Uri.parse(DuplicatePreferences.getStorageAccessFrameWorkURIPermission(context)));
                String[] parts = file.getPath().split("\\/");
                Log.i(TAG, "deleteFile:parts " + parts);
                for (int j = 0; j < parts.length; j++) {
                    Log.i(TAG, "deleteFile:j= " + j);
                    checkSDCardFile.add(parts[j].equals(documentFile.getName()));
                    if (parts[j].equals(documentFile.getName())) {
                        for (int i = 3; i < parts.length; i++) {
                            if (documentFile != null) {
                                Log.i(TAG, "deleteFile:i= " + i);
                                documentFile = documentFile.findFile(parts[i]);
                            }
                        }
                        if (documentFile != null) {
                            documentFile.delete();
                        }
                    }
                }
                if (!checkSDCardFile.contains(Boolean.TRUE)) {
                    normalFunctionForDeleteFile(context, file);
                    return;
                }
                return;
            } catch (Exception e) {
                return;
            }
        }
        normalFunctionForDeleteFile(context, file);
    }


    private static void normalFunctionForDeleteFile(Context context, File file) {
        File f = new File(file.getAbsolutePath());
        if (!f.exists()) {
            return;
        }
        deleteFileFromMediaStore(context.getContentResolver(), f);
    }

    private static void deleteFileFromMediaStore(ContentResolver contentResolver, File file) {
        String canonicalPath;
        try {
            canonicalPath = file.getCanonicalPath();
        } catch (IOException e) {
            canonicalPath = file.getAbsolutePath();
        }
        try {
            Uri uri = Files.getContentUri("external");
            if (contentResolver.delete(uri, "_data=?", new String[]{canonicalPath}) == 0) {
                if (!file.getAbsolutePath().equals(canonicalPath)) {
                    contentResolver.delete(uri, "_data=?", new String[]{file.getAbsolutePath()});
                }
            }
        } catch (RuntimeException e2) {
            e2.printStackTrace();
        }
    }

    public static void resetOneTimePopUp() {
        ONE_TIME_DUPLICATE_INFORMATION_POPUP = false;
        ONE_TIME_POPUP_PHOTOS = false;
        ONE_TIME_POPUP_VIDEOS = false;
        ONE_TIME_POPUP_AUDIOS = false;
        ONE_TIME_POPUP_DOCUMENTS = false;
    }


}
