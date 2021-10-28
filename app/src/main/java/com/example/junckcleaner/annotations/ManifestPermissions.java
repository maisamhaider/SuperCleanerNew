package com.example.junckcleaner.annotations;

import android.Manifest;

public @interface ManifestPermissions {

    String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.CAMERA};

}
