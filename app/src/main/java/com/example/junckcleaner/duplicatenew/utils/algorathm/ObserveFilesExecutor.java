package com.example.junckcleaner.duplicatenew.utils.algorathm;


import static com.example.junckcleaner.duplicatenew.models.GlobalMethods.convertCRC64;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.format.Formatter;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.example.junckcleaner.annotations.MyAnnotations;
import com.example.junckcleaner.duplicatenew.models.FileDetails;
import com.example.junckcleaner.duplicatenew.utils.Constants;
import com.example.junckcleaner.duplicatenew.utils.DuplicatePreferences;
import com.example.junckcleaner.interfaces.DuplicateScanningListener;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ObserveFilesExecutor {
    int count = 1;

    Context readingAllFilesContext;
    DuplicateScanningListener duplicateScanningListener;
    String scanType;
    public boolean stopped = false;

    public ObserveFilesExecutor(Context context, DuplicateScanningListener duplicateScanningListener, String scanType) {
        this.readingAllFilesContext = context;
        this.duplicateScanningListener = duplicateScanningListener;
        this.scanType = scanType;
        stopped = false;
    }

    protected void onPreExecute() {
        resetAllBeforeStartScan();
    }

    public void execute() {
        new Handler().post(() -> {
            onPreExecute();
            Executor executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler();
            executor.execute(() -> {
                doInBackground();
                handler.post(() -> {
                    if (!stopped) {
                        onPostExecute();
                    }
                });
            });

        });
    }

    public HashMap<String, ArrayList<FileDetails>> fileDetailMapStr;
    public List<ArrayList<FileDetails>> duplicateDetailMap;
    FileDetails fileDetails;


    public HashMap<String, ArrayList<FileDetails>> getAllMediaByContent(Activity activity) {
        ArrayList<FileDetails> arrayList1 = new ArrayList<>();
        ArrayList<FileDetails> arrayList = new ArrayList<>();
        List<String> list;
        FileDetails fileDetails = new FileDetails();
        int counter = 0;
        int total = 0;
        try {
            this.fileDetailMapStr = new HashMap<>();
            this.duplicateDetailMap = new ArrayList<>();
            Cursor query = null;
            switch (scanType) {
                case MyAnnotations.IMAGES:

                    query = activity.getContentResolver()
                            .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                    new String[]{"_data", "_display_name", "mime_type", "_size",
                                            "datetaken", "date_added"},
                                    null,
                                    null,
                                    null);
                    break;
                case MyAnnotations.VIDEOS:
                    query = activity.getContentResolver()
                            .query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                                    new String[]{"_data", "_display_name", "mime_type", "_size",
                                            "datetaken", "date_added"},
                                    null,
                                    null,
                                    null);
                    break;
                case MyAnnotations.AUDIOS:
                    query = activity.getContentResolver()
                            .query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                                    new String[]{"_data", "_display_name", "mime_type", "_size",
                                            "datetaken", "date_added"},
                                    null,
                                    null,
                                    null);
                    break;
                case MyAnnotations.ALL_SCAN:

                    String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                            + MediaStore.Files.FileColumns.MEDIA_TYPE_NONE;
                    query = activity.getContentResolver()
                            .query(MediaStore.Files.getContentUri("external"),
                                    new String[]{"_data", "_display_name", "mime_type", "_size",
                                            "datetaken", "date_added"},
                                    selection,
                                    null,
                                    null);

                    break;
                case MyAnnotations.DOCUMENTS:

                    String selectionMimeType = MediaStore.Files.FileColumns.MIME_TYPE + " IN(?,?,?)";
                    String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("pdf");
                    String mimeTypeDoc = MimeTypeMap.getSingleton().getMimeTypeFromExtension("doc");
                    String mimeTypetxt = MimeTypeMap.getSingleton().getMimeTypeFromExtension("txt");
                    String[] selectionArgsPdf = new String[]{mimeType, mimeTypeDoc, mimeTypetxt};
                    query = activity.getContentResolver()
                            .query(MediaStore.Files.getContentUri("external"),
                                    new String[]{"_data", "_display_name", "mime_type", "_size",
                                            "datetaken", "date_added"},
                                    selectionMimeType,
                                    selectionArgsPdf,
                                    null);

                    break;
            }

            if (query == null || !query.moveToFirst()) {
                query.close();
                new Handler(Looper.getMainLooper()).post(() -> {
                    //end
                    duplicateScanningListener.publishProgress(duplicateDetailMap);
                });
                return fileDetailMapStr;
            }
            total = query.getCount();
            do {
                counter++;
                int finalCounter = counter;
                int finalTotal = total;
                new Handler(Looper.getMainLooper()).post(() -> duplicateScanningListener.
                        publishProgress(new String[]{String.valueOf(finalCounter),
                                String.valueOf(finalTotal)}));
                @SuppressLint("Range") String string =
                        query.getString(query.getColumnIndex("_data"));
                if (DuplicatePreferences.isZeroBytes(readingAllFilesContext) ||
                        new File(string).length() != 0) {
                    if (new File(string).exists()) {
                        long length = new File(string).length();
                        String readableFileSize =
                                Formatter.formatFileSize(readingAllFilesContext, length);
                        this.fileDetails = new FileDetails();
                        this.fileDetails.setFileSize(length);
                        this.fileDetails.setFileName(new File(string).getName());
                        this.fileDetails.setFilePath(string);
                        this.fileDetails.setFileSizeStr(readableFileSize);
                        convertCRC64(new File(string).getName());

                        String md5CheckSum = Constants.getMd5Checksum(string, this.readingAllFilesContext);
                        Constants.uniqueMd5Value.put(md5CheckSum, "." + Constants.getExtension(string));
                        Constants.extensionHashSet.add("." + Constants.getExtension(string));
                        if (md5CheckSum == null)
                            continue;
                        if (this.fileDetailMapStr.containsKey(md5CheckSum)) {
                            arrayList1 = fileDetailMapStr.get(md5CheckSum);
                            list = new ArrayList<>();
                            for (FileDetails fileDet : arrayList1) {
                                list.add(fileDet.getFileName());
                            }
                            if (list.contains(new File(string).getName())) {
                                arrayList = fileDetailMapStr.get(md5CheckSum);
                                fileDetails = this.fileDetails;
                            } else {
                                continue;
                            }

                        } else {
                            ArrayList arrayList3 = new ArrayList();
                            arrayList3.add(this.fileDetails);
                            this.fileDetailMapStr.put(md5CheckSum, new ArrayList<>(arrayList3));
                        }
                        arrayList.add(fileDetails);

                        arrayList = new ArrayList<>();

//
                    }
                }

            } while (query.moveToNext());
            query.close();

            for (Entry<String, ArrayList<FileDetails>> entry : fileDetailMapStr.entrySet()) {
                if (entry.getValue().size() != 1) {
                    duplicateDetailMap.add(entry.getValue());
                }
            }
            new Handler(Looper.getMainLooper()).post(() ->
                    duplicateScanningListener.publishProgress(duplicateDetailMap));
            fileDetailMapStr.clear();
            return this.fileDetailMapStr;
        } catch (Exception e) {
            e.printStackTrace();
            new Handler(Looper.getMainLooper()).post(() ->
                    duplicateScanningListener.publishProgress(duplicateDetailMap));
            Log.e("test", "exception" + e.getMessage() + " " + e.getCause());
            return this.fileDetailMapStr;
        }
    }

    public HashMap<String, ArrayList<FileDetails>> AllFilesByContent(Activity activity) {
        ArrayList<FileDetails> arrayList1 = new ArrayList<>();
        ArrayList<FileDetails> arrayList = new ArrayList<>();
        ArrayList<String> list;
        FileDetails fileDetails = new FileDetails();
        int counter = 0;
        int total = 0;
        boolean doNotScanImages = false;
        boolean doNotScanVideo = false;
        boolean doNotScanAudio = false;
        boolean doNotScanDocuments = false;
        try {
            this.fileDetailMapStr = new HashMap<>();
            this.duplicateDetailMap = new ArrayList<>();
            Cursor queryImage = null;
            Cursor queryVideo = null;
            Cursor queryAudio = null;
            Cursor queryDocuments = null;


            queryImage = activity.getContentResolver()
                    .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            new String[]{"_data", "_display_name", "mime_type", "_size",
                                    "datetaken", "date_added"},
                            null,
                            null,
                            null);


            queryVideo = activity.getContentResolver()
                    .query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                            new String[]{"_data", "_display_name", "mime_type", "_size",
                                    "datetaken", "date_added"},
                            null,
                            null,
                            null);

            queryAudio = activity.getContentResolver()
                    .query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            new String[]{"_data", "_display_name", "mime_type", "_size",
                                    "datetaken", "date_added"},
                            null,
                            null,
                            null);


            String selectionMimeType = MediaStore.Files.FileColumns.MIME_TYPE + " IN(?,?,?)";
            String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("pdf");
            String mimeTypeDoc = MimeTypeMap.getSingleton().getMimeTypeFromExtension("doc");
            String mimeTypetxt = MimeTypeMap.getSingleton().getMimeTypeFromExtension("txt");
            String[] selectionArgsPdf = new String[]{mimeType, mimeTypeDoc, mimeTypetxt};
            queryDocuments = activity.getContentResolver()
                    .query(MediaStore.Files.getContentUri("external"),
                            new String[]{"_data", "_display_name", "mime_type", "_size",
                                    "datetaken", "date_added"},
                            selectionMimeType,
                            selectionArgsPdf,
                            null);

            if (queryImage == null || !queryImage.moveToFirst()) {
                doNotScanImages = true;
            } else {
                total += queryImage.getCount();
            }
            if (queryVideo == null || !queryVideo.moveToFirst()) {
                doNotScanVideo = true;
            } else {
                total += queryVideo.getCount();

            }
            if (queryAudio == null || !queryAudio.moveToFirst()) {
                doNotScanAudio = true;
            } else {
                total += queryAudio.getCount();
            }
            if (queryDocuments == null || !queryDocuments.moveToFirst()) {
                doNotScanDocuments = true;
            } else {
                total += queryDocuments.getCount();
            }

            if ((queryImage == null || !queryImage.moveToFirst()) &&
                    (queryVideo == null || !queryVideo.moveToFirst()) &&
                    (queryAudio == null || !queryAudio.moveToFirst()) &&
                    (queryDocuments == null || !queryDocuments.moveToFirst())) {
                queryImage.close();
                queryVideo.close();
                queryAudio.close();
                queryDocuments.close();
                new Handler(Looper.getMainLooper()).post(() -> {
                    //end
                    duplicateScanningListener.publishProgress(duplicateDetailMap);
                });
                return fileDetailMapStr;
            }

            if (!doNotScanImages) {
                do {
                    counter++;
                    int finalCounter = counter;
                    int finalTotal = total;
                    new Handler(Looper.getMainLooper()).post(() -> duplicateScanningListener.
                            publishProgress(new String[]{String.valueOf(finalCounter),
                                    String.valueOf(finalTotal)}));
                    @SuppressLint("Range") String string =
                            queryImage.getString(queryImage.getColumnIndex("_data"));
                    if (DuplicatePreferences.isZeroBytes(readingAllFilesContext) ||
                            new File(string).length() != 0) {
                        if (new File(string).exists()) {
                            long length = new File(string).length();
                            String readableFileSize =
                                    Formatter.formatFileSize(readingAllFilesContext, length);
                            this.fileDetails = new FileDetails();
                            this.fileDetails.setFileSize(length);
                            this.fileDetails.setFileName(new File(string).getName());
                            this.fileDetails.setFilePath(string);
                            this.fileDetails.setFileSizeStr(readableFileSize);
                            convertCRC64(new File(string).getName());
                            String md5CheckSum = Constants.getMd5Checksum(string, this.readingAllFilesContext);
                            Constants.uniqueMd5Value.put(md5CheckSum, "." + Constants.getExtension(string));
                            Constants.extensionHashSet.add("." + Constants.getExtension(string));
                            if (md5CheckSum == null)
                                continue;
                            if (this.fileDetailMapStr.containsKey(md5CheckSum)) {
                                arrayList1 = fileDetailMapStr.get(md5CheckSum);
                                list = new ArrayList<>();
                                for (FileDetails fileDet : arrayList1) {
                                    list.add(fileDet.getFileName());
                                }
                                if (list.contains(new File(string).getName())) {
                                    arrayList = fileDetailMapStr.get(md5CheckSum);
                                    fileDetails = this.fileDetails;
                                } else {
                                    continue;
                                }
                            } else {
                                ArrayList arrayList3 = new ArrayList();
                                arrayList3.add(this.fileDetails);
                                this.fileDetailMapStr.put(md5CheckSum, new ArrayList<>(arrayList3));
                            }
                            arrayList.add(fileDetails);
                            arrayList = new ArrayList<>();

//
                        }
                    }

                } while (queryImage.moveToNext());
                queryImage.close();
            }
            if (!doNotScanVideo) {
                do {
                    counter++;
                    int finalCounter = counter;
                    int finalTotal = total;
                    new Handler(Looper.getMainLooper()).post(() -> duplicateScanningListener.
                            publishProgress(new String[]{String.valueOf(finalCounter),
                                    String.valueOf(finalTotal)}));
                    @SuppressLint("Range") String string =
                            queryVideo.getString(queryVideo.getColumnIndex("_data"));
                    if (DuplicatePreferences.isZeroBytes(readingAllFilesContext) ||
                            new File(string).length() != 0) {
                        if (new File(string).exists()) {
                            long length = new File(string).length();
                            String readableFileSize =
                                    Formatter.formatFileSize(readingAllFilesContext, length);
                            this.fileDetails = new FileDetails();
                            this.fileDetails.setFileSize(length);
                            this.fileDetails.setFileName(new File(string).getName());
                            this.fileDetails.setFilePath(string);
                            this.fileDetails.setFileSizeStr(readableFileSize);
                            convertCRC64(new File(string).getName());
                            String md5CheckSum = Constants.getMd5Checksum(string, this.readingAllFilesContext);
                            Constants.uniqueMd5Value.put(md5CheckSum, "." + Constants.getExtension(string));
                            Constants.extensionHashSet.add("." + Constants.getExtension(string));
                            if (md5CheckSum == null)
                                continue;
                            if (this.fileDetailMapStr.containsKey(md5CheckSum)) {
                                arrayList1 = fileDetailMapStr.get(md5CheckSum);
                                list = new ArrayList<>();
                                for (FileDetails fileDet : arrayList1) {
                                    list.add(fileDet.getFileName());
                                }
                                if (list.contains(new File(string).getName())) {
                                    arrayList = fileDetailMapStr.get(md5CheckSum);
                                    fileDetails = this.fileDetails;
                                } else {
                                    continue;
                                }
                            } else {
                                ArrayList arrayList3 = new ArrayList();
                                arrayList3.add(this.fileDetails);
                                this.fileDetailMapStr.put(md5CheckSum,
                                        new ArrayList<>(arrayList3));
                            }
                            arrayList.add(fileDetails);
                            arrayList = new ArrayList<>();

//
                        }
                    }

                } while (queryVideo.moveToNext());
                queryVideo.close();
            }
            if (!doNotScanAudio) {
                do {
                    counter++;
                    int finalCounter = counter;
                    int finalTotal = total;
                    new Handler(Looper.getMainLooper()).post(() -> duplicateScanningListener.
                            publishProgress(new String[]{String.valueOf(finalCounter),
                                    String.valueOf(finalTotal)}));
                    @SuppressLint("Range") String string =
                            queryAudio.getString(queryAudio.getColumnIndex("_data"));
                    if (DuplicatePreferences.isZeroBytes(readingAllFilesContext) ||
                            new File(string).length() != 0) {
                        if (new File(string).exists()) {
                            long length = new File(string).length();
                            String readableFileSize =
                                    Formatter.formatFileSize(readingAllFilesContext, length);
                            this.fileDetails = new FileDetails();
                            this.fileDetails.setFileSize(length);
                            this.fileDetails.setFileName(new File(string).getName());
                            this.fileDetails.setFilePath(string);
                            this.fileDetails.setFileSizeStr(readableFileSize);
                            convertCRC64(new File(string).getName());
                            String md5CheckSum = Constants.getMd5Checksum(string, this.readingAllFilesContext);
                            Constants.uniqueMd5Value.put(md5CheckSum, "." + Constants.getExtension(string));
                            Constants.extensionHashSet.add("." + Constants.getExtension(string));
                            if (md5CheckSum == null)
                                continue;
                            if (this.fileDetailMapStr.containsKey(md5CheckSum)) {
                                arrayList1 = fileDetailMapStr.get(md5CheckSum);
                                list = new ArrayList<>();
                                for (FileDetails fileDet : arrayList1) {
                                    list.add(fileDet.getFileName());
                                }
                                if (list.contains(new File(string).getName())) {
                                    arrayList = fileDetailMapStr.get(md5CheckSum);
                                    fileDetails = this.fileDetails;
                                } else {
                                    continue;
                                }
                            } else {
                                ArrayList arrayList3 = new ArrayList();
                                arrayList3.add(this.fileDetails);
                                this.fileDetailMapStr.put(md5CheckSum,
                                        new ArrayList<>(arrayList3));
                            }
                            arrayList.add(fileDetails);
                            arrayList = new ArrayList<>();

//
                        }
                    }

                } while (queryAudio.moveToNext());
                queryAudio.close();
            }
            if (!doNotScanDocuments) {
                do {
                    counter++;
                    int finalCounter = counter;
                    int finalTotal = total;
                    new Handler(Looper.getMainLooper()).post(() -> duplicateScanningListener.
                            publishProgress(new String[]{String.valueOf(finalCounter),
                                    String.valueOf(finalTotal)}));
                    @SuppressLint("Range") String string =
                            queryDocuments.getString(queryDocuments.getColumnIndex("_data"));
                    if (DuplicatePreferences.isZeroBytes(readingAllFilesContext) ||
                            new File(string).length() != 0) {
                        if (new File(string).exists()) {
                            long length = new File(string).length();
                            String readableFileSize =
                                    Formatter.formatFileSize(readingAllFilesContext, length);
                            this.fileDetails = new FileDetails();
                            this.fileDetails.setFileSize(length);
                            this.fileDetails.setFileName(new File(string).getName());
                            this.fileDetails.setFilePath(string);
                            this.fileDetails.setFileSizeStr(readableFileSize);
                            convertCRC64(new File(string).getName());
                            String md5CheckSum = Constants.getMd5Checksum(string, this.readingAllFilesContext);
                            Constants.uniqueMd5Value.put(md5CheckSum, "." + Constants.getExtension(string));
                            Constants.extensionHashSet.add("." + Constants.getExtension(string));
                            if (md5CheckSum == null)
                                continue;
                            if (this.fileDetailMapStr.containsKey(md5CheckSum)) {
                                arrayList1 = fileDetailMapStr.get(md5CheckSum);
                                list = new ArrayList<>();
                                for (FileDetails fileDet : arrayList1) {
                                    list.add(fileDet.getFileName());
                                }
                                if (list.contains(new File(string).getName())) {
                                    arrayList = fileDetailMapStr.get(md5CheckSum);
                                    fileDetails = this.fileDetails;
                                } else {
                                    continue;
                                }
                            } else {
                                ArrayList arrayList3 = new ArrayList();
                                arrayList3.add(this.fileDetails);
                                this.fileDetailMapStr.put(md5CheckSum,
                                        new ArrayList<>(arrayList3));
                            }
                            arrayList.add(fileDetails);
                            arrayList = new ArrayList<>();

//
                        }
                    }

                } while (queryDocuments.moveToNext());
                queryDocuments.close();
            }

            for (Entry<String, ArrayList<FileDetails>> entry : fileDetailMapStr.entrySet()) {
                if (entry.getValue().size() != 1) {
                    duplicateDetailMap.add(entry.getValue());
                }
            }
            new Handler(Looper.getMainLooper()).post(() ->
                    duplicateScanningListener.publishProgress(duplicateDetailMap));
            fileDetailMapStr.clear();
            return this.fileDetailMapStr;
        } catch (Exception e) {
            e.printStackTrace();
            new Handler(Looper.getMainLooper()).post(() ->
                    duplicateScanningListener.publishProgress(duplicateDetailMap));
            Log.e("test", "exception" + e.getMessage() + " " + e.getCause());
            return this.fileDetailMapStr;
        }
    }

    private void resetAllBeforeStartScan() {
        DuplicatePreferences.setScanStop(this.readingAllFilesContext, false);
        DuplicatePreferences.setSortBy(this.readingAllFilesContext, Constants.DATE_DOWN);
        DuplicatePreferences.setInitiateRescanAndEnterImagePageFirstTimeAfterScan(this.readingAllFilesContext, true);
        DuplicatePreferences.setInitiateRescanAndEnterVideoPageFirstTimeAfterScan(this.readingAllFilesContext, true);
        DuplicatePreferences.setInitiateRescanAndEnterAudioPageFirstTimeAfterScan(this.readingAllFilesContext, true);
        DuplicatePreferences.setInitiateRescanAndEnterDocumentPageFirstTimeAfterScan(this.readingAllFilesContext, true);
        DuplicatePreferences.setInitiateRescanAndEnterOtherPageFirstTimeAfterScan(this.readingAllFilesContext, true);
        Constants.resetOneTimePopUp();


        Constants.uniqueMd5Value.clear();
        Constants.extensionHashSet.clear();
        Constants.audiosExtensionHashSet.clear();
        Constants.documentsExtensionHashSet.clear();
        Constants.photosExtensionHashSet.clear();
        Constants.videosExtensionHashSet.clear();
    }


    protected void doInBackground() {
        count = 0;
        if (scanType.equals(MyAnnotations.IMAGES) ||
                scanType.equals(MyAnnotations.VIDEOS) ||
                scanType.equals(MyAnnotations.AUDIOS) ||
                scanType.equals(MyAnnotations.DOCUMENTS)) {

            getAllMediaByContent((Activity) readingAllFilesContext);
        } else if (scanType.equals(MyAnnotations.ALL_SCAN)) {
            AllFilesByContent((Activity) readingAllFilesContext);


        }
    }

    protected void onPostExecute() {
        this.duplicateScanningListener.checkScanning();

    }


    public void stopAsyncTask() {
        stopped = true;
        DuplicatePreferences.setScanStop(this.readingAllFilesContext, true);

    }

    public static String getSD_CardPath_M(Context context) {
        String[] externalStoragePath = getExternalStorageDirectories(context);
        if (externalStoragePath.length == 0) {
            return null;
        }
        return externalStoragePath[0] + "/";
    }

    public static String[] getExternalStorageDirectories(Context context) {
        int i;
        List<String> results = new ArrayList();
        try {
            for (File file : context.getExternalFilesDirs(null)) {
                boolean addPath;
                String path = file.getPath().split("/Android")[0];
                addPath = Environment.isExternalStorageRemovable(file);
                if (addPath) {
                    results.add(path);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        int i2;
        if (Build.VERSION.SDK_INT >= 23) {
            i = 0;
            while (i < results.size()) {
                if (!results.get(i).toLowerCase().matches(".*[0-9a-f]{4}[-][0-9a-f]{4}")) {
                    Log.d("ReadingAllFiles", ((String) results.get(i)) + " might not be extSDcard");
                    i2 = i - 1;
                    results.remove(i);
                    i = i2;
                }
                i++;
            }
        } else {
            i = 0;
            while (i < results.size()) {
                if (!(results.get(i).toLowerCase().contains("ext") ||
                        results.get(i).toLowerCase().contains("sdcard"))) {
                    Log.d("ReadingAllFiles", results.get(i) + " might not be extSDcard");
                    i2 = i - 1;
                    results.remove(i);
                    i = i2;
                }
                i++;
            }
        }
        String[] storageDirectories = new String[results.size()];
        for (i = 0; i < results.size(); i++) {
            storageDirectories[i] = results.get(i);
        }
        return storageDirectories;
    }

}