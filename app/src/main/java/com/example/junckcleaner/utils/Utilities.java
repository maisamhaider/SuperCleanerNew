package com.example.junckcleaner.utils;

import com.example.junckcleaner.models.FileModel;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

public class Utilities {


    public List<FileModel> getAllPackages(String path) {
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
                    doc.setPath(f.getAbsolutePath());
                    if (f.length() > 0)
                        docList.add(doc);
                }
            }
        }
        return docList;
    }

    public static class AllPackagesFilter implements FileFilter {
        @Override
        public boolean accept(File pathname) {
            String path = pathname.getPath();
            return (path.endsWith(".apk"));
        }
    }

    public float getCalculatedDataSizeFloat(float size) {
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
}
