package com.example.junckcleaner.utils;

import android.annotation.SuppressLint;
import android.app.usage.StorageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.junckcleaner.annotations.MyAnnotations;
import com.example.junckcleaner.models.FileModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;


public class Utils {

    private static final String SYSTEM_PACKAGE_NAME = "android";
    private final PackageManager mPackageManager;
    Context context;
    String destinationPath;
    File folder;
    float docSize = 0;
    float audioSize = 0;
    float size = 0;

    public Utils(Context context) {
        this.context = context;
        mPackageManager = (PackageManager) context.getPackageManager();

    }

    // get saved status data
    public List<FileModel> getSavedStatusFiles(File parentDir) {
        File fold = new File(parentDir.getPath());
        List<FileModel> docList = new ArrayList<>();
        File[] mlist = fold.listFiles();

        {
            if (mlist != null)
                for (File f : mlist) {
                    if (f.isDirectory()) {
                        docList.addAll(getSavedStatusFiles(new File(f.getAbsolutePath())));
                    } else {
                        FileModel data = new FileModel();
                        if (f.getAbsolutePath().endsWith(".jpeg") ||
                                f.getAbsolutePath().endsWith(".jpg") ||
                                f.getAbsolutePath().endsWith(".png") ||
                                f.getName().endsWith("mp4")) {
                            if (f.getName().contains("p_repair")) {
                                data.setName(f.getName());
                                data.setPath(f.getPath());
                                data.setSize(f.length());
                                //                doc.setType(FileTypes.DocumentType);
                                if (f.length() > 0)
                                    docList.add(data);
                            }
                        }
                    }
                }
        }

        return docList;
    }

    // get all data
    public List<FileModel> getListFiles(File parentDir) {
        File fold = new File(parentDir.getPath());
        List<FileModel> docList = new ArrayList<>();
        File[] mlist = fold.listFiles();
        File[] mFilelist = fold.listFiles();

        {
            if (mlist != null)
                for (File f : mlist) {
                    if (f.isDirectory()) {
                        docList.addAll(getListFiles(new File(f.getAbsolutePath())));
                    } else {
                        FileModel data = new FileModel();
                        data.setName(f.getName());
                        data.setPath(f.getPath());
                        data.setSize(f.length());
                        //                doc.setType(FileTypes.DocumentType);
                        if (f.length() > 0)
                            docList.add(data);
                    }
                }
        }

        return docList;
    }


    //get specific data
    public List<FileModel> getListFiles(File parentDir, String forWhat) {
        File fold = new File(parentDir.getPath());
        List<FileModel> docList = new ArrayList<>();
        File[] mlist = fold.listFiles();


        if (forWhat.matches("images")) {
            if (mlist != null) {
                for (File f : mlist) {
                    if (f.isDirectory()) {
                        docList.addAll(getListFiles(new File(f.getAbsolutePath(), "images")));
                    } else {

                        FileModel data = new FileModel();
                        if (f.getAbsolutePath().endsWith(".jpeg") || f.getAbsolutePath().endsWith(".jpg") || f.getAbsolutePath().endsWith(".png")) {
                            data.setName(f.getName());
                            data.setPath(f.getPath());
                            data.setSize(f.length());
                            if (f.length() > 0)
                                docList.add(data);
                        }


                    }
                }
            }

        } else {
            if (mlist != null) {
                for (File f : mlist) {
                    if (f.isDirectory()) {
                        docList.addAll(getListFiles(new File(f.getAbsolutePath(), "videos")));
                    } else {
                        FileModel data = new FileModel();
                        if (f.getAbsolutePath().endsWith("mp4")) {
                            data.setName(f.getName());
                            data.setPath(f.getPath());
                            data.setSize(f.length());

                            if (f.length() > 0)
                                docList.add(data);
                        }
                    }
                }
            }
        }
        return docList;
    }

    public float getTotalStorage() {
        long totalStorage;
        long totalStorage1;

        String p1;
        String p2 = Environment.getRootDirectory().getPath();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            p1 = Environment.getStorageDirectory().getAbsolutePath();

        } else {
            p1 = Environment.getExternalStorageDirectory().getAbsolutePath();
        }

        StatFs statFs = new StatFs(p1);
        StatFs statFs1 = new StatFs(p2);
        totalStorage = (statFs.getBlockSizeLong() * statFs.getBlockCountLong());
        totalStorage1 = (statFs1.getBlockSizeLong() * statFs1.getBlockCountLong());


        return totalStorage + totalStorage1;
    }

    public float getTotalStorage(String file) {
        long totalStorage;

        StatFs statFs = new StatFs(file);
        totalStorage = (statFs.getBlockSizeLong() * statFs.getBlockCountLong());

        return totalStorage;
    }


    public long getAvailableStorage(String file) {
        long megAvailable;

        StatFs stat = new StatFs(file);
        long bytesAvailable;

        bytesAvailable = stat.getAvailableBlocksLong() * stat.getBlockSizeLong();
        megAvailable = bytesAvailable;
        return megAvailable;
    }

    public long getAvailableStorage() {
        long megAvailable;
        String path1;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            path1 = Environment.getStorageDirectory().getAbsolutePath();

        } else {
            path1 = Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        StatFs stat = new StatFs(path1);

        long bytesAvailable;

        bytesAvailable = stat.getAvailableBlocksLong() * stat.getBlockSizeLong();
        megAvailable = bytesAvailable;
        return megAvailable;
    }


//


//    public List<String> getActiveApps() {
// ApplicationInfo applicationInfo;
//
//        PackageManager packageManager = context.getPackageManager();
//
//        try {
//            applicationInfo = packageManager.getApplicationInfo(apkPackageName, 0);
//        PackageManager pm = context.getPackageManager();
//        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
//        List<String> list = new ArrayList<>();
//
//        for (ApplicationInfo packageInfo : packages) {
//
//            if (isSTOPPED(packageInfo)) {
//                if (!list.contains(packageInfo.packageName)) {
//                    list.add(packageInfo.packageName);
//                }
//            }
//        }
//        return list;
//    }

    public List<String> getSystemActiveApps() {

        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        List<String> list = new ArrayList<>();

        for (ApplicationInfo packageInfo : packages) {
            if (isSTOPPED(packageInfo) && isSYSTEM(packageInfo)) {
                if (!list.contains(packageInfo.packageName)) {
                    list.add(packageInfo.packageName);
                }
            }
        }
        return list;
    }


//    private boolean isSTOPPED(ApplicationInfo pkgInfo) {
//
//        return ((pkgInfo.flags & ApplicationInfo.FLAG_STOPPED) == 0);
//    }

    private boolean isSYSTEM(ApplicationInfo pkgInfo) {

        return ((pkgInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }

    public boolean isSystemPackage(ResolveInfo resolveInfo) {

        return ((resolveInfo.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }


    public String GetAppName(String ApkPackageName) {

        String Name = "";

        ApplicationInfo applicationInfo;

        PackageManager packageManager = context.getPackageManager();

        try {

            applicationInfo = packageManager.getApplicationInfo(ApkPackageName, 0);

            if (applicationInfo != null) {


                Name = (String) packageManager.getApplicationLabel(applicationInfo);

            }

        } catch (PackageManager.NameNotFoundException e) {

            e.printStackTrace();
        }
        return Name;
    }


    public float getPercentage(float totalData, float usedData) {

        return (usedData * 100 / totalData);
    }


    public List<String> GetAllInstalledApkInfo() {

        List<String> ApkPackageName = new ArrayList<>();

        Intent intent = new Intent(Intent.ACTION_MAIN, null);

        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

        List<ResolveInfo> resolveInfoList = context.getPackageManager().queryIntentActivities(intent, 0);

        for (ResolveInfo resolveInfo : resolveInfoList) {

            ActivityInfo activityInfo = resolveInfo.activityInfo;

            if (isSystemPackage(resolveInfo) || !isSystemPackage(resolveInfo)) {
                if (!ApkPackageName.contains(activityInfo.applicationInfo.packageName)) {
                    ApkPackageName.add(activityInfo.applicationInfo.packageName);
                }
            }
        }

        return ApkPackageName;

    }

    public List<String> getSysActiveApps(boolean ram) {

        PackageManager pm = context.getPackageManager();
        @SuppressLint("QueryPermissionsNeeded")
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        List<String> list = new ArrayList<>();
        if (ram) {
            for (ApplicationInfo packageInfo : packages) {
                if (!isStopped(packageInfo)) {
                    if (!list.contains(packageInfo.packageName)) {
                        list.add(packageInfo.packageName);
                    }
                }
            }
        } else {
            for (ApplicationInfo packageInfo : packages) {
                if (isStopped(packageInfo) && isSys(packageInfo)) {
                    if (!list.contains(packageInfo.packageName)) {
                        list.add(packageInfo.packageName);
                    }
                }
            }
        }

        return list;
    }


    private boolean isStopped(ApplicationInfo appInformation) {

        return ((appInformation.flags & ApplicationInfo.FLAG_STOPPED) == 0);
    }

    private boolean isSys(ApplicationInfo appInformation) {

        return ((appInformation.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }

    public LiveData<List<String>> getSysOrInstalledAppsList(boolean systemApp, boolean isAll, boolean takeThisApp) {

        List<String> AppPackageName = new ArrayList<>();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

        List<ResolveInfo> resolveInfoList = context.getPackageManager().queryIntentActivities(intent, 0);

        for (ResolveInfo resolveInfo : resolveInfoList) {

            ActivityInfo activityInfo = resolveInfo.activityInfo;

            if (isAll) {

                if (!AppPackageName.contains(activityInfo.applicationInfo.packageName)) {
                    if (takeThisApp) {
                        AppPackageName.add(activityInfo.applicationInfo.packageName);
                    } else {
                        if (!activityInfo.applicationInfo.packageName.equals(context.getPackageName())) {
                            AppPackageName.add(activityInfo.applicationInfo.packageName);
                        }
                    }

                }

            } else {
                if (systemApp) {
                    if (isSysPackage(resolveInfo)) {
                        if (!AppPackageName.contains(activityInfo.applicationInfo.packageName)) {
                            if (takeThisApp) {
                                AppPackageName.add(activityInfo.applicationInfo.packageName);
                            } else {
                                if (!activityInfo.applicationInfo.packageName.equals(context.getPackageName())) {
                                    AppPackageName.add(activityInfo.applicationInfo.packageName);
                                }
                            }
                        }
                    }
                } else {
                    if (!isSysPackage(resolveInfo)) {
                        if (!AppPackageName.contains(activityInfo.applicationInfo.packageName)) {
                            if (takeThisApp) {
                                AppPackageName.add(activityInfo.applicationInfo.packageName);
                            } else {
                                if (!activityInfo.applicationInfo.packageName.equals(context.getPackageName())) {
                                    AppPackageName.add(activityInfo.applicationInfo.packageName);
                                }
                            }
                        }
                    }
                }
            }
        }

        MutableLiveData<List<String>> liveData = new MutableLiveData<>();

        liveData.setValue(AppPackageName);

        return liveData;
    }


    public LiveData<List<String>> activeApps() {

        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        List<String> list = new ArrayList<>();

        for (ApplicationInfo packageInfo : packages) {

            if (/*isSysPackage(packageInfo) || */!isSysPackage(pm, packageInfo)) {
                if (!list.contains(packageInfo.packageName)) {
                    if (!packageInfo.packageName.equals(context.getPackageName())) {
                        list.add(packageInfo.packageName);
                    }

                }
            }
        }

        MutableLiveData<List<String>> liveData = new MutableLiveData<>();

        liveData.setValue(list);

        return liveData;
    }

    private boolean isSTOPPED(ApplicationInfo pkgInfo) {

        return ((pkgInfo.flags & ApplicationInfo.FLAG_STOPPED) == 0);
    }


    public boolean isSysPackage(ResolveInfo resolveInfo) {

        return ((resolveInfo.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }

    public boolean isSysPackage(PackageManager pm, ApplicationInfo info) {

        return (info.flags & ApplicationInfo.FLAG_SYSTEM) == 0;
        //This app is a non-system app

    }

    public long appTimes(String apkPackageName, boolean update) {
        long timeStamp = 0;
        PackageManager pm = context.getPackageManager();

        try {
            PackageInfo info = pm.getPackageInfo(apkPackageName, 0);
            if (update) {
                timeStamp = info.lastUpdateTime;
            } else {
                timeStamp = info.firstInstallTime;
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        return timeStamp;
    }


    // return any time on condition..
    public <T> T appInfo(String apkPackageName, String whatThing) {

        T thingType = null;

        ApplicationInfo applicationInfo;

        PackageManager packageManager = context.getPackageManager();

        try {
            applicationInfo = packageManager.getApplicationInfo(apkPackageName, 0);

            if (applicationInfo != null) {

                if (whatThing.matches(MyAnnotations.APP_ICON)) {
                    thingType = (T) packageManager.getApplicationIcon(applicationInfo);
                } else if (whatThing.matches(MyAnnotations.APP_NAME)) {
                    thingType = (T) packageManager.getApplicationLabel(applicationInfo);
                } else if (whatThing.matches(MyAnnotations.APP_VERSION)) {
                    PackageManager pm = context.getPackageManager();
                    PackageInfo packageInfo = pm.getPackageInfo(apkPackageName, 0);
                    thingType = (T) packageInfo.versionName;
                }
            }

        } catch (PackageManager.NameNotFoundException e) {

            e.printStackTrace();
        }
        return thingType;
    }


    public long appSize(String packageName) throws PackageManager.NameNotFoundException {
        return new File(context.getPackageManager().getApplicationInfo(packageName, 0)
                .publicSourceDir).length();
    }


    public boolean isSystemApp(String packageName) {
        try {
            // Get packageinfo for target application
            @SuppressLint("PackageManagerGetSignatures")
            PackageInfo targetPkgInfo = mPackageManager.getPackageInfo(
                    packageName, PackageManager.GET_SIGNATURES);
            // Get packageinfo for system package
            PackageInfo sys = mPackageManager.getPackageInfo(
                    SYSTEM_PACKAGE_NAME, PackageManager.GET_SIGNATURES);
            // Match both packageinfo for there signatures
            return (targetPkgInfo != null && targetPkgInfo.signatures != null && sys.signatures[0]
                    .equals(targetPkgInfo.signatures[0]));
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public boolean isAppLoaded(String packageName) {
        if (packageName == null) {
            throw new IllegalArgumentException("Package name can not be null");
        }
        try {
            ApplicationInfo ai = mPackageManager.getApplicationInfo(
                    packageName, 0);
            // First check if it is preloaded.
            // If yes then check if it is System app or not.
            if (ai != null
                    && (ai.flags & (ApplicationInfo.FLAG_SYSTEM
                    | ApplicationInfo.FLAG_UPDATED_SYSTEM_APP)) != 0) {
                // Check if signature matches
                return isSystemApp(packageName);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public long getAppTime(String apkPackageName, String whatKind) {
        long timeStamp = 0;
        PackageManager pm = context.getPackageManager();

        try {
            if (MyAnnotations.INSTALLATION.matches(whatKind)) {
                PackageInfo info = pm.getPackageInfo(apkPackageName, 0);
                Field field = PackageInfo.class.getField("firstInstallTime");
                timeStamp = field.getLong(info);
            } else {
                PackageInfo info = pm.getPackageInfo(apkPackageName, 0);
                Field field = PackageInfo.class.getField("lastUpdateTime");
                timeStamp = field.getLong(info);
            }

        } catch (PackageManager.NameNotFoundException | IllegalAccessException e1) {
            e1.printStackTrace();
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        return timeStamp;
    }


    // return left string from first ,
    public static String getRightStringToThePoint(String s, String pointString) {
        String d = s;
        String ssDate = d.substring(d.lastIndexOf(pointString));
        return ssDate;
    }

    public void moveFile(String sourcePath, String finalDir) {

        String lastName = getRightStringToThePoint(sourcePath, "/");

        for (String path : getExternalMounts()) {
            destinationPath = path + "/" + finalDir + lastName;
            folder = new File(path + "/" + finalDir);
        }


        if (!folder.exists()) {
            folder.mkdirs();
            //                Toast.makeText(context, "created", Toast.LENGTH_SHORT).show();
            //                Toast.makeText(context, "not created", Toast.LENGTH_SHORT).show();

        }
        File sourceFile = new File(sourcePath);

        File destinationFile = new File(destinationPath);

        sourceFile.renameTo(destinationFile);

    }

    public ArrayList<String> getExternalMounts() {
        final ArrayList<String> out = new ArrayList<>();
        String reg = "(?i).*vold.*(vfat|ntfs|exfat|fat32|ext3|ext4).*rw.*";
        String s = "";
        try {
            final Process process = new ProcessBuilder().command("mount")
                    .redirectErrorStream(true).start();
            process.waitFor();
            final InputStream is = process.getInputStream();
            final byte[] buffer = new byte[1024];
            while (is.read(buffer) != -1) {
                s = s + new String(buffer);
            }
            is.close();
        } catch (final Exception e) {
            e.printStackTrace();
        }

        // parse output
        final String[] lines = s.split("\n");
        for (String line : lines) {
            if (!line.toLowerCase(Locale.US).contains("asec")) {
                if (line.matches(reg)) {
                    String[] parts = line.split(" ");
                    for (String part : parts) {
                        if (part.startsWith("/"))
                            if (!part.toLowerCase(Locale.US).contains("vold"))
                                out.add(part);
                    }
                }
            }
        }
        return out;
    }

    public static boolean externalMemoryAvailable() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    public long getAvailableExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSizeLong();
            long availableBlocks = stat.getAvailableBlocksLong();
//            return formatSize(availableBlocks * blockSize);
            return availableBlocks * blockSize;
        } else {
            return 0;
        }
    }

    public long getTotalExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSizeLong();
            long totalBlocks = stat.getBlockCountLong();
//            return formatSize(totalBlocks * blockSize);
            return totalBlocks * blockSize;
        } else {
            return 0;
        }
    }


    public void copyFileOrDirectory(String srcDir) {

        try {
            File src = new File(srcDir);
            File dst = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                    "/DCIM/phone_repair/p_repair" + src.getName());

            if (src.isDirectory()) {

                String[] files = src.list();
                int filesLength = 0;
                if (files != null) {
                    filesLength = files.length;
                }
                for (int i = 0; i < filesLength; i++) {
                    String src1 = (new File(src, files[i]).getPath());
                    copyFileOrDirectory(src1);
                }
            } else {
                copyFile(src, dst);
                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                        Uri.fromFile(new File(dst.getAbsolutePath()))));
                Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();

            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Not Saved", Toast.LENGTH_SHORT).show();
        }
    }

    public void copyFile(File sourceFile, File destFile) throws IOException {
        if (!Objects.requireNonNull(destFile.getParentFile()).exists())
            destFile.getParentFile().mkdirs();

        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        try (FileChannel source = new FileInputStream(sourceFile).getChannel(); FileChannel destination = new FileOutputStream(destFile).getChannel()) {
            destination.transferFrom(source, 0, source.size());
        }
    }

    @SuppressLint("Recycle")
    public List<FileModel> getAllImagePaths() {
        List<FileModel> list = new ArrayList<>();
        FileModel file;
        Uri uri;
        Cursor cursor;
        int column_index_data;
        String absolutePath = null;
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String sortOrder = MediaStore.MediaColumns.DATE_MODIFIED + " DESC";
        cursor = context.getContentResolver().query(uri, null, null,
                null, sortOrder);
        assert cursor != null;
        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        while (cursor.moveToNext()) {
            absolutePath = cursor.getString(column_index_data);
            String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.TITLE));
            long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE));
//            Long size = (new File(absolutePathOfVideo)).length();
            file = new FileModel();
            file.setPath(absolutePath);
//            file.setSize(size);
//            file.setType(FileTypes.ImageType);
            if (/*isPhoto(absolutePathOfVideo) &&*/ size > 0)
                list.add(file);
        }
        return list;
    }

    public List<FileModel> getAllVideosPaths() {
        List<FileModel> list = new ArrayList<>();
        FileModel file;
        Uri uri;
        Cursor cursor;
        int column_index_data;
        String absolutePath = null;
        uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String sortOrder = MediaStore.MediaColumns.DATE_MODIFIED + " DESC";
        cursor = context.getContentResolver().query(uri, null, null,
                null, sortOrder);
        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        while (cursor.moveToNext()) {
            absolutePath = cursor.getString(column_index_data);
            String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.TITLE));
            long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE));
//            Long size = (new File(absolutePathOfVideo)).length();
            file = new FileModel();
            file.setPath(absolutePath);
//            file.setSize(size);
//            file.setType(FileTypes.ImageType);
            if (/*isPhoto(absolutePathOfVideo) &&*/ size > 0)
                list.add(file);
        }
        return list;
    }

    public List<FileModel> getAllAudiosPaths() {
        List<FileModel> list = new ArrayList<>();
        FileModel file;
        Uri uri;
        Cursor cursor;
        int column_index_data;
        String absolutePath = null;
        uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String sortOrder = MediaStore.MediaColumns.DATE_MODIFIED + " DESC";
        cursor = context.getContentResolver().query(uri, null, null,
                null, sortOrder);
        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        while (cursor.moveToNext()) {
            absolutePath = cursor.getString(column_index_data);
            String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.TITLE));
            long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE));
//            Long size = (new File(absolutePathOfVideo)).length();
            file = new FileModel();
            file.setPath(absolutePath);
            file.setName(title);
//            file.setSize(size);
//            file.setType(FileTypes.ImageType);
            if (/*isPhoto(absolutePathOfVideo) &&*/ size > 0)
                list.add(file);
        }
        return list;
    }


    public List<FileModel> getAllDocs(String path) {
        File fold = new File(path);
        List<FileModel> docList = new ArrayList<>();
        File[] mlist = fold.listFiles();
        File[] mFilelist = fold.listFiles(new AllDoFilter());
        if (mlist != null) {
            for (File f : mlist) {
                if (f.isDirectory()) {
                    List<FileModel> fList = getAllDocs(f.getAbsolutePath());
                    docList.addAll(fList);
                }
            }
        }
        if (mFilelist != null) {
            for (File f : mFilelist) {
                FileModel doc = new FileModel();
                doc.setName(f.getName());
                //                doc.setSize(f.length());
                //                doc.setType(FileTypes.DocumentType);
                doc.setPath(f.getAbsolutePath());
                if (f.length() > 0)
                    docList.add(doc);
            }
        }
        return docList;
    }


    public List<FileModel> getVideos(String path) {
        File fold = new File(path);
        List<FileModel> docList = new ArrayList<>();
        File[] mlist = fold.listFiles();
        File[] mFilelist = fold.listFiles();
        if (mlist != null) {
            for (File f : mlist) {
                if (f.isDirectory()) {
                    List<FileModel> fList = getVideos(f.getAbsolutePath());
                    docList.addAll(fList);
                }
            }
        }
        if (mFilelist != null) {
            for (File f : mFilelist) {
                FileModel doc = new FileModel();
                doc.setName(f.getName());
                //                doc.setSize(f.length());
                //                doc.setType(FileTypes.DocumentType);
                doc.setPath(f.getAbsolutePath());
                if (f.length() > 0)
                    docList.add(doc);
            }
        }
        return docList;
    }

    public List<FileModel> getImages(String path) {
        File fold = new File(path);
        List<FileModel> docList = new ArrayList<>();
        File[] mlist = fold.listFiles();
        File[] mFilelist = fold.listFiles();
        if (mlist != null) {
            for (File f : mlist) {
                if (f.isDirectory()) {
                    List<FileModel> fList = getImages(f.getAbsolutePath());
                    docList.addAll(fList);
                }
            }
        }
        if (mFilelist != null) {
            for (File f : mFilelist) {
                FileModel img = new FileModel();
                img.setName(f.getName());
                //                doc.setSize(f.length());
                //                doc.setType(FileTypes.DocumentType);
                img.setPath(f.getAbsolutePath());
                if (f.length() > 0)
                    docList.add(img);
            }
        }
        return docList;
    }


    private List<FileModel> getAllPackages(String path) {
        File fold = new File(path);
        List<FileModel> docList = new ArrayList<>();
        File[] mlist = fold.listFiles();
        File[] mFilelist = fold.listFiles(new AllPackagesFilter());
        if (mlist != null) {
            for (File f : mlist) {
                if (f.isDirectory()) {
                    List<FileModel> fList = getAllPackages(f.getAbsolutePath());
                    docList.addAll(fList);
                }
            }
            if (mFilelist != null) {
                for (File f : mFilelist) {
                    FileModel doc = new FileModel();
                    doc.setName(f.getName());
                    doc.setSize(f.length());
                    doc.setPath(f.getAbsolutePath());
                    if (f.length() > 0)
                        docList.add(doc);
                }
            }
        }
        return docList;
    }

    public LiveData<List<FileModel>> getFilesApks(String path) {
        MutableLiveData<List<FileModel>> mutableLiveData = new MutableLiveData<>();
        mutableLiveData.setValue(getAllPackages(path));
        return mutableLiveData;

    }


    public long getAppsCache(String packageName) {
        long size = 0;
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +
                "Android" + File.separator + "data" + File.separator + packageName +
                File.separator + "cache" + File.separator;

        List<File> files = getCacheFiles(new File(path)/*, packageName*//*,data*/);
        if (!files.isEmpty()) {
            for (File f : files) {
                size = f.length();
            }
        }

        return size;
    }

    public List<File> getCacheFiles(File parentDir/*, String packageName*//*,SendData sendData*/) {
        ArrayList<File> inFiles = new ArrayList<>();
        File[] files = parentDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {

                    inFiles.addAll(getCacheFiles(file));

                } else {
                    inFiles.add(file);

                }
            }
        }

        return inFiles;
    }

    public long getCache(String app) {
        long totalStorage = 0;
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = context.getPackageManager().getApplicationInfo(app, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            StorageStatsManager storageStatsManager = (StorageStatsManager)
                    context.getApplicationContext().getSystemService(Context.STORAGE_STATS_SERVICE);
            try {
                if (applicationInfo != null) {
                    totalStorage = storageStatsManager.queryStatsForUid(applicationInfo.storageUuid,
                            applicationInfo.uid).getCacheBytes();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
//            try {
//                Method method = context.getPackageManager().getClass().getMethod("getPackageSizeInfo");
//            } catch (NoSuchMethodException e) {
//                e.printStackTrace();
//            }

            totalStorage = getAppsCache(app);
        }


        return totalStorage;
    }

    public float getAllSize(String path) {
        float docSize = 0;
        File fold = new File(path);
        File[] mlist = fold.listFiles();
        File[] mFilelist = fold.listFiles();
        if (mlist != null)
            for (File f : mlist) {
                if (f.isDirectory()) {
                    getAllSize(f.getAbsolutePath());
                }
            }
        if (mlist != null)
            if (mFilelist != null) {
                for (File f : mFilelist) {
                    docSize = docSize + f.length();
                }
            }
        return docSize;
    }

    public List<String> getAllCacheFolders(String path) {
        List<String> docSize = new ArrayList<>();
        File fold = new File(path);
        File[] mlist = fold.listFiles();
        File[] mFilelist = fold.listFiles();
        if (mlist != null)
            for (File f : mlist) {
                if (f.isDirectory()) {
                    docSize.add(f.getAbsolutePath());
                    getAllCacheFolders(f.getAbsolutePath());
                }
            }

//        if (mFilelist != null) {
//            for (File f : mFilelist) {
//                docSize.add(f.getAbsolutePath());
//            }
//        }
        return docSize;
    }


    public float getAllDocSize(String path) {

        File fold = new File(path);
        File[] mlist = fold.listFiles();
        File[] mFilelist = fold.listFiles(new AllDoFilter());
        if (mlist != null) {
            for (File f : mlist) {
                if (f.isDirectory()) {
                    getAllDocSize(f.getAbsolutePath());
                }
            }
        }
        if (mFilelist != null) {
            for (File f : mFilelist) {
                docSize = docSize + f.length();
            }
        }
        return docSize;
    }

    public float getAudioSize(String path) {

        File fold = new File(path);
        File[] mlist = fold.listFiles();
        File[] mFilelist = fold.listFiles(new AllAudioFilter());
        if (mlist != null) {
            for (File f : mlist) {
                if (f.isDirectory()) {
                    getAllDocSize(f.getAbsolutePath());
                }
            }
        }
        if (mFilelist != null) {
            for (File f : mFilelist) {
                audioSize = audioSize + f.length();
            }
        }
        return audioSize;
    }

    public float getAllPkgsSize(String path) {
        File fold = new File(path);
        File[] mlist = fold.listFiles();
        File[] mFilelist = fold.listFiles(new AllPackagesFilter());
        if (mlist != null) {
            for (File f : mlist) {
                if (f.isDirectory()) {
                    getAllPkgsSize(f.getAbsolutePath());
                }
            }
        }

        if (mFilelist != null) {
            for (File f : mFilelist) {
                size = size + f.length();
            }
        }
        return size;
    }


    // return images audio videos size
    @SuppressLint("Recycle")
    public float getAllIAAsSize(String forWhat) {
        Uri uri = null;
        Cursor cursor = null;
        String[] projection = new String[0];
        float size = 0;
        if (forWhat.matches("videos")) {
            uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            projection = new String[]{
                    MediaStore.Video.Media._ID,
                    MediaStore.Video.Media.DISPLAY_NAME,
                    MediaStore.Video.Media.SIZE
            };
        } else if (forWhat.matches("images")) {
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            projection = new String[]{
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.SIZE
            };

        } else if (forWhat.matches("audios")) {
            uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            projection = new String[]{
                    MediaStore.Audio.Media._ID,
                    MediaStore.Audio.Media.DISPLAY_NAME,
                    MediaStore.Audio.Media.SIZE
            };

        }

        String sortOrder = MediaStore.MediaColumns.DATE_MODIFIED + " DESC";
        if (uri != null) {
            cursor = context.getContentResolver().query(uri, projection, null,
                    null, sortOrder);
        }
        assert cursor != null;
        while (cursor.moveToNext()) {
            size = size + cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE));

        }
        return size;
    }


    //delete file taken from media store
    public void scanaddedFile(String path) {
        try {
            MediaScannerConnection.scanFile(context, new String[]{path},
                    null, (path1, uri) -> context.getContentResolver()
                            .delete(uri, null, null));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void deleteFile(File file) {

        file.delete();
    }


    public static class AllDoFilter implements FileFilter {
        @Override
        public boolean accept(File pathname) {
            String path = pathname.getPath();
            return (path.endsWith(".ods")
                    || path.endsWith(".xls")
                    || path.endsWith(".xlsx")
                    || path.endsWith(".doc")
                    || path.endsWith(".odt")
                    || path.endsWith(".docx")
                    || path.endsWith(".pps")
                    || path.endsWith(".pptx")
                    || path.endsWith(".ppt")
                    || path.endsWith(".PDF")
                    || path.endsWith(".pdf")
                    || path.endsWith(".txt")
                    || path.endsWith(".ziip")
                    || path.endsWith(".7z")
                    || path.endsWith(".rar")
                    || path.endsWith(".rpm")
                    || path.endsWith(".tar.gz")
                    || path.endsWith(".z")
                    || path.endsWith(".zip"));
        }
    }

    public static class AllImgFilter implements FileFilter {
        @Override
        public boolean accept(File pathname) {
            String path = pathname.getPath();
            return (path.endsWith(".jpeg") ||
                    path.endsWith(".jpg") ||
                    path.endsWith(".png"));
        }
    }

    public static class AllAudioFilter implements FileFilter {
        @Override
        public boolean accept(File pathname) {
            String path = pathname.getPath();
            return (path.endsWith(".mp3") ||
                    path.endsWith(".opus") ||
                    path.endsWith(".m4a") ||
                    path.endsWith(".amr") ||
                    path.endsWith(".mpa") ||
                    path.endsWith(".mid") ||
                    path.endsWith(".ogg") ||
                    path.endsWith(".wav") ||
                    path.endsWith(".wma") ||
                    path.endsWith(".wpl") ||
                    path.endsWith(".cda") ||
                    path.endsWith(".aif"));
        }
    }

    public static class AllPackagesFilter implements FileFilter {
        @Override
        public boolean accept(File pathname) {
            String path = pathname.getPath();
            return (path.endsWith(".apk"));
        }
    }

    @SuppressLint("DefaultLocale")
    public String getDataSizeWithPrefix(float size) {
        String sizePrefix = "B";
        float finalSize = size;
        if (size >= 1024) {
            float sizeKb = size / 1024;
            sizePrefix = "KB";
            finalSize = sizeKb;
            if (sizeKb >= 1024) {
                float sizeMB = sizeKb / 1024;
                sizePrefix = "MB";
                finalSize = sizeMB;
                if (sizeMB >= 1024) {
                    float sizeGb = sizeMB / 1024;
                    sizePrefix = "GB";
                    finalSize = sizeGb;
                }
            }
        }
        return String.format("%.2f", finalSize) + "" + sizePrefix;
    }

    @SuppressLint("DefaultLocale")
    public String getPrefix(float size) {
        String sizePrefix = "B";
        if (size >= 1024) {
            float sizeKb = size / 1024;
            sizePrefix = "KB";
            if (sizeKb >= 1024) {
                float sizeMB = sizeKb / 1024;
                sizePrefix = "MB";
                if (sizeMB >= 1024) {
                    float sizeGb = sizeMB / 1024;
                    sizePrefix = "GB";
                }
            }
        }
        return sizePrefix;
    }

    public String getPrefixToMb(float size) {
        String sizePrefix = "B";
        if (size >= 1024) {
            float sizeKb = size / 1024;
            sizePrefix = "KB";
            if (sizeKb >= 1024) {
                float sizeMB = sizeKb / 1024;
                sizePrefix = "MB";
            }
        }
        return sizePrefix;
    }

    public float getDataSizeFloat(float size) {
        float finalSize = size;
        if (size >= 1024) {
            float sizeKb = size / 1024;
            finalSize = sizeKb;
            if (sizeKb >= 1024) {
                float sizeMB = sizeKb / 1024;
                finalSize = sizeMB;
                if (sizeMB >= 1024) {
                    float sizeGb = sizeMB / 1024;
                    finalSize = sizeGb;

                }
            }
        }
        return finalSize;
    }

    public float cpuTemperature() {


        Process process;
        try {
            process = Runtime.getRuntime().exec("cat sys/class/thermal/thermal_zone0/temp");
            process.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();
            if (line != null) {
                float temp = Float.parseFloat(line);
                return getCelsius(temp / 1000.0f);
            } else {
                return getCelsius(51.0f);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return getCelsius(0.0f);
        }

    }

    public float getCelsius(float fahren) {
        return (fahren - 32) / (1.8f);

    }

    public float getFahrenheit(float cel) {
        return (cel * 1.8f) + 32;

    }


    public float getCalculatedDataSizeMB(float size) {
        float sizeBytes = size;
        float finalSize = size;
        if (sizeBytes >= 1024) {
            float sizeKb = sizeBytes / 1024;
            finalSize = sizeKb;
            if (sizeKb >= 1024) {
                float sizeMB = sizeKb / 1024;
                finalSize = sizeMB;
            }
        }
        return finalSize;
    }

    public int randomValue(int min, int max) {

        return new Random().nextInt((max - min) + 1) + min;
    }

    public int memoryFun(int total) {
        if (total <= 2) {
            return 2;
        } else if (total > 2 && total < 4) {
            return 4;

        } else if (total > 4 && total < 8) {
            return 8;
        } else if (total > 10 && total < 16) {

            return 16;

        } else if (total > 16 && total < 32) {
            return 32;

        } else if (total > 32 && total < 64) {

            return 64;

        } else if (total > 64 && total < 128) {
            return 128;


        } else if (total > 128 && total < 256) {

            return 256;

        } else if (total > 256 && total < 512) {

            return 512;

        } else if (total > 512 && total < 1024) {
            return 1024;

        }
        return 0;

    }

    public void setViberate() {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(500);
        }

    }


}