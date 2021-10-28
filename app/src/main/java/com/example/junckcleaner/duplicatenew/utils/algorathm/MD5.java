package com.example.junckcleaner.duplicatenew.utils.algorathm;

import android.content.Context;

import com.example.junckcleaner.duplicatenew.utils.DuplicatePreferences;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {

    public String fileToMD5(String filePath, Context context) {
        try {
            String output;
            MessageDigest digest = MessageDigest.getInstance("MD5");
            InputStream is = new FileInputStream(filePath);
            byte[] buffer = new byte[8192];
            while (true) {
                int read = is.read(buffer);
                if (read <= 0 || DuplicatePreferences.isScanningStopped(context)) {
                    output = String.format("%32s", new Object[]{new BigInteger(1, digest.digest()).toString(16)})
                            .replace(' ', '0');
                    is.close();
                } else {
                    digest.update(buffer, 0, read);
                }

                output = String.format("%32s", new Object[]{new BigInteger(1, digest.digest()).toString(16)})
                        .replace(' ', '0');
                is.close();
                return output;
            }
        } catch (NoSuchAlgorithmException e) {
            return null;
        } catch (FileNotFoundException e2) {
            return null;
        } catch (IOException e3) {
            return null;
        }
    }

}