package com.example.junckcleaner.duplicatenew.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class DuplicatePreferences {
    private static final String DUPLICATE_REMOVER_PREF = "dFRPref";
     private static final String SORT_BY = "sortBy";


    public static SharedPreferences getDuplicateFileRemoverPref(Context context) {
        return context.getSharedPreferences(DUPLICATE_REMOVER_PREF, 0);
    }


    public static void setSortBy(Context context, String str) {
        getDuplicateFileRemoverPref(context).edit().putString(SORT_BY, str).apply();
    }


    public static void setScanStop(Context context, boolean isStopScanning) {
        getDuplicateFileRemoverPref(context).edit().putBoolean("STOP_SCANNING", isStopScanning).apply();
    }

    public static boolean isScanningStopped(Context context) {
        return getDuplicateFileRemoverPref(context).getBoolean("STOP_SCANNING", false);
    }




    public static void setDeletedSize(Context context, long size) {
        getDuplicateFileRemoverPref(context).edit().putLong("deleteSize", size).apply();
    }


    public static boolean isZeroBytes(Context context) {
        return getDuplicateFileRemoverPref(context).getBoolean("ZeroBytes", false);
    }


    public static void setStopScanForNotification(Context context, boolean isStopScanning) {
        getDuplicateFileRemoverPref(context).edit().putBoolean("STOP_SCANNING_ForNotification", isStopScanning).apply();
    }


    public static void setInitiateRescanAndEnterImagePageFirstTimeAfterScan(Context context, boolean value) {
        getDuplicateFileRemoverPref(context).edit().putBoolean("IMAGE_RESCAN_ENTER_PAGE_FIRST_TIME", value).apply();
    }

    public static void setInitiateRescanAndEnterVideoPageFirstTimeAfterScan(Context context, boolean value) {
        getDuplicateFileRemoverPref(context).edit().putBoolean("VIDEO_RESCAN_ENTER_PAGE_FIRST_TIME", value).apply();
    }


    public static void setInitiateRescanAndEnterAudioPageFirstTimeAfterScan(Context context, boolean value) {
        getDuplicateFileRemoverPref(context).edit().putBoolean("AUDIO_RESCAN_ENTER_PAGE_FIRST_TIME", value).apply();
    }


    public static void setInitiateRescanAndEnterDocumentPageFirstTimeAfterScan(Context context, boolean value) {
        getDuplicateFileRemoverPref(context).edit().putBoolean("DOCUMENT_RESCAN_ENTER_PAGE_FIRST_TIME", value).apply();
    }


    public static void setInitiateRescanAndEnterOtherPageFirstTimeAfterScan(Context context, boolean value) {
        getDuplicateFileRemoverPref(context).edit().putBoolean("OTHER_RESCAN_ENTER_PAGE_FIRST_TIME", value).apply();
    }


    public static void setNavigateFromHome(Context context, boolean value) {
        getDuplicateFileRemoverPref(context).edit().putBoolean("NAVIGATE_FROM_HOME", value).apply();
    }


    public static String getStorageAccessFrameWorkURIPermission(Context context) {
        return getDuplicateFileRemoverPref(context).getString("STORAGE_ACCESS_FRAMEWORK_PERMISSION_PATH", null);
    }

}
