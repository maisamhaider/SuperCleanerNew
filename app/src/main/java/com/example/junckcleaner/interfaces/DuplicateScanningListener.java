package com.example.junckcleaner.interfaces;


import com.example.junckcleaner.duplicatenew.models.FileDetails;

import java.util.ArrayList;
import java.util.List;

public interface DuplicateScanningListener {
    void checkScanning();
    void publishProgress(String... strArr);
    void publishProgress(List<ArrayList<FileDetails>> duplicatesList);
}
