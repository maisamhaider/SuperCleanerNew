package com.example.junckcleaner.duplicatenew.models;


import android.annotation.SuppressLint;
import android.graphics.Typeface;

@SuppressLint({"UseSparseArrays", "Range"})
public class GlobalMethods {
    public static Typeface type;
    public FileDetails fileDetails = new FileDetails();


    public static long convertCRC64(String str) {
        char[] genrateCPlusChar = genrateCPlusChar(str.toLowerCase());
        return new CRC641().update(genrateCPlusChar, genrateCPlusChar.length);
    }


    public static char[] genrateCPlusChar(String str) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            sb.append(str.charAt(i));
            sb.append("\u0000");
        }
        return sb.toString().toCharArray();
    }




}
