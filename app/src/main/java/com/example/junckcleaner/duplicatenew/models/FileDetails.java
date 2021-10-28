package com.example.junckcleaner.duplicatenew.models;

import java.io.Serializable;

public class FileDetails implements Serializable, Comparable<FileDetails> {
    private static final long serialVersionUID = 3311332759495163383L;
    String filePath;
    String fileName;
    String sizeString;
    long size;
    boolean check = true;


    public int compareTo(FileDetails fileDetails) {
        long j2 = this.size;
        long j3 = fileDetails.size;
        if (j2 > j3) {
            return -1;
        }
        if (j2 < j3) {
            return 1;
        }
        long doubleToLongBits = Double.doubleToLongBits((double) j2);
        long doubleToLongBits2 = Double.doubleToLongBits((double) fileDetails.size);
        if (doubleToLongBits == doubleToLongBits2) {
            return 0;
        }
        return doubleToLongBits > doubleToLongBits2 ? -1 : 1;
    }


    public String getFileName() {
        return this.fileName;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public long getFileSize() {
        return this.size;
    }

    public String getFileSizeStr() {
        return this.sizeString;
    }

    public boolean isChecked() {
        return this.check;
    }

    public void setChecked(boolean check) {
        this.check = check;
    }

    public void setFileName(String str) {
        this.fileName = str;
    }

    public void setFilePath(String str) {
        this.filePath = str;
    }


    public void setFileSize(long j2) {
        this.size = j2;
    }

    public void setFileSizeStr(String str) {
        this.sizeString = str;
    }


}
